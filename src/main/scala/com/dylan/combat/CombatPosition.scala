package com.dylan.combat

import com.dylan.data.geom.Vector

class CombatPosition(val location: Vector) extends Target {
	var occupant: Option[Combattant] = None
	def occupied = occupant != None

	/**
	 * Tells whether this position is navigable
	 */
	var navigable = true

	//TODO implement the target's takeDamage etc methods
	def takeDamage(d: Damage) = false
	def takeHeal(h: Heal) = false
	def addRecurringEffect(e: AddRecurringEffect) = false
	def removeRecurringEffect(e: RemoveRecurringEffect) = false
}

case class Adjacency(source: CombatPosition, sink: CombatPosition)