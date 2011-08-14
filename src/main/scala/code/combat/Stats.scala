/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.battles

import com.dylan.meta.Description
import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber
import scala.xml.Text

object StatModAlignment extends Enumeration{
	val Hostile, Neutral, Friendly = Value
}

object StatModDuration extends Enumeration{
	val Permanent, Temporary = Value 
}

object StatModSubject extends Enumeration{
	val Current, Max = Value
}

case class StatModCritereaChanged(message:String)

abstract class StatMod[StatType]
(val priority:Int, val alignment:StatModAlignment.Value, val duration:StatModDuration.Value, val subject:StatModSubject.Value)
extends Publisher[StatModCritereaChanged] with Description{
	def modifiedValue(value:StatType): StatType
	def modificationCritereaChanged(message:String) = publish(StatModCritereaChanged(message))
}

object StatMod{
	import StatModAlignment._
	import StatModDuration._
	import StatModSubject._
	
	implicit object StatModOrdering extends Ordering[StatMod[_]]{
		def compare(x:StatMod[_], y:StatMod[_]) = x.priority - y.priority
	}

	def isPermanent(mod:StatMod[_]) = mod.duration == Permanent
	def targetsCurrent(mod:StatMod[_]) = mod.subject == Current
	def targetsMax(mod:StatMod[_]) = mod.subject == Max

	trait Amount{
		protected def amount:Double
	}
	trait ValueModifierAddDouble extends Amount{
		def modifiedValue(value:Double) = value + amount
	}
	trait ValueModifierSubtractDouble extends Amount{
		def modifiedValue(value:Double) = value - amount
	}
	trait ValueModifierMultDouble extends Amount{
		def modifiedValue(value:Double) = value * amount
	}

	def Damage(amt:Double) = new StatMod[Double](1, Hostile, Permanent, Current) with ValueModifierSubtractDouble{
		val amount = amt
		def name = "Damage"
		def description = Text(amount + " Damage")
	}

	def Restore(amt:Double) = new StatMod[Double](1, Friendly, Permanent, Current) with ValueModifierAddDouble{
		val amount = amt
		def name = "Restore"
		def description = Text("Restore " + amount + " points")
	}

	def AddativeBonus(p:Int, amt:Double) = new StatMod[Double](p, Friendly, Permanent, Max) with ValueModifierAddDouble{
		val amount = amt
		def name = "Bonus"
		def description = Text("Adds " + amount + " points")
	}

	def MultiplicativeBonus(p:Int, amt:Double) = new StatMod[Double](p, Friendly, Permanent, Max) with ValueModifierMultDouble{
		val amount = amt
		def name = "Bonus"
		def description = Text("Multiplies by " + amount + " points")
	}

	def AddativeBuff(p:Int, amt:Double) = new StatMod[Double](p, Friendly, Temporary, Max) with ValueModifierAddDouble{
		val amount = amt
		def name = "Buff"
		def description = Text("A Buff of " + amount + " points")
	}

	def MultiplicativeBuff(p:Int, amt:Double) = new StatMod[Double](p, Friendly, Temporary, Max) with ValueModifierMultDouble{
		val amount = amt
		def name = "Buff"
		def description = Text(amount + "x Multiplier")
	}

	def SubtractiveDebuff(p:Int, amt:Double) = new StatMod[Double](p, Hostile, Temporary, Max) with ValueModifierSubtractDouble{
		val amount = amt
		def name = "Debuff"
		def description = Text("A Debuff of " + amount + " points")
	}

	def MultiplicativeDebuff(p:Int, amt:Double) = new StatMod[Double](p, Hostile, Temporary, Max) with ValueModifierMultDouble{
		val amount = amt
		def name = "Debuff"
		def description = Text(amount + "x Multiplier")
	}
}

/**
 * Base operations for stats that are depletable and modifiable. A Stat is composed of a `current` and `max` value, along with
 * any number of modifications to those values. Modifications are applied to either the `current` or `max` value, on a `temporary`
 * or `permanent` basis. "Permanent" mods represent things like armor bonuses and per-level bonuses; they persist outside of battle.
 * "Temporary" mods are buffs and debuffs, which are meant to be removed at the end of a battle. Permanent mods targetting the Current
 * value are a special case: the effects are applied immediately. This represents things like hp damage and healing. Temporary 
 */
abstract class Stat(baseValue: =>Double) extends Subscriber[StatModCritereaChanged, StatMod[Double]] with Description{
	import StatMod._
	
	override def toString = "Stat[" + name + ": " + current + " / " + max + "]"

	//-----Internal State Variables-----
	private var m_modifications:List[StatMod[Double]] = Nil
	private var m_baseCached = baseValue
	private var m_current = baseValue
	private var m_max = baseValue

	def modifications = m_modifications
	/**
	 * Setter for the modifications list. Clients should not be able to interact with the list directly.
	 * After the usual change, the max and current values are recalculated
	 */
	private def modifications_= (list:List[StatMod[Double]]) = {
		m_modifications = list //change the list

		//side effects: update the max value, then update the current value according to the ratio
		updateValues
	}

	private def checkCachedBase = {
		if(m_baseCached != baseValue){
			m_baseCached = baseValue
			updateValues
		}
	}

	def notify(pub:StatMod[Double], event: StatModCritereaChanged) = updateValues

	private def updateValues = {
		val newMax = modifications.filter(StatMod.targetsMax).foldLeft(baseValue)((accum,mod) => mod.modifiedValue(accum))

		val ratio = m_current / m_max
		m_max = newMax
		m_current = m_max * ratio
	}

	//Special case for "Temporary" mods to the `current` value. They get applied after the real value is calculated
	def current = {
		checkCachedBase
		modifications.filter(StatMod.targetsCurrent).foldLeft(m_current)((accum,mod) => mod.modifiedValue(accum))
	}
	def max = {
		checkCachedBase
		m_max
	}

	private def modifyCurrent(mod: (Double)=>Double) = m_current = mod(m_current).min(max)

	/**
	 * Removes the `mod` from the `modifications` list, then updates the max and current values accordingly
	 */
	private def removeModification(mod:StatMod[Double]) = modifications = modifications.filterNot(_==mod)

	/**
	 * Adds the `mod` to the `modifications` list in the correct place (preserving priority order) then
	 * updates the max and current values accordingly
	 */
	private def addModification(mod: StatMod[Double])(implicit ord:Ordering[StatMod[_]]) = {
		def insert(list:List[StatMod[Double]]):List[StatMod[Double]] = list match {
			case Nil => List(mod)
			case x :: xs if (ord.compare(mod, x) < 0) => mod :: x :: xs
			case x :: xs => x :: insert(xs)
		}
		mod.subscribe(this.asInstanceOf[mod.Sub])
		modifications = insert(modifications)
	}

	/**
	 * Adds a modification to this stat. Modifications to the `current` state of this stat will be applied 
	 * immediately, then discarded. Modifications to the `max` state of this stat will be added to a list
	 * of modifications for future removal.
	 */
	def += (mod: StatMod[Double]) = {
		if(StatMod.targetsCurrent(mod) && StatMod.isPermanent(mod)) modifyCurrent(mod.modifiedValue _)
		else addModification(mod)
		this
	}

	/**
	 * Removes the `mod` from the modifications list, but only if it was in there previously.
	 */
	def -= (mod: StatMod[Double]) = {
		mod.removeSubscription(this.asInstanceOf[mod.Sub])
		removeModification(mod); 
		this
	}

	/**
	 * Removes all modifications that match the given `predicate`. Useful for removing all `Temporary` or `Hostile` modifications.
	 */
	def --= (predicate:(StatMod[Double])=>Boolean) = {
		val remaining = modifications.filterNot(predicate)
		val toRemove = modifications.filter(predicate)
		for(mod<-toRemove) mod.removeSubscription(this.asInstanceOf[mod.Sub])
		modifications = remaining
		this
	}
}

object DerivedStat {
	private def makeDescribedStat(base: =>Double)(descriptor:Description) = new Stat(base) {
		def name = descriptor.name
		def description = descriptor.description
	}

	def apply(stat:Stat)(derive:(Stat)=>Double)(descriptor:Description) = makeDescribedStat(derive(stat))(descriptor)
	def apply(stat1:Stat, stat2:Stat)(derive:(Stat,Stat)=>Double)(descriptor:Description) = makeDescribedStat(derive(stat1,stat2))(descriptor)
	def apply(stat1:Stat, stat2:Stat, stat3:Stat)(derive:(Stat,Stat,Stat)=>Double)(descriptor:Description) = makeDescribedStat(derive(stat1, stat2, stat3))(descriptor)
}
	