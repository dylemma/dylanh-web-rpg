package com.dylan.combat

trait Combattant extends Target {
	def skills: List[Skill]
	def turnProvider: CombatTurnProvider
	def choiceProvider: SkillChoiceProvider
}