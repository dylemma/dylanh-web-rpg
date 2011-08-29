/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.dylan.data

import org.junit._
import Assert._
import com.dylan.data.Position
import com.dylan.data.graphs._
import GraphAlg._
import org.scalatest.junit.JUnitSuite

class GraphOpsTest extends JUnitSuite {

	@Before
	def setUp: Unit = {
	}

	@After
	def tearDown: Unit = {
	}

	@Test
	def testDijkstra = {
		val graph = new GridGraph(-2.0 to 2 by 1, -2.0 to 2 by 1)
		val result = DijkstraWeighted(graph, Position(0, 0))
		//
		println("=================================")
		println("        Dijkstra's Output        ")
		println("=================================")
		for ((pos, (dist, prev)) <- result) {
			println("Found " + pos + " at " + dist + " away from start. Came from " + prev)
		}
	}

}
