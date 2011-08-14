package com.dylan.data.graphs

//object GraphTraits {

//--------------------------------------------------------------
//	GRAPH DEFINITION
//--------------------------------------------------------------

/**
 * Base trait for a graph
 */
trait Graph[Node, Edge] {
	def nodes: Set[Node]
	def contains(node: Node): Boolean
	def edges: Set[Edge]
	def edges(incidentOn: Node): Set[Edge]
	def edgeEnds(edge: Edge): (Node, Node)
	def edges(from: Node, to: Node): Set[Edge] = edges(from).filter(_ == to)
	def edge(from: Node, to: Node): Option[Edge] = edges(from).find(_ == to)
	def follow(from: Node, over: Edge) = {
		val ends = edgeEnds(over)
		if (ends._1 == from) ends._2
		else if (ends._2 == from) ends._1
		else throw new IllegalArgumentException("edge " + over + " is not incident on " + from);
	}
}

/**
 * Trait that uses a `graphSubject` to perform Graph operations
 */
trait GraphProxy[Node, Edge] extends Graph[Node, Edge] {
	def graphSubject: Graph[Node, Edge]
	def nodes = graphSubject.nodes
	def edges = graphSubject.edges
	def edgeEnds(edge: Edge) = graphSubject.edgeEnds(edge)
	def contains(node: Node) = graphSubject.contains(node)
	def edges(incidentOn: Node): Set[Edge] = graphSubject.edges(incidentOn)
}

/**
 * Trait that treats an `Input` like a Graph
 */
trait PartialGraphAdaptor[Node, Edge, Input] {
	def nodes(input: Input): Set[Node]
	def edges(input: Input): Set[Edge]
	def edgeEnds(input: Input, edge: Edge): (Node, Node)
	def contains(input: Input, node: Node): Boolean
	def edges(input: Input, incidentOn: Node): Set[Edge]
	def adaptGraph(input: Input) = new GraphAdaptor(this, input)
}

/**
 * PartialGraphAdaptor with a dedicated `Input` value which is used to perform Graph operations
 */
class GraphAdaptor[Node, Edge, Input](protected val adaptor: PartialGraphAdaptor[Node, Edge, Input], protected val adaptee: Input)
	extends Graph[Node, Edge] {
	def nodes = adaptor.nodes(adaptee)
	def edges = adaptor.edges(adaptee)
	def edgeEnds(edge: Edge) = adaptor.edgeEnds(adaptee, edge)
	def contains(node: Node) = adaptor.contains(adaptee, node)
	def edges(incidentOn: Node) = adaptor.edges(adaptee, incidentOn)
}

//--------------------------------------------------------------
//	WEIGHTED EDGES DEFINITION
//--------------------------------------------------------------

trait WeightedEdges[Edge, Weight] {
	def weight(edge: Edge): Weight
}

trait WeightedEdgesProxy[Edge, Weight] extends WeightedEdges[Edge, Weight] {
	protected def weightedEdgesSubject: WeightedEdges[Edge, Weight]
	def weight(edge: Edge) = weightedEdgesSubject.weight(edge)
}

trait PartialWeightedEdgesAdaptor[Edge, Weight, Input] {
	def weight(input: Input, edge: Edge): Weight
	def adaptWeights(input: Input) = new WeightedEdgesAdaptor(this, input)
}

class WeightedEdgesAdaptor[Edge, Weight, Input](protected val adaptor: PartialWeightedEdgesAdaptor[Edge, Weight, Input], protected val adaptee: Input)
	extends WeightedEdges[Edge, Weight] {
	def weight(edge: Edge) = adaptor.weight(adaptee, edge)
}

//--------------------------------------------------------------
//	DIRECTED EDGES DEFINITION
//--------------------------------------------------------------

trait DirectedEdges[Node, Edge] {
	def outgoing(node: Node): Set[Edge]
	def incoming(node: Node): Set[Edge]
	def target(edge: Edge): Node
	def source(edge: Edge): Node
	def outNeighbors(node: Node): Set[Node] = outgoing(node).map(target(_))
	def inNeighbors(node: Node): Set[Node] = incoming(node).map(source(_))
}

trait DirectedEdgesProxy[Node, Edge] extends DirectedEdges[Node, Edge] {
	protected def directedEdgesSubject: DirectedEdges[Node, Edge]
	def outgoing(node: Node) = directedEdgesSubject.outgoing(node)
	def incoming(node: Node) = directedEdgesSubject.incoming(node)
	def target(edge: Edge) = directedEdgesSubject.target(edge)
	def source(edge: Edge) = directedEdgesSubject.source(edge)
}

trait PartialDirectedEdgesAdaptor[Node, Edge, Input] {
	def outgoing(input: Input, node: Node): Set[Edge]
	def incoming(input: Input, node: Node): Set[Edge]
	def target(input: Input, edge: Edge): Node
	def source(input: Input, edge: Edge): Node
	def adaptDirectedEdges(input: Input) = new DirectedEdgesAdaptor(this, input)
}

class DirectedEdgesAdaptor[Node, Edge, Input](protected val adaptor: PartialDirectedEdgesAdaptor[Node, Edge, Input], protected val adaptee: Input)
	extends DirectedEdges[Node, Edge] {
	def outgoing(node: Node) = adaptor.outgoing(adaptee, node)
	def incoming(node: Node) = adaptor.incoming(adaptee, node)
	def target(edge: Edge) = adaptor.target(adaptee, edge)
	def source(edge: Edge) = adaptor.source(adaptee, edge)
}

//--------------------------------------------------------------
//	UNDIRECTED EDGES DEFINITION
//--------------------------------------------------------------

trait UndirectedEdges[Node, Edge] {
	def incidentEdges(node: Node): Set[Edge]
	def endpoints(edge: Edge): (Node, Node)
	def neighbor(node: Node, edge: Edge): Node = {
		val ends = endpoints(edge)
		if (node == ends._1) ends._2
		else if (node == ends._2) ends._1
		else throw new IllegalArgumentException
	}
	def neighbors(node: Node) = incidentEdges(node).map(neighbor(node, _))
}

trait UndirectedEdgesProxy[Node, Edge] extends UndirectedEdges[Node, Edge] {
	protected def undirectedEdgesSubject: UndirectedEdges[Node, Edge]
	def incidentEdges(node: Node) = undirectedEdgesSubject.incidentEdges(node)
	def endpoints(edge: Edge) = undirectedEdgesSubject.endpoints(edge)
}

trait PartialUndirectedEdgesAdaptor[Node, Edge, Input] {
	def incidentEdges(input: Input, node: Node): Set[Edge]
	def endpoints(input: Input, edge: Edge): (Node, Node)
	def adaptUndirectedEdges(input: Input) = new UndirectedEdgesAdaptor(this, input)
}

class UndirectedEdgesAdaptor[Node, Edge, Input](protected val adaptor: PartialUndirectedEdgesAdaptor[Node, Edge, Input], protected val adaptee: Input)
	extends UndirectedEdges[Node, Edge] {
	def incidentEdges(node: Node) = adaptor.incidentEdges(adaptee, node)
	def endpoints(edge: Edge) = adaptor.endpoints(adaptee, edge)
}
