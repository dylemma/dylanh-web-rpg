package test.dylan.data.graphs

import org.junit._
import Assert._
import org.scalatest.junit.JUnitSuite
import org.scalatest.junit.ShouldMatchersForJUnit
import com.dylan.data.graphs._
import GraphAlg._

class GraphAlgTest extends JUnitSuite with ShouldMatchersForJUnit {

	implicit object RangeGraphAdaptor extends PartialGraphAdaptor[Int, (Int, Int), Range] with PartialWeightedEdgesAdaptor[(Int, Int), Double, Range] {
		def nodes(input: Range) = input.toSet
		def edges(input: Range) = input.sliding(2).map(v => (v(0), v(1))).toSet
		def contains(input: Range, node: Int) = input.contains(node)
		def edges(input: Range, incidentOn: Int) = {
			val i = incidentOn; val j = i + 1
			(if (input.contains(i) && input.contains(j)) Some((i, j)) else None).toSet
		}
		def weight(input: Range, edge: (Int, Int)) = (edge._1 - edge._2).abs * 1.5
		def edgeEnds(input: Range, edge: (Int, Int)) = edge
	}

	@Test
	def goDijkstra = {
		val range: Range = 1 to 10
		println(DijkstraWeighted(range, 5))
		println(DijkstraUnweighted(range, 5))
	}

	@Test
	def passNow = 1 should be(1)
}