package com.dylan.combat

/**
 * An EffectReactor's job is simply to react to Effects.
 */
trait EffectReactor {
	def priority: Float
	def react(effect: Effect): List[EffectResponse]
}

/**
 * Companion object for the EffectReactor. Defines an ordering for EffectReactors
 * based on their priority values.
 */
private object EffectReactor {
	implicit object EffectReactorOrdering extends Ordering[EffectReactor] {
		def compare(left: EffectReactor, right: EffectReactor): Int =
			(left.priority - right.priority) match {
				case 0 => left.## - right.##
				case i => i.signum
			}
	}
}

/**
 * Trait to manage an ordered list of EffectReactors. Provides getter, adder, and remover methods.
 */
trait HasReactors {
	import EffectReactor._ //for reactor ordering

	private var reactorsList: List[EffectReactor] = Nil

	def reactors = reactorsList

	def addReactor(reactor: EffectReactor) = {
		reactorsList = orderedInsert(reactor, reactorsList)
	}

	def removeReactor(reactor: EffectReactor) = {
		reactorsList = reactorsList.remove(_ == reactor)
	}

	private def orderedInsert[T](elem: T, list: List[T])(implicit ordering: Ordering[T]): List[T] = list match {
		case head :: tail => {
			import ordering._
			if (elem < head) elem :: head :: tail
			else head :: orderedInsert(elem, tail)
		}
		case Nil => elem :: Nil
	}
}