/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.battle

import com.dylan.controller._
import com.dylan.data.Position

/**
 * Represents a battle between two opposing teams. The battle is over
 * when either team is defeated. The winner is the non-defeated team.
 * In the case of a tie, the battle may be over, with no winner.
 * 
 * @param teamA the "left hand" team
 * @param teamB the "right hand" team
 */
case class Battle(val teamA:CombatTeam, val teamB:CombatTeam) {
	def over = teamA.defeated || teamB.defeated
	def winner = {
		if(teamA.defeated && teamB.defeated) None
		else if(teamA.defeated) Some(teamB)
		else if(teamB.defeated) Some(teamA)
		else None
	}
	def loser = winner match {
		case None => None
		case Some(team) => opponent(team)
	}
	def opponent(team:CombatTeam) = team match {
		case team if(team==teamA) => Some(teamB)
		case team if(team==teamB) => Some(teamA)
		case _ => None
	}
}

/**
 * A team of Combattants. The team is defeated when none
 * of the combattants are alive.
 */
class CombatTeam(members:List[Combattant]){
	def defeated = members.find(_ alive) == None
}

/**
 * Represents the qualities of a Combattant. A combattant has
 * hit points that determine whether it is alive, a position on
 * the battlefield, and a list of skills to use.
 * TODO: a combattant should also have stats and potentially gear
 */
trait Combattant{
	def hitPoints:PointsValue
		def dead = hitPoints.current <= 0
		def alive = !dead
		
	def position:Position
	def skills:List[BattleSkill]
}

/**
 * Represents a depletable/refillable value, such as HP or Mana
 * TODO: flesh this out with operator overloads etc
 */
case class PointsValue(current:Int, max:Int)

case class BattleSkillUsedMessage(skillName:String, user:Combattant)
case class BattleSkill(useMessage:BattleSkillUsedMessage, effects:(Battle)=>Seq[Reaction[Battle]])