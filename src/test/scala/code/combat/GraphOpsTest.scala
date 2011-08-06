/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.dylan.data

import org.junit._
import Assert._
import com.dylan.data._

class GraphOpsTest {

	@Before
	def setUp: Unit = {
	}

	@After
	def tearDown: Unit = {
	}

	@Test
	def testDijkstra = {
		val graph = new GraphMap[Int](GraphStructureGenerator.rectangularComplete(5,5))
		val result = GraphOps.Dijkstra(graph, Position(0,0))
		//
		println("=================================")
		println("        Dijkstra's Output        ")
		println("=================================")
		for((pos,(dist,prev))<-result){
			println("Found " + pos + " at " + dist + " away from start. Came from " + prev)
		}
	}

}
