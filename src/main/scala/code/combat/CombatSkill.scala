/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.battles
import com.dylan.battle.Combattant
import com.dylan.data._
import com.dylan.meta.Description

/**
 * A `TargetMode` instance determines the set of Positions that a user can target
 * when using a skill from the given `origin` within the `environment`. The user
 * may target exactly one of these positions. For things like area of effect,
 * specify a `BloomMode`
 */
abstract class TargetMode{
	def validTargets(origin:Position, environment:GraphMap[_]): Set[Position]
}

object TargetMode{
	object SelfTarget extends TargetMode{
		def validTargets(origin:Position, environment:GraphMap[_]) = Set(origin)
	}
	
	def DirectTarget(range:Double) = new TargetMode{
		def validTargets(origin:Position, environment:GraphMap[_]) = {
			val searchResults = GraphOps.Dijkstra(environment, origin)
			(for((pos,(dist,prev))<-searchResults; if(dist <= range)) yield pos) toSet
		}
	} 

	/**
	 * Note/TODO: DirectionalTarget could be an arrow/spell, in which case there is no need
	 * to worry about the occupancy of each Position along the path. But in the case of a
	 * melee attack, the attack should be impeded by things in the way. Possibly want to
	 * make a distinction between Directional DIRECT and Directional Melee targetting modes
	 */
	def DirectionalTarget(direction:Direction, range:Double) = new TargetMode{
		def validTargets(origin:Position, environment:GraphMap[_]) = {
			val path = environment.pathFrom(origin, direction, Int.MaxValue)
			var distance = 0.0
			var set = Set[Position]()
			for(edge<-path){
				distance = distance + edge.length
				if(distance <= range) set = set + edge.target
			}
			set
		}
	}
}
/**
 * A `BloomMode` instance controlls where and how much a skill shows its effects, based on
 * the location that the skill was used (`origin`), the `target` Position, and the `environment`
 * in which the skill was used.
 */
abstract class BloomMode{
	/**
	 * Returns a map of Positions affected by the skill, where a Position maps to a "percentage
	 * effectiveness" value (from 0.0 to 1.0)
	 */
	def areaOfEffect(origin:Position, target:Position, environment:GraphMap[_]): Map[Position, Double]
}

abstract class CombatEffect{
	def affect(target:Combattant, effectiveness:Double): Unit
}

abstract class SkillCost extends Description{
	def spendCost(user:Combattant): Unit
}

abstract case class CombatSkill(targetMode:TargetMode, bloomMode:BloomMode, effect:CombatEffect, cost:SkillCost) extends Description

