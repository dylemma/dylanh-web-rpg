package com.dylan.combat

import scala.collection.JavaConversions._

//class CombatField(val graph: Graph[CombatPosition, Adjacency]) {
//
//	def positions: Set[CombatPosition] = graph.vertexSet.toSet
//
//	def containsPosition(pos: CombatPosition) = graph.containsVertex(pos)
//
//	def findPath(start: CombatPosition, end: CombatPosition, limit: Double = Double.MaxValue) = {
//		val pathFinder = new DijkstraShortestPath(graph, start, end, limit)
//		pathFinder.getPath match {
//			case null => None
//			case path => Some(path)
//		}
//	}
//}

trait CombatField {
	def positions: Set[CombatPosition]
	def combattants: List[Combattant]
	def targets = combattants ++ positions

	def getPositionOf(combattant: Combattant) = positions.find(_.occupant == Some(combattant))
	def positionOf(combattant: Combattant) = getPositionOf(combattant).get
}