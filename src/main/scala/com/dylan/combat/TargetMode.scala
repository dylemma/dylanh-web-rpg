package com.dylan.combat

abstract class TargetMode {

	/**
	 * Produces a list of valid target positions, based on the `origin` of skill use, and the `field` of battle
	 */
	def validTargets(origin: CombatPosition, field: CombatField): List[Target]

	/**
	 * Checks if the `target` position is valid (i.e. reachable) from the `origin` position within the `field` of battle
	 */
	def isValidTarget(target: Target, origin: CombatPosition, field: CombatField): Boolean

	/**
	 * Creates a proxy to this TargetMode, which uses the specified `origin` for both the `validTargets` and `isValidTarget` methods
	 */
	def from(origin: CombatPosition) = new TargetModeFrom(this, origin)

	/**
	 * Creates a proxy to this TargetMode, which uses the specified `field` for both the `validTargets` and `isValidTarget` methods
	 */
	def in(field: CombatField) = new TargetModeIn(this, field)
}

/**
 * A Facade for the specified `targetMode`, where the targeting methods do not require an `origin`
 */
class TargetModeFrom(val targetMode: TargetMode, val origin: CombatPosition) {

	/**
	 * Produces a list of valid target positions for a skill used at the `origin` (member val)
	 *  position, within the specified `field` of combat.
	 */
	def validTargets(field: CombatField) = targetMode.validTargets(origin, field)

	/**
	 * Checks if the specified `target` position is a valid target within the `field` of combat,
	 * for a skill used at the `origin` (member val) position.
	 */
	def isValidTarget(target: Target, field: CombatField) = targetMode.isValidTarget(target, origin, field)

	/**
	 * Creates a proxy to this TargetModeFrom, which uses a fixed `field` of combat as well as this
	 * object's `origin` position to answer calls to `validTargets` and `isValidTarget`
	 */
	def in(field: CombatField) = new TargetModeFromIn(targetMode, origin, field)
}

/**
 * A Facade for the specified `targetMode`, where the targeting methods do not require a `field`
 */
class TargetModeIn(val targetMode: TargetMode, val field: CombatField) {

	/**
	 * Produces a list of valid target positions for a skill used within the `field` (member val),
	 * from the specified `origin` position.
	 */
	def validTargets(origin: CombatPosition) = targetMode.validTargets(origin, field)

	/**
	 * Checks if the specified `target` position is a valid target within the `field` (member val)
	 * of combat, for a skill used at the specified `origin` position.
	 */
	def isValidTarget(target: Target, origin: CombatPosition) = targetMode.isValidTarget(target, origin, field)

	/**
	 * Creates a proxy to this TargetModeIn, which uses a fixed `origin` of skill use as well as this
	 * object's `field` of combat to answer calls to `validTargets` and `isValidTarget`
	 */
	def from(origin: CombatPosition) = new TargetModeFromIn(targetMode, origin, field)
}

/**
 * A Facade for the specified `targetMode`, where the targeting methods do not require an `origin` or `field`
 */
class TargetModeFromIn(val targetMode: TargetMode, val origin: CombatPosition, val field: CombatField) {
	/**
	 * Produces a list of valid target positions if a skill using this target mode were to be
	 * used from the `origin` (member val) position, within the `field` of combat.
	 */
	def validTargets = targetMode.validTargets(origin, field)

	/**
	 * Checks if the specified `target` position is a valid target for a skill used from the
	 * `origin` (member val) position, within the `field` of combat
	 */
	def isValidTarget(target: Target) = targetMode.isValidTarget(target, origin, field)
}