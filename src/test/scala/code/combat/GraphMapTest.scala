/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.dylan.data

import org.junit._
import Assert._
import com.dylan.data._
import org.scalatest.junit.JUnitSuite

class GraphMapTest extends JUnitSuite{

	@Before
	def setUp: Unit = {
	}

	@After
	def tearDown: Unit = {
	}
	
	@Test
	def testRectangleGraphStructureSizes = {
		assert(GraphStructureGenerator.rectangularComplete(1,1).size == 1)
		assert(GraphStructureGenerator.rectangularComplete(1,2).size == 2)
		assert(GraphStructureGenerator.rectangularComplete(2,2).size == 4)
		assert(GraphStructureGenerator.rectangularComplete(3,3).size == 9)
		assert(GraphStructureGenerator.rectangularComplete(6,5).size == 30)
	}
	
	@Test
	def testRectangleGraphEdgesAllHaveReturnEdges = {
		val structure = GraphStructureGenerator.rectangularComplete(3,3)
		for(pos<- structure.keys; outEdge<- structure(pos)){
			assert(structure.contains(outEdge.target))
			val returnEdge = structure(outEdge.target).find(_.target == pos)
			returnEdge match {
				case None => assert(false, "There should be a return edge")
				case Some(edge) => assert(edge.direction.opposite == outEdge.direction, edge)
			}
		}
	}
	
	@Test 
	def testRectangleGraphAssortedEdgeSets = {
		val structure = GraphStructureGenerator.rectangularComplete(6,5)
		
		val upperLeftCornerNeighbors = structure(Position(-2.5, 2)).map(_ target)
		assertEquals(upperLeftCornerNeighbors, Set(Position(-2.5,1), Position(-1.5,2), Position(-1.5,1)))
		
		val inTheMiddleNeighbors = structure(Position(.5,0)).map(_ target)
		assertEquals(inTheMiddleNeighbors, 
			Set(
				Position(-.5,1),	Position(.5,1), Position(1.5,1), 
				Position(-.5,0),									Position(1.5,0), 
				Position(-.5,-1),	Position(.5,-1),Position(1.5,-1)
			)
		)
		
		val bottomNeighbors = structure(Position(.5,-2)).map(_ target)
		assertEquals(bottomNeighbors,
			Set(
				Position(-.5,-1),	Position(.5,-1),	Position(1.5,-1),
				Position(-.5,-2),										Position(1.5,-2)
			)
		)
	}
	@Test
	def testGraphMapPositions = {
		val structure = GraphStructureGenerator.rectangularComplete(2,2)
		val graph = new GraphMap[Int](structure)
		assert(graph.hasPosition(Position(-.5,-.5)))
		assert(graph.hasPosition(Position(.5, -.5)))
		assert(graph.hasPosition(Position(-.5, .5)))
		assert(graph.hasPosition(Position(.5, .5)))
	}
	
	@Test
	def testGraphMapEmptyOccupancy = {
		val structure = GraphStructureGenerator.rectangularComplete(3,3)
		val graph = new GraphMap[Int](structure)
		assert(graph.unoccupiedPositions.count(a=>true) == 9)
		assert(graph.occupiedPositions.count(a=>true) == 0)
	}
	
	@Test
	def testGraphMap2Occupancy = {
		val structure = GraphStructureGenerator.rectangularComplete(3,3)
		val graph = new GraphMap(structure, Position(1,0)->1, Position(-1,0)->2)
		assert(graph.unoccupiedPositions.count(a=>true) == 7)
		assert(graph.occupiedPositions.count(a=>true) == 2)
	}
	
	@Test
	def testGraphMapWithInvalidPoints = {
		val structure = GraphStructureGenerator.rectangularComplete(3, 3)
		val graph = new GraphMap(structure, Position(-5,0)->1, Position(-4,0)->2, Position(0,0)->0)
		assert(graph.occupiedPositions.count(a=>true) == 1)
		assert(graph(Position(0,0)) == 0)
		assert(graph.get(Position(-5,0)) == None)
		assert(graph.get(Position(-4,0)) == None)
	}
	
	@Test
	def testGraphMapAddRemoveOccupancy = {
		val structure = Map(Position(0,0)->Set[Edge]())
		val graph = new GraphMap[Int](structure)
		assert(graph.hasPosition(Position(0,0)))
		assertFalse(graph.positionOccupied(Position(0,0)))
		graph.put(Position(0,0), 1)
		assertTrue(graph.positionOccupied(Position(0,0)))
		assert(graph(Position(0,0)) == 1)
		graph -= Position(0,0)
		assert(graph.get(Position(0,0)) == None)
		assertFalse(graph.positionOccupied(Position(0,0)))
	}
}
