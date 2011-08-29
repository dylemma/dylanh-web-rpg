package test.dylan.data.statrefactor

import org.junit._
import Assert._
import com.dylan.data.meta.Description
import org.scalatest.junit.JUnitSuite
import org.scalatest.junit.ShouldMatchersForJUnit
import scala.xml.Text
import com.dylan.data.stats._

class CritereaTest extends JUnitSuite with ShouldMatchersForJUnit {
	import com.dylan.data._

	class SimpleDependent[A] extends Dependent[A] {
		var observed = false

		def observeCritereaChanged(old: A, nu: A) = {
			observed = true
		}
	}

	@Test
	def testCritereaGetSet = {
		val c = Criterea(10)
		c.value should be(10)

		c.value = 5
		c.value should be(5)
	}

	@Test
	def testCritereaDependent = {
		val c = Criterea(10)
		val d = new SimpleDependent[Int]
		c.addDependent(d)
		c.value = 0
		d.observed should be(true)

		d.observed = false
		c.removeDependent(d)
		c.value = 10
		d.observed should be(false)
	}

	@Test
	def testTransformedCriterea1 = {
		val c1 = Criterea(10)
		val ct = DependencyTransformer(c1)(_ * 2)
		val d = new SimpleDependent[Int]
		ct.addDependent(d)
		ct.value should be(20)
		c1.value = 5
		d.observed should be(true)
		ct.value should be(10)
	}

	@Test
	def testTransformedCriterea2 = {
		val c1 = Criterea(10)
		val c2 = Criterea("hi")
		val d = new SimpleDependent[String]
		val ct = DependencyTransformer(c1, c2)((i, s) => i + s)
		ct.addDependent(d)
		d.observed should be(false)
		ct.value should be("10hi")

		c1.value = 5
		d.observed should be(true)
		ct.value should be("5hi")

		d.observed = false
		c2.value = "sup"
		d.observed should be(true)
		ct.value should be("5sup")
	}

	@Test
	def testTransformedCriterea3 = {
		val c1 = Criterea(10)
		val c2 = Criterea("hi")
		val c3 = Criterea(5.0)
		val ct = DependencyTransformer(c1, c2, c3)((i, s, d) => (i * d) + s)
		val d = new SimpleDependent[String]
		ct.addDependent(d)
		d.observed should be(false)
		ct.value should be("50.0hi")

		c1.value = 5
		d.observed should be(true)
		ct.value should be("25.0hi")
		d.observed = false

		c2.value = "yo"
		d.observed should be(true)
		ct.value should be("25.0yo")
		d.observed = false

		c3.value = 10.0
		d.observed should be(true)
		ct.value should be("50.0yo")

	}
}