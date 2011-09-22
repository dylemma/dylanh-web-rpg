package com.dylan.combat

import org.jgrapht.Graph
import scala.collection.JavaConversions._
import org.jgrapht.alg.DijkstraShortestPath

class CombatField(val graph: Graph[CombatPosition, Adjacency]) {

	def positions: Set[CombatPosition] = graph.vertexSet.toSet

	def containsPosition(pos: CombatPosition) = graph.containsVertex(pos)

	def findPath(start: CombatPosition, end: CombatPosition, limit: Double = Double.MaxValue) = {
		val pathFinder = new DijkstraShortestPath(graph, start, end, limit)
		pathFinder.getPath match {
			case null => None
			case path => Some(path)
		}
	}
}