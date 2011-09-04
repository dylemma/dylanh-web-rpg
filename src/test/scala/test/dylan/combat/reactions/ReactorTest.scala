package test.dylan.combat.reactions

import org.junit._
import org.scalatest.junit.JUnitSuite
import org.scalatest.junit.ShouldMatchersForJUnit
import com.dylan.combat.reactions.Reactor
import com.dylan.combat.reactions.CompositeReactor

class ReactorTest extends JUnitSuite with ShouldMatchersForJUnit {

	class EvenReactor extends Reactor[Int, String] {
		def react = { case i: Int if i % 2 == 0 => "even" }
	}

	class OddReactor extends Reactor[Int, String] {
		def react = { case i: Int if i % 2 == 1 => "odd" }
	}

	@Test
	def testEvenReactor = {
		val er = new EvenReactor
		er.react.isDefinedAt(2) should be(true)
		er.react.isDefinedAt(1) should be(false)
		er.react(2) should be("even")
	}

	@Test
	def testOddReactor = {
		val or = new OddReactor
		or.react.isDefinedAt(1) should be(true)
		or.react.isDefinedAt(2) should be(false)
		or.react(1) should be("odd")
	}

	@Test
	def testEvenOddComposite = {
		val er = new EvenReactor
		val or = new OddReactor
		val cmp = new CompositeReactor[Int, String] {}
		cmp.add(er)
		cmp.add(or)
		cmp.react.isDefinedAt(1) should be(true)
		cmp.react.isDefinedAt(2) should be(true)
		cmp.react(1) should be("odd")
		cmp.react(2) should be("even")
	}

}