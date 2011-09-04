package com.dylan.combat.skills

import com.dylan.data.Position
import com.dylan.combat.CombatField

abstract class TargetMode {

	/**
	 * Produces a list of valid target positions, based on the `origin` of skill use, and the `field` of battle
	 */
	def validTargets(origin: Position, field: CombatField): List[Position]

	/**
	 * Checks if the `target` position is valid (i.e. reachable) from the `origin` position within the `field` of battle
	 */
	def isValidTarget(target: Position, origin: Position, field: CombatField): Boolean

	/**
	 * Creates a proxy to this TargetMode, which uses the specified `origin` for both the `validTargets` and `isValidTarget` methods
	 */
	def from(origin: Position) = new TargetModeFrom(this, origin)

	/**
	 * Creates a proxy to this TargetMode, which uses the specified `field` for both the `validTargets` and `isValidTarget` methods
	 */
	def in(field: CombatField) = new TargetModeIn(this, field)
}

/**
 * Convenience class that wraps a specified `targetMode` instance, with methods defined
 * at a single fixed `origin`.
 */
class TargetModeFrom(val targetMode: TargetMode, val origin: Position) {

	/**
	 * Produces a list of valid target positions for a skill used at the `origin` (member val)
	 *  position, within the specified `field` of combat.
	 */
	def validTargets(field: CombatField) = targetMode.validTargets(origin, field)

	/**
	 * Checks if the specified `target` position is a valid target within the `field` of combat,
	 * for a skill used at the `origin` (member val) position.
	 */
	def isValidTarget(target: Position, field: CombatField) = targetMode.isValidTarget(target, origin, field)

	/**
	 * Creates a proxy to this TargetModeFrom, which uses a fixed `field` of combat as well as this
	 * object's `origin` position to answer calls to `validTargets` and `isValidTarget`
	 */
	def in(field: CombatField) = new TargetModeFromIn(targetMode, origin, field)
}

/**
 * Convenience class that wraps a specified `targetMode` instance, with methods defined
 * at a single fixed `field` of combat.
 */
class TargetModeIn(val targetMode: TargetMode, val field: CombatField) {

	/**
	 * Produces a list of valid target positions for a skill used within the `field` (member val),
	 * from the specified `origin` position.
	 */
	def validTargets(origin: Position) = targetMode.validTargets(origin, field)

	/**
	 * Checks if the specified `target` position is a valid target within the `field` (member val)
	 * of combat, for a skill used at the specified `origin` position.
	 */
	def isValidTarget(target: Position, origin: Position) = targetMode.isValidTarget(target, origin, field)

	/**
	 * Creates a proxy to this TargetModeIn, which uses a fixed `origin` of skill use as well as this
	 * object's `field` of combat to answer calls to `validTargets` and `isValidTarget`
	 */
	def from(origin: Position) = new TargetModeFromIn(targetMode, origin, field)
}

/**
 * Convenience class that wraps a specified `targetMode` instance, with methods defined
 * at a single fixed `origin` within a fixed `field` of combat.
 */
class TargetModeFromIn(val targetMode: TargetMode, val origin: Position, val field: CombatField) {
	/**
	 * Produces a list of valid target positions if a skill using this target mode were to be
	 * used from the `origin` (member val) position, within the `field` of combat.
	 */
	def validTargets = targetMode.validTargets(origin, field)

	/**
	 * Checks if the specified `target` position is a valid target for a skill used from the
	 * `origin` (member val) position, within the `field` of combat
	 */
	def isValidTarget(target: Position) = targetMode.isValidTarget(target, origin, field)
}

/**
 * TargetMode implementation for a self-targetting skill.
 */
class SelfTarget(val self: Position) extends TargetMode {
	/**
	 * Produces a list containing the `self` position, since a self-targetting
	 * skill can only target the location it was used from.
	 */
	def validTargets(origin: Position, field: CombatField) = List(self)

	/**
	 * A target position is only valid if it is the same position that the skill
	 * is being used from, i.e. the `self`.
	 */
	def isValidTarget(target: Position, origin: Position, field: CombatField) = target == self
}

/**
 * TargetMode implementation that represents a direct attack with a given range. This target mode
 * does not consider obstacles between the `origin` and the `target`; only that the target is within
 * the `field` of battle, and that it is within range (less than or equal to) of the origin.
 */
class DirectTarget(val range: Double) extends TargetMode {
	/**
	 * Produces a list of positions that lie within `range` of the `origin` position, which exist within the `field` of combat.
	 */
	def validTargets(origin: Position, field: CombatField) = field.positions.iterator.filter(_.dist(origin) <= range).toList.sortBy(_.dist(origin))

	/**
	 * Checks if the specified `target` position exists in the specified `field`, and that it lies within `range` of the `origin`
	 */
	def isValidTarget(target: Position, origin: Position, field: CombatField) = field.containsPosition(target) && target.dist(origin) <= range
}

//TODO: Create a "Blockable" target mode, which has a set range but will consider obstacles
