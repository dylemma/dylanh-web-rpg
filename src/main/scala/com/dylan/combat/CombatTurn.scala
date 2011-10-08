package com.dylan.combat

trait CombatTurn {
	def priority: Int
	def speed: Double
	def combattant: Combattant
}

object CombatTurn {
	implicit object TurnOrdering extends Ordering[CombatTurn] {
		def compare(left: CombatTurn, right: CombatTurn) = (left.priority - right.priority) match {
			case 0 => (right.speed - left.speed) match {
				case 0 => left.## - right.##
				case i => i.signum
			}
			case i => i
		}
	}
}

trait CombatTurnProvider {
	def turns: List[CombatTurn]
}

trait SkillChoiceProvider {
	def chooseSkill(field: CombatField): Option[(Skill, Target)]
}