package com.dylan.combat.reactions

/**
 * A Reactor simply reacts to a message of type `A` by responding with a message of type `B`
 */
trait Reactor[A, B] {
	//	def react(msg: A): Option[B]
	def react: PartialFunction[A, B]
}

trait CompositeReactor[A, B] extends Reactor[A, B] {
	protected var reactors: List[Reactor[A, B]] = Nil

	def add(reactor: Reactor[A, B]) = reactors = reactor :: reactors
	def remove(reactor: Reactor[A, B]) = reactors = reactors.remove(_ == reactor)

	//	def react(msg: A) = reactors.foldLeft[Option[B]](None)((accum, next) => accum.orElse(next.react(msg)))
	def react = reactors.foldLeft[PartialFunction[A, B]](Map())((accum, next) => accum.orElse(next.react))
}