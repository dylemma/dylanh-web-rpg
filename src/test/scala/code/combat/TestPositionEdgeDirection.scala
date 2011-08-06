/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.dylan.data

import org.junit._
import Assert._
import com.dylan.data._

class TestPositionEdgeDirection {

	@Before
	def setUp: Unit = {
	}

	@After
	def tearDown: Unit = {
	}

	@Test
	def testPositionAdd = {
		assert(Position(0,0) + Position(1,1) == Position(1,1))
		assert(Position(1,2) + Position(-1,-2) == Position(0,0))
	}
	
	@Test
	def testEdgeDirectionOpposites = {
		import Direction._
		assert(Up.opposite == Down)
		assert(Down.opposite == Up)
		assert(Left.opposite == Right)
		assert(Right.opposite == Left)
		assert(UpRight.opposite == DownLeft)
		assert(DownLeft.opposite == UpRight)
		assert(UpLeft.opposite == DownRight)
		assert(DownRight.opposite == UpLeft)
	}
	
	@Test
	def testEdgesFromVectors = {
		import Direction._
		assert(fromVector(1, 1) == UpRight)
		assert(fromVector(1, 0) == Right)
		assert(fromVector(1, -1)== DownRight)
		assert(fromVector(0, 1) == Up)
		assert(fromVector(0, -1)== Down)
		assert(fromVector(-1, 1)== UpLeft)
		assert(fromVector(-1, 0)== Left)
		assert(fromVector(-1,-1)== DownLeft)
	}

}
