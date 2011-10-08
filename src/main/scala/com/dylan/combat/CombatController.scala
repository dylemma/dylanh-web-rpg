package com.dylan.combat

import com.dylan.data.util._
import scala.collection.mutable.Publisher
import scala.collection.mutable.Stack

/**
 * A wrapper for a combat controller, which provides methods that are safe
 * for controlled classes to use (this is an effort to prevent API abuse).
 */
class CombatControllerFacade(controller: CombatController) {
	def executeEffect(effect: TargettedEffect) = {
		controller.executeEffects(List(effect))
	}
}

class CombatController(field: CombatField) extends EffectStackExecutor with CombatEventPublisher[CombatControllerFacade] {

	protected lazy val eventContext = new CombatControllerFacade(this)

	def executeRound = {
		//get list of combattants
		val combattants = field.combattants
		//ask combattants for their turn requests (sorted by tier, then speed, generally)
		var turns = combattants.iterator.map(_.turnProvider.turns).flatten.toList
		//turn the requests into a priority queue
		//while(the queue isn't empty)
		while (!turns.isEmpty) {
			val (turn, remainingTurns) = turns.removeMin
			turns = remainingTurns
			//  val turn = queue.pop
			//  (execute the turn)
			val combattant = turn.combattant
			//  use the combattants turn strategy (e.g. PC, NPC, forced action) to get the combattant's Option[skill choice]
			for ((skill, target) <- combattant.choiceProvider.chooseSkill(field)) {
				if (skill.isValid(combattant, target, field)) {
					executeEffects(skill.use(combattant, target, field))
					publishSkillUsed(skill, combattant, target)
				} else invalidSkillChoice(skill, combattant, target)
			}
		}
	}

	protected def invalidSkillChoice(skill: Skill, user: Combattant, target: Target) = {}
	protected def messageRecieved(source: Target, message: String) = {}

	/**
	 * Handle an Effect being applied to a Target.
	 */
	protected def effectApplied(target: Target, effect: Effect) = effect match {
		case d @ Damage(amount, kind, source, direct) => {
			target.takeDamage(d)
			publishDamageApplied(target, d)
		}
		case h @ Heal(amount, source, direct) => {
			target.takeHeal(h)
			publishHealApplied(target, h)
		}
		case a @ AddRecurringEffect(source, effect) => {
			target.addRecurringEffect(a)
			publishEffectAdded(target, a)
		}
		case r @ RemoveRecurringEffect(source, effect) => {
			target.removeRecurringEffect(r)
			publishEffectRemoved(target, r)
		}
	}

}