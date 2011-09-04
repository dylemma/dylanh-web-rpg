package com.dylan.combat
import org.jgrapht.Graph
import com.dylan.data.{ Position, Adjacency }
import scala.collection.JavaConversions._
import org.jgrapht.alg.DijkstraShortestPath

class CombatField(val graph: Graph[Position, Adjacency]) {

	def positions: Set[Position] = graph.vertexSet.toSet

	def containsPosition(pos: Position) = graph.containsVertex(pos)

	def findPath(start: Position, end: Position, limit: Double = Double.MaxValue) = {
		val pathFinder = new DijkstraShortestPath(graph, start, end, limit)
		pathFinder.getPath match {
			case null => None
			case path => Some(path)
		}
	}
}