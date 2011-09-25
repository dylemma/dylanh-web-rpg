package com.dylan.combat

import com.dylan.data.util._ //for list pimping

class CombatController(field: CombatField) extends EffectStackExecutor {

	def executeTurn = {
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
			for ((skill, target) <- combattant.choiceProvider.chooseSkill) {
				if (skill.isValid(combattant, target)) executeEffects(skill.use(combattant, target, field))
				else invalidSkillChoice(skill, combattant, target)
			}
		}
	}

	def invalidSkillChoice(skill: Skill, user: Combattant, target: Target) = {}
	def messageRecieved(source: Target, message: String) = {}
	def effectApplied(target: Target, effect: Effect) = {}
}