package com.dylan.data.graphs

object GraphAlg {

	def DijkstraWeighted[Input, Node, Edge, Weight] // templates, rawr!
	(input: Input, start: Node) // input object, start node
	(implicit graphAdaptor: PartialGraphAdaptor[Node, Edge, Input], weightsAdaptor: PartialWeightedEdgesAdaptor[Edge, Weight, Input], numeric: Numeric[Weight]) //implicit adaptors
	: Map[Node, (Weight, Option[(Node, Edge)])] = //return type
		{
			val g = graphAdaptor.adaptGraph(input)
			val w = weightsAdaptor.adaptWeights(input)
			val wg = g | w
			DijkstraWeighted(wg, start)
		}

	def DijkstraWeighted[Node, Edge, Weight](graph: WeightedGraph[Node, Edge, Weight], start: Node)(implicit numeric: Numeric[Weight]) = Dijkstra(graph, start, graph.weight _)

	def DijkstraUnweighted[Input, Node, Edge](input: Input, start: Node)(implicit graphAdaptor: PartialGraphAdaptor[Node, Edge, Input]): Map[Node, (Int, Option[(Node, Edge)])] =
		{
			val g = graphAdaptor.adaptGraph(input)
			DijkstraUnweighted(g, start)
		}

	def DijkstraUnweighted[Node, Edge](graph: Graph[Node, Edge], start: Node) = Dijkstra(graph, start, (e: Edge) => 1)

	def Dijkstra[Node, Edge, Weight](graph: Graph[Node, Edge], start: Node, weight: (Edge) => Weight)(implicit numeric: Numeric[Weight]): Map[Node, (Weight, Option[(Node, Edge)])] = {
		if (!graph.contains(start)) throw new IllegalArgumentException("Start node must be contained in the graph");

		import numeric._
		val nodes = graph.nodes
		val dist = scala.collection.mutable.Map[Node, Weight]()
		val prev = scala.collection.mutable.Map[Node, (Node, Edge)]() //nodes.map(v => (v, None)).toSeq: _*)
		dist.update(start, numeric.zero);
		object QOrdering extends Ordering[Node] {
			def compare(x: Node, y: Node) = {
				(dist.get(x), dist.get(y)) match {
					case (None, None) => 0 //both nodes have not been found yet, thus their dist = infinity
					case (Some(i), Some(j)) => (i - j).signum
					case (None, Some(j)) => 1
					case (Some(i), None) => -1
				}
			}
		}
		var queue = nodes.toList.sorted(QOrdering)
		var needExit = false
		while (!queue.isEmpty && !needExit) {
			val u = queue.head
			queue = queue.tail
			dist.get(u) match {
				case None => needExit = true
				case Some(uDist) => {
					for (uv <- graph.edges(u)) {
						val alt = uDist + weight(uv)
						val v = graph.follow(u, uv)
						if (!dist.contains(v)) dist.update(v, alt)
						if (alt <= dist(v)) {
							dist.update(v, alt)
							prev.update(v, (u, uv))
							queue = queue.sorted(QOrdering)
						}
					}
				}
			}
		}
		(for (node <- dist.keysIterator) yield (node, (dist(node), prev.get(node)))) toMap
	}

}