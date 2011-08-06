/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.data

trait GraphOps {
	type V
	type E
	def nodes: List[V]
	def edges: List[E]
	def edges(from:V): List[E]
	def containsVertex(v:V): Boolean
}

trait WeightedGraphOps extends GraphOps{
	def edgeWeight(edge:E): Double
}

trait DirectedGraphOps extends GraphOps{
	def edgeOrigin(edge:E): V
	def edgeTarget(edge:E): V
}

object GraphOps{
	
	def Dijkstra[V0, G <: (WeightedGraphOps{type V = V0}) with (DirectedGraphOps{type V = V0})]
	(graph:G, start:V0): Map[V0,(Double, Option[V0])] = { //(Map[V0,Double], Map[V0, Option[V0]]) = { 
		if(!graph.containsVertex(start)) throw new IllegalArgumentException("Starting vertex must be contained within the graph")
		val nodes = graph.nodes //cache this list as it is reused a few times
		val dist = scala.collection.mutable.Map(nodes.map(v=>(v,Double.MaxValue)) : _*)
		val prev = scala.collection.mutable.Map[V0,Option[V0]](nodes.map(v=>(v,None)) : _*)
		dist.update(start,0)
		object QOrdering extends Ordering[V0]{def compare(x:V0, y:V0) = (dist(x)-dist(y)).signum}
		var queue = nodes.sorted(QOrdering)
		var needExit = false
		while(!queue.isEmpty && !needExit){
			val u = queue.head
			queue = queue.tail
			val uDist = dist(u)
			if(uDist == Double.MaxValue){
				needExit = true
			} else {
				for(uv <- graph.edges(u)){
					val alt = uDist + graph.edgeWeight(uv)
					val target = graph.edgeTarget(uv)
					if(alt < dist(target)){
						dist.update(target, alt)
						prev.update(target, Some(u))
						queue = queue.sorted(QOrdering)
					}
				}
			}
		}
		nodes.map(v=>(v,(dist(v), prev(v)))) toMap
	}	
	
	//TODO: A* Algorithm
}