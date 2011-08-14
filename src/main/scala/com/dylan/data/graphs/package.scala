package com.dylan.data

package object graphs {
	//--------------------------------------------------------------
	//	IMPLICIT PROMOTERS
	//--------------------------------------------------------------

	type WeightedGraph[Node, Edge, Weight] = Graph[Node, Edge] with WeightedEdges[Edge, Weight]
	type UndirectedGraph[Node, Edge] = Graph[Node, Edge] with UndirectedEdges[Node, Edge]
	type DirectedGraph[Node, Edge] = Graph[Node, Edge] with DirectedEdges[Node, Edge]
	type WeightedUndirectedGraph[Node, Edge, Weight] = Graph[Node, Edge] with WeightedEdges[Edge, Weight] with UndirectedEdges[Node, Edge]
	type WeightedDirectedGraph[Node, Edge, Weight] = Graph[Node, Edge] with WeightedEdges[Edge, Weight] with DirectedEdges[Node, Edge]

	class GraphUpgrader[Node, Edge](graph: Graph[Node, Edge]) {
		def |[Weight](weights: WeightedEdges[Edge, Weight]): WeightedGraph[Node, Edge, Weight] =
			new GraphProxy[Node, Edge] with WeightedEdgesProxy[Edge, Weight] {
				def graphSubject = graph
				def weightedEdgesSubject = weights
			}

		def |(undirectedEdges: UndirectedEdges[Node, Edge]): UndirectedGraph[Node, Edge] =
			new GraphProxy[Node, Edge] with UndirectedEdgesProxy[Node, Edge] {
				def graphSubject = graph
				def undirectedEdgesSubject = undirectedEdges
			}

		def |(directedEdges: DirectedEdges[Node, Edge]): DirectedGraph[Node, Edge] =
			new GraphProxy[Node, Edge] with DirectedEdgesProxy[Node, Edge] {
				def graphSubject = graph
				def directedEdgesSubject = directedEdges
			}
	}

	implicit def graphToGraphUpgrader[Node, Edge](graph: Graph[Node, Edge]) = new GraphUpgrader(graph)

	class WeightedGraphUpgrader[Node, Edge, Weight](graph: WeightedGraph[Node, Edge, Weight]) {
		def |(undirectedEdges: UndirectedEdges[Node, Edge]): WeightedUndirectedGraph[Node, Edge, Weight] =
			new GraphProxy[Node, Edge] with WeightedEdgesProxy[Edge, Weight] with UndirectedEdgesProxy[Node, Edge] {
				def graphSubject = graph
				def weightedEdgesSubject = graph
				def undirectedEdgesSubject = undirectedEdges
			}

		def |(directedEdges: DirectedEdges[Node, Edge]): WeightedDirectedGraph[Node, Edge, Weight] =
			new GraphProxy[Node, Edge] with WeightedEdgesProxy[Edge, Weight] with DirectedEdgesProxy[Node, Edge] {
				def graphSubject = graph
				def weightedEdgesSubject = graph
				def directedEdgesSubject = directedEdges
			}
	}
	implicit def weightedGraphToWGUpgrader[Node, Edge, Weight](graph: WeightedGraph[Node, Edge, Weight]) = new WeightedGraphUpgrader(graph)

	class UndirectedGraphUpgrader[Node, Edge](graph: UndirectedGraph[Node, Edge]) {
		def |[Weight](weights: WeightedEdges[Edge, Weight]): WeightedUndirectedGraph[Node, Edge, Weight] =
			new GraphProxy[Node, Edge] with UndirectedEdgesProxy[Node, Edge] with WeightedEdgesProxy[Edge, Weight] {
				def graphSubject = graph
				def undirectedEdgesSubject = graph
				def weightedEdgesSubject = weights
			}
	}
	implicit def undirectedGraphToUndiUpgrader[Node, Edge](graph: UndirectedGraph[Node, Edge]) = new UndirectedGraphUpgrader(graph)

	class DirectedGraphUpgrader[Node, Edge](graph: DirectedGraph[Node, Edge]) {
		def |[Weight](weights: WeightedEdges[Edge, Weight]): WeightedDirectedGraph[Node, Edge, Weight] =
			new GraphProxy[Node, Edge] with DirectedEdgesProxy[Node, Edge] with WeightedEdgesProxy[Edge, Weight] {
				def graphSubject = graph
				def directedEdgesSubject = graph
				def weightedEdgesSubject = weights
			}
	}
	implicit def undirectedGraphToDiUpgrader[Node, Edge](graph: DirectedGraph[Node, Edge]) = new DirectedGraphUpgrader(graph)
}