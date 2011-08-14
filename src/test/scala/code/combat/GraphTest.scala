package test.dylan.battles

import org.junit._
import Assert._
import org.scalatest.junit.JUnitSuite
import org.scalatest.junit.ShouldMatchersForJUnit
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._
import scalax.collection.edge.Implicits._
import code.dylan.combat._

class GraphTest extends JUnitSuite with ShouldMatchersForJUnit {
	
	@Test
	def testPositionsDist {
		Position(0,0) dist Position(5,0) should be (5)
	}
	
	
	@Test
	def makeGridGraph {
		val nodes = (for(x<- -1 to 1; y<- -1 to 1) yield Position(x,y)) toSet
		def adj(pos:Position) = (for(x<- -1 to 1; y<- -1 to 1; if(x!=0||y!=0)) yield Position(pos.x+x,pos.y+y))
		val edges = nodes.map( node => adj(node) filter (nodes.contains(_)) map(other => node ~ other) ) flatten;
		val g = Graph.from(nodes,edges)
		g.degreeCount should be( Map(3->4, 5->4, 8->1) )
		g.edges.foreach(println(_))
	}
}