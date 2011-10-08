package com.dylan.combat

trait Skill {
	/**
	 * The `user` uses this skill on the `target` (in the `field`), causing
	 * any number of Effects to any number of Targets.
	 */
	def use(user: Combattant, target: Target, field: CombatField): List[TargettedEffect]

	/**
	 * Get a list of Targets that the `user` may use while in the `field`.
	 */
	def validTargets(user: Combattant, field: CombatField): List[Target]

	/**
	 * Checks if the `target` is a valid target if the `user` would use this
	 * skill within the `field`.
	 */
	def isValid(user: Combattant, target: Target, field: CombatField): Boolean
}

