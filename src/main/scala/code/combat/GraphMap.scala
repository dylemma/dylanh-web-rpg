///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.dylan.data
//
//class GraphMap[T](private val structure:Map[Position,Set[Edge]], datum:(Position,T)*)
//extends scala.collection.mutable.Map[Position,T]
//with WeightedGraphOps with DirectedGraphOps{
//	private var dataMap = Map(datum.filter(d=>structure.contains(d._1)):_*)
//	type V = Position
//	type E = Edge
//	
//	
//	//-----MAP Methods-----
//	def get(key: Position) = dataMap.get(key)
//	def iterator: Iterator[(Position,T)] = dataMap.iterator
//	def += (kv: (Position, T)) = {
//		if(structure.contains(kv._1)) dataMap = dataMap + kv
//		this
//	}
//	def -= (key: Position) = {
//		dataMap = dataMap - key
//		this
//	}
//	//-----End of MAP Methods-----
//	
//	def hasPosition = structure.contains(_)
//	def positionOccupied = dataMap.contains(_)
//	
//	def occupiedPositions = dataMap.keys
//	def unoccupiedPositions = structure.keys.filterNot(dataMap.contains(_))
//	def allPositions = structure.keys
//	
//	def outEdges(pos:Position) = structure.getOrElse(pos, Set())
//	def neighbors(pos:Position) = outEdges(pos).map(_ target)
//	
//	def pathFrom(start:Position, dir:Direction, stepLimit:Int):List[Edge] = {
//		if(stepLimit == 0)
//			Nil
//		else outEdges(start).find(_.direction == dir) match {
//			case None => Nil
//			case Some(edge) => edge :: pathFrom(edge.target, dir, stepLimit-1)
//		}
//	}
//	
//	//-----GraphOps methods
//	def nodes:List[Position] = allPositions toList
//	def edges:List[Edge] = allPositions.map(outEdges(_)).flatten toList
//	def edges(from:Position) = outEdges(from) toList
//	def containsVertex(v:Position) = hasPosition(v)
//	def edgeTarget(edge:Edge) = edge.target
//	def edgeWeight(edge:Edge) = edge.length
//	
//	/**
//	 * Due to the Edge class not storing a "target", this method is O(|V| + |E|) so call with care
//	 */
//	def edgeOrigin(edge:Edge) = allPositions.find(outEdges(_).contains(edge)) match {
//		case None => throw new IllegalArgumentException(edge + " does not appear to be in the graph")
//		case Some(node) => node
//	}
//}
//
//object GraphStructureGenerator{
//	def rectangularComplete(origin:Position, width:Int, height:Int): Map[Position,Set[Edge]] = {
//		def centeredRange(c:Double, l:Int) = {
//			if(l%2==0) (c-l/2+.5) to (c+l/2-.5, 1)
//			else c-l/2 to (c+l/2,1)
//		}
//		val positions = (for(x<-centeredRange(origin.x, width); y<-centeredRange(origin.y, height)) yield Position(x,y)) toSet
//		val directions = for(i<- -1 to 1; j<- -1 to 1; if(i!=0||j!=0)) yield Position(i,j)
//
//		(for(pos<-positions)
//			yield (pos, (directions.map(dir=>Edge(pos,dir)).filter( t=>positions.contains(t.target)) toSet ))
//			) toMap
//	}
//
//	def rectangularComplete(width:Int, height:Int): Map[Position,Set[Edge]] = rectangularComplete(Position(0,0), width, height)
//}
