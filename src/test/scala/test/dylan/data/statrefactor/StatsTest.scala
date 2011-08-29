package test.dylan.data.stats

import org.junit._
import Assert._
import com.dylan.data.meta.Description
import org.scalatest.junit.JUnitSuite
import org.scalatest.junit.ShouldMatchersForJUnit
import scala.xml.Text
import com.dylan.data.stats._
import StatModMeta._
import scala.collection.mutable.ListBuffer

class StatTest extends JUnitSuite with ShouldMatchersForJUnit {

	trait GenericDescription extends Description {
		def name = "TheStat"
		def description = Text("blah blah")
	}

	trait GenericModDescription extends Description {
		def name = "TheMod"
		def description = Text("blah blah")
	}

	class SimpleDependent[A] extends Dependent[A] {
		var observed = false

		def observeCritereaChanged(old: A, nu: A) = {
			observed = true
		}
	}

	class HistoryDependent[A] extends Dependent[A] {
		private val _history = new ListBuffer[A]

		def observeCritereaChanged(old: A, nu: A) = _history += nu
		def history = _history.toList
	}

	class SimpleStatMod[A](func: (A) => A, val priority: Int, val timeframe: Timeframe, val affiliation: Affiliation) extends StatMod[A] {
		def modify(a: A) = func(a)
		val name = "Some Mod"
		val description = Text("blah blah")
	}

	trait GenericStatModMeta extends StatModMeta {
		val priority = 1
		val timeframe = Timeframe.Permanent
		val affiliation = Affiliation.Friendly
	}

	@Test
	def testSimple = {
		val stat = new Stat(Criterea(10)) with GenericDescription
		stat.value should be(10)
	}

	@Test
	def testStatWithChangedBaseCriterea = {
		// start with stat = 10/10
		val base = Criterea(10)
		val stat = new Stat(base) with GenericDescription
		stat.value should be(10)
		// change the base to 5/20, now the stat should be 5/20
		base.value = 5
		stat.value should be(5)
	}

	@Test
	def testStatWithDerivedCriterea = {
		val c1 = Criterea(10)
		val c2 = Criterea(10)
		val base = DependencyTransformer(c1, c2)((a, b) => a + b)
		val stat = new Stat(base) with GenericDescription
		stat.value should be(20)
		c1.value = 5
		stat.value should be(15)
		c2.value = 5
		stat.value should be(10)
	}

	@Test
	def testImmediateNotificationOfStatChange = {
		val c = Criterea(10)
		val stat = new Stat(c) with GenericDescription
		val d = new SimpleDependent[Int]
		stat.addDependent(d)
		d.observed should be(false)
		c.value = 5
		d.observed should be(true)
	}

	@Test
	def testStatWithMod = {
		val stat = new Stat(Criterea(10)) with GenericDescription
		val mod = new SimpleStatMod[Int](_ * 2, priority = 1, Timeframe.Permanent, Affiliation.Friendly)
		val dep = new SimpleDependent[Int]
		stat.addDependent(dep)
		dep.observed should be(false)
		stat += mod
		dep.observed should be(true)
		stat.value should be(20)

		dep.observed = false
		stat -= mod
		dep.observed should be(true)
		stat.value should be(10)
	}

	@Test
	def testStatModPriorities = {
		val stat1 = new Stat(Criterea(10)) with GenericDescription
		val mod1a = new SimpleStatMod[Int](_ * 2, priority = 1, Timeframe.Permanent, Affiliation.Friendly)
		val mod1b = new SimpleStatMod[Int](_ + 5, priority = 2, Timeframe.Permanent, Affiliation.Friendly)
		stat1 += mod1a += mod1b

		val stat2 = new Stat(Criterea(10)) with GenericDescription
		val mod2a = new SimpleStatMod[Int](_ * 2, priority = 2, Timeframe.Permanent, Affiliation.Friendly)
		val mod2b = new SimpleStatMod[Int](_ + 5, priority = 1, Timeframe.Permanent, Affiliation.Friendly)
		stat2 += mod2a += mod2b

		stat1.value should be(25)
		stat2.value should be(30)
	}

	@Test
	def testSamePriorityMods = {
		val stat = new Stat(Criterea(10)) with GenericDescription
		val mod1 = new SimpleStatMod[Int](_ + 10, priority = 2, Timeframe.Permanent, Affiliation.Friendly)
		val mod2 = new SimpleStatMod[Int](_ + 10, priority = 2, Timeframe.Permanent, Affiliation.Friendly)
		stat += mod1 += mod2
		stat.value should be(30)
	}

	@Test
	def testChangingMods = {
		//create a stat with a mod that relies on an external criterea...
		val stat = new Stat(Criterea(10)) with GenericDescription
		val c = Criterea(1)
		val deriveModifier = (multiplier: Int) => { (input: Int) => input * multiplier }
		val mod = new DependentStatMod(deriveModifier, c) with GenericStatModMeta with GenericModDescription
		val dep = new SimpleDependent[Int]
		stat.addDependent(dep)

		//initial conditions
		dep.observed should be(false)
		stat.value should be(10)

		//add the mod: this should fire a change event and set the value of the stat
		stat += mod
		c.value = 2
		dep.observed should be(true)
		stat.value should be(20)

		//change the mod's criterea. This should cascade down to the observer and change the stat value
		dep.observed = false
		c.value = 3
		dep.observed should be(true)
		stat.value should be(30)

		//get rid of the mod. This should revert the stat to its base value
		dep.observed = false
		stat -= mod
		dep.observed should be(true)
		stat.value should be(10)

		//changing the mod's criterea should no longer affect the stat in any way
		dep.observed = false
		c.value = 4
		dep.observed should be(false)
		stat.value should be(10)
	}

	@Test
	def testSimpleDepRep = {
		val stat = new DepRepStat(Criterea(10.0)) with GenericDescription
		stat.currentValue should be(10.0)
		stat.maxValue should be(10.0)

		stat -= 5
		stat.currentValue should be(5.0)
		stat.maxValue should be(10.0)

		stat += 2
		stat.currentValue should be(7.0)
		stat.maxValue should be(10.0)
	}

	@Test
	def testDepRepBeyondBounds = {
		val stat = new DepRepStat(Criterea(10.0)) with GenericDescription
		stat -= 100 //over-damage
		stat.currentValue should be(0.0)
		stat.maxValue should be(10.0)

		stat += 50 //over-heal
		stat.currentValue should be(10.0)
		stat.maxValue should be(10.0)
	}

	@Test
	def testDepRepWithModifiedCriterea = {
		val c = Criterea(10.0)
		val stat = new DepRepStat(c) with GenericDescription
		stat.currentValue should be(10.0)
		stat.maxValue should be(10.0)

		stat -= 2
		stat.currentValue should be(8.0)
		c.value = 20
		stat.currentValue should be(16.0)
		stat.maxValue should be(20.0)

		stat -= 6
		stat.currentValue should be(10.0)
		c.value = 10
		stat.currentValue should be(5.0)
	}

	@Test
	def testDepRepObservableChanges = {
		val d = new HistoryDependent[DepRepValue]
		val c = Criterea(10.0)
		val stat = new DepRepStat(c) with GenericDescription
		stat.addDependent(d)
		val lb = new ListBuffer[DepRepValue]

		stat -= 5
		lb += DepRepValue(5, 10)

		stat += 2
		lb += DepRepValue(7, 10)

		c.value = 20
		lb += DepRepValue(14, 20)

		stat += 6
		lb += DepRepValue(20, 20)

		c.value = 5
		lb += DepRepValue(5, 5)

		c.value = 10
		lb += DepRepValue(10, 10)

		d.history should be(lb.toList)
	}

	@Test
	def testDepRepWithMod = {
		val base = Criterea(0.0)
		val stat = new DepRepStat(base) with GenericDescription
		val d = new HistoryDependent[DepRepValue]
		val lb = new ListBuffer[DepRepValue]
		stat.addDependent(d)

		base.value = 10
		lb += DepRepValue(10, 10)

		val mod = new SimpleStatMod[Double](_ + 100, priority = 2, Timeframe.Permanent, Affiliation.Friendly)
		stat += mod
		lb += DepRepValue(110, 110)

		stat -= 55
		lb += DepRepValue(55, 110)

		stat -= mod
		lb += DepRepValue(5, 10)

		d.history should be(lb.toList)
	}
}