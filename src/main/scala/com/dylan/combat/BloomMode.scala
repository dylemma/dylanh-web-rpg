package com.dylan.combat

/**
 * A `BloomMode` instance controls where and how much a skill shows its effects, based on
 * the location that the skill was used (`origin`), the `target` Position, and the `environment`
 * in which the skill was used.
 */
abstract class BloomMode {
	/**
	 * Returns a map of Targets affected by the skill, where a Position maps to a "percentage
	 * effectiveness" value (from 0.0 to 1.0)
	 */
	def areaOfEffect(origin: CombatPosition, target: Target, environment: CombatField): Map[Target, Double]
}