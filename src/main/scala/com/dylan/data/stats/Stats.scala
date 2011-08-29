package com.dylan.data.stats

import com.dylan.data.meta.Description
import scala.collection.SortedMap

/**
 * DepRepValue is shorthand for Depletable, Replenishable Value. It represents a quantity like Health or Mana.
 */
case class DepRepValue(current: Double, max: Double) {
	def modifyMax(modify: (Double) => Double) = {
		val ratio = current / max
		val newMax = modify(max)
		val newCurr = newMax * ratio
		DepRepValue(newCurr, newMax)
	}
	def withMax(mx: Double) = modifyMax(_ => mx)
	def replenishCurrent(amount: Double) = {
		if (current >= max) this
		else DepRepValue((current + amount).min(max), max)
	}
	def depleteCurrent(amount: Double) = DepRepValue((current - amount).max(0), max)
	def modifyCurrent(modify: (Double) => Double) = DepRepValue(modify(current), max)
}

object StatModMeta {
	/**
	 * The Affiliation of a StatMod decides whether it is hostile, friendly, or neutral to its target
	 */
	object Affiliation extends Enumeration {
		val Hostile, Neutral, Friendly = Value
	}
	type Affiliation = Affiliation.Value

	/**
	 * The Timeframe of a StatMod tells whether it should exist out of battle. Permanent mods represent
	 * things like armor bonuses and per-level bonuses. Temporary mods represent buffs within battle.
	 */
	object Timeframe extends Enumeration {
		val Temporary, Permanent = Value
	}
	type Timeframe = Timeframe.Value

	trait Bonus extends StatModMeta {
		val affiliation = Affiliation.Friendly
		val timeframe = Timeframe.Permanent
	}

	trait Buff extends StatModMeta {
		val affiliation = Affiliation.Friendly
		val timeframe = Timeframe.Temporary
	}

	//TODO: Permanent/Hostile

	trait Debuff extends StatModMeta {
		val affiliation = Affiliation.Hostile
		val timeframe = Timeframe.Temporary
	}

	//TODO: Permanent/Neutral
	//TODO: Temporary/Neutral
}

/**
 * Trait for describing how a StatMod works. It includes the priority, affiliation, and timeframe of the mod.
 */
trait StatModMeta {
	import StatModMeta._

	/**
	 * Stat Mods are stored in prioritized order. A lower priority number actually means a higher priority
	 * (e.g. priority #1).
	 */
	val priority: Int
	val affiliation: Affiliation
	val timeframe: Timeframe

	//convenience methods for Affiliation
	def isHostile = affiliation == Affiliation.Hostile
	def isNeutral = affiliation == Affiliation.Neutral
	def isFriendly = affiliation == Affiliation.Friendly

	//convenience methods for Timeframe
	def isTemporary = timeframe == Timeframe.Temporary
	def isPermanent = timeframe == Timeframe.Permanent
}

trait StatMod[A] extends StatModMeta with Description with Criterea[(A) => A] {
	def modify(value: A): A
	def initialCritereaValue = modify
}

/**
 * A StatMod that depends on an external value to fully form its modifier function. An example
 * of this could be a bonus that is added per player level, where the player level is the external
 * value.
 *
 * @param getModifierFunc - A function that takes in the external `criterea` and returns the
 * 							resulting Stat Modification function
 * @param dependency - A Criterea wrapper for the external value
 */
abstract class DependentStatMod[A, T](val getModifierFunc: (T) => (A) => A, val dependency: Criterea[T]) extends StatMod[A] with Dependent[T] {
	dependency.addDependent(this)
	value = getModifierFunc(dependency.value)
	def modify(i: A) = value(i)
	def observeCritereaChanged(oldValue: T, newValue: T) = {
		value = getModifierFunc(newValue) //should fire criterea modified events
	}
}

abstract class Stat[A](val baseValue: Criterea[A]) extends Criterea[A] with Description {
	implicit object StatModOrdering extends Ordering[StatMod[A]] {
		def compare(x: StatMod[A], y: StatMod[A]) = x.priority - y.priority match {
			case 0 => x.## - y.## //differentiate by hash codes or else same-priority mods will be treated as "equal"
			case i => i //normal case: accept the priority difference
		}
	}

	protected lazy val initialCritereaValue = baseValue.value // initialize criterea value to the base value
	protected var modifications = SortedMap[StatMod[A], Dependent[(A) => A]]()(StatModOrdering) // hold the modifications in order of priority
	protected def calculateStatValue = modifications.keys.foldLeft(baseValue.value)((accum, next) => next.modify(accum))

	// create a dependent that will listen for changes to the base value
	object BaseValueDependent extends Dependent[A] {
		override def observeCritereaChanged(oldValue: A, newValue: A) = value = calculateStatValue
	}
	// register the dependent
	baseValue.addDependent(BaseValueDependent)

	// register instances of this class with each StatMod that is added to the `modifications`
	// make sure to remove the instances when their corresponding StatMod is removed 
	private class StatModDependent(mod: StatMod[A]) extends Dependent[(A) => A] {
		def observeCritereaChanged(oldValue: (A) => A, newValue: (A) => A) = value = calculateStatValue
	}

	def +=(mod: StatMod[A]) = {
		if (!modifications.contains(mod)) {
			//create and add a dependent that will cause updates when the mod changes
			val modDependent = new StatModDependent(mod)
			mod.addDependent(modDependent)
			modifications = modifications + (mod -> modDependent)
			//update the criterea value, since it has most likely changed
			value = calculateStatValue
		}
		this
	}

	def -=(mod: StatMod[A]) = {
		if (modifications.contains(mod)) {
			//remove the dependent that was added to the mod
			val modDependent = modifications(mod)
			mod.removeDependent(modDependent)
			modifications = modifications - mod
			//update the criterea value since it has most likely changed
			value = calculateStatValue
		}
		this
	}

	override def toString = "Stat[%s = %s]".format(name, value)
}

abstract class DepRepStat(private val maxValueCriterea: Criterea[Double]) extends Criterea[DepRepValue] with Description {
	protected lazy val initialCritereaValue = DepRepValue(maxValueCriterea.value, maxValueCriterea.value)

	object MaxValueChangeObserver extends Dependent[Double] {
		def observeCritereaChanged(oldValue: Double, newValue: Double) = value = value.withMax(newValue)
	}
	maxValueCriterea.addDependent(MaxValueChangeObserver)

	def +=(amount: Double) = { value = value.replenishCurrent(amount); this }
	def -=(amount: Double) = { value = value.depleteCurrent(amount); this }

	def currentValue = value.current
	def maxValue = value.max

	override def toString = "Stat[%s = %s / %s]".format(name, currentValue, maxValue)
}

