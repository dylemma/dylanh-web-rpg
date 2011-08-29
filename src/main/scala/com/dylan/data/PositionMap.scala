package com.dylan.data

import scala.collection.immutable.NumericRange
import com.dylan.data.graphs._

class PositionMap[T](val environment: WeightedGraph[Position, Adjacency, Double], datum: (Position, T)*) extends scala.collection.mutable.Map[Position, T] {
	private val dataMap = scala.collection.mutable.Map[Position, T]()

	def get(key: Position) = dataMap.get(key)
	def iterator = dataMap.iterator
	def +=(kv: (Position, T)) = {
		if (environment.contains(kv._1)) dataMap += kv
		this
	}
	def -=(key: Position) = {
		dataMap -= key
		this
	}

	def occupiedPositions = dataMap.keys
	def unoccupiedPositions = environment.nodes.filter(!dataMap.contains(_))
	def positionOccupied(pos: Position) = dataMap.contains(pos)
}