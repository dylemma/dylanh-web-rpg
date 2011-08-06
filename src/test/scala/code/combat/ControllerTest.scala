/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.dylan.controller

import org.junit._
import Assert._
import com.dylan.controller._

class ControllerTest {

	case class IntMessage(i:Int) extends InteractionMessage
	case class StringMessage(s:String) extends InteractionMessage
	class CountdownReaction(message:IntMessage) extends Reaction[Int](message, _+1)
	trait CountdownGenerator extends ActionGenerator[Int]{
		def generateActionList(state:Int) = {
			new Action(
				StringMessage("Starting countdown from " + state), 
				1, 
				(i:Int) => for(j<-(i-1) to(0,-1)) yield new CountdownReaction(IntMessage(j))
			) ::
			new Action(
				StringMessage("Bouncing back up to " + (state-1)), 
				1, 
				(i:Int) => new Reaction(StringMessage("Bounced to " + (state-1)), (i:Int)=>state-1) :: Nil
			) :: 
			Nil
		}
	}
	
	trait MessageHandler extends InteractionMessageHandler{
		def handleMessage(message:InteractionMessage) = message match {
			case IntMessage(i) => println(i)
			case StringMessage(s) => println(s)
		}
	}
	
	@Before
	def setUp: Unit = {
	}

	@After
	def tearDown: Unit = {
	}

	@Test
	def nothing = {}
	
	//@Test
	def example = {
		object controller extends EvolvingStateController[Int](10, _==0) with CountdownGenerator with MessageHandler
		while(!controller.done) controller.step
		assert(true)
	}

}
