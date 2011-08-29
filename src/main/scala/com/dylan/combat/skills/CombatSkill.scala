/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.combat.skills

import com.dylan.battle.Combattant
import com.dylan.data._
import graphs.GraphAlg._
import Numeric.Implicits._
import com.dylan.data.meta.Description

/**
 * A `TargetMode` instance determines the set of Positions that a user can target
 * when using a skill from the given `origin` within the `environment`. The user
 * may target exactly one of these positions. For things like area of effect,
 * specify a `BloomMode`
 */
abstract class TargetMode {
	def validTargets(origin: Position, environment: PositionMap[_]): Set[Position]
}

class SelfTarget extends TargetMode {
	def validTargets(origin: Position, environment: PositionMap[_]) = Set(origin)
}

class DirectTarget(val range: Double) extends TargetMode {
	def validTargets(origin: Position, environment: PositionMap[_]) = {
		val searchResults = DijkstraWeighted(environment.environment, origin)
		(for ((pos, (dist, prev)) <- searchResults; if (dist <= range)) yield pos) toSet
	}
}

/**
 * Note/TODO: DirectionalTarget could be an arrow/spell, in which case there is no need
 * to worry about the occupancy of each Position along the path. But in the case of a
 * melee attack, the attack should be impeded by things in the way. Possibly want to
 * make a distinction between Directional DIRECT and Directional Melee targetting modes
 */
class DirectionalTarget(val initialEdge: Adjacency, val range: Double) extends TargetMode {
	def validTargets(origin: Position, map: PositionMap[_]) = {
		val direction = map.environment.follow(origin, initialEdge) - origin
		val length = direction.length
		var spaces: List[Position] = Nil
		var totalDist = 0.0
		var next = origin
		do {
			spaces = next :: spaces
			totalDist += length
			next = next + direction
		} while (totalDist <= range && map.environment.contains(next))

		spaces.toSet
	}
}
/**
 * A `BloomMode` instance controlls where and how much a skill shows its effects, based on
 * the location that the skill was used (`origin`), the `target` Position, and the `environment`
 * in which the skill was used.
 */
abstract class BloomMode {
	/**
	 * Returns a map of Positions affected by the skill, where a Position maps to a "percentage
	 * effectiveness" value (from 0.0 to 1.0)
	 */
	def areaOfEffect(origin: Position, target: Position, environment: PositionMap[_]): Map[Position, Double]
}

abstract class CombatEffect {
	def affect(target: Combattant, effectiveness: Double): Unit
}

abstract class SkillCost extends Description {
	def spendCost(user: Combattant): Unit
}

abstract case class CombatSkill(targetMode: TargetMode, bloomMode: BloomMode, effect: CombatEffect, cost: SkillCost) extends Description

