package com.dylan.combat

import scala.collection.JavaConversions._

trait CombatField {
	def positions: Set[CombatPosition]
	def combattants: List[Combattant]
	def targets = combattants ++ positions

	def getPositionOf(combattant: Combattant) = positions.find(_.occupant == Some(combattant))
	def positionOf(combattant: Combattant) = getPositionOf(combattant).get
}