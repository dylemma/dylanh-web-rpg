package com.dylan.combat

trait Skill {
	def use(user: Combattant, target: Target, field: CombatField): List[TargettedEffect]
	def validTargets(user: Combattant, field: CombatField): List[Target]
	def isValid(user: Combattant, target: Target): Boolean
}

