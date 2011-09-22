package com.dylan.combat

/**
 * A Target is able to react to Effects when they are applied to it.
 */
trait Target extends HasReactors {
	def react(effect: Effect) = {
		reactWithReactors(effect, reactors)
	}

	/**
	 * Send the effect through the list of reactors. Each reactor may produce any number of responses.
	 * If the response is an Acceptance of some effect (maybe a modified version of the original one),
	 * that effect is passed down to the other reactors via recursion. Otherwise, (StackPush and Message reactions)
	 * the reaction is returned in the result list. Base case (no reactors) is to accept the effect as-is.
	 */
	private def reactWithReactors(effect: Effect, reactors: List[EffectReactor]): List[EffectResponse] = reactors match {
		//get the head's reaction(s)
		case head :: tail => {
			val reactions = head.react(effect)
			if (reactions.isEmpty) reactWithReactors(effect, tail)
			else reactions.map((r) => r match {
				//if the head accepted the effect, see what the next reactors will do
				case r @ Acceptance(e) => reactWithReactors(e, tail)
				//if the head generated a StackPush or Message, let it return now
				case r => r :: Nil
			}).flatten
		}
		//no reactor = no reaction
		case Nil => Acceptance(effect) :: Nil
	}
}

/**
 * A composition of one Target and one Effect. The existance of an instance of this class means that the
 * effect is destined to be applied to the target. This class should not handle that responsibility, i.e.
 * a TargettedEffect instance is just a messenger.
 */
class TargettedEffect(val target: Target, val effect: Effect)