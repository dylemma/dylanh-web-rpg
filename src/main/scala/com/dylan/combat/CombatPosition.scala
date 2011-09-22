package com.dylan.combat

import com.dylan.data.Vector

class CombatPosition(val location: Vector) extends Target {
	var occupant: Option[Combattant] = None
	def occupied = occupant != None

	/**
	 * Tells whether this position is navigable
	 */
	var navigable = true
}

case class Adjacency(source: CombatPosition, sink: CombatPosition)