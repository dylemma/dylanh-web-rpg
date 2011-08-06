/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.dylan.battles

import org.junit._
import Assert._
import com.dylan.battles.{Stat, StatMod, StatModAlignment, StatModDuration, StatModSubject, StatModCritereaChanged}
import com.dylan.battles.StatMod._
import com.dylan.meta.Description
import org.scalatest.junit.JUnitSuite
import org.scalatest.junit.ShouldMatchersForJUnit
import scala.xml.Text

class StatRefactorTest extends JUnitSuite with ShouldMatchersForJUnit{

	trait StatDescription extends Description{
		def name = "stat"
		def description = Text("Some generic stat")
	}
	
	trait ModDescription extends Description{
		def name = "Mod"
		def description = Text("Some generic modification")
	}
	
	@Test
	def testBaseStat = {
		val stat = new Stat(5) with StatDescription
		stat.current should be (5)
		stat.max should be (5)
	}
	
	@Test
	def testStatWithOneBonus = {
		val stat = new Stat(5) with StatDescription
		val bonus = StatMod.AddativeBonus(1,5)
		stat += bonus
		assert(stat.current == 10)
		assert(stat.max == 10)
		stat -= bonus
		assert(stat.current == 5)
		assert(stat.max == 5)
	}

	@Test
	def testStatModPrioritiesAndRemovals = {
		val bonus1 = StatMod.AddativeBonus(1,5)
		val bonus2 = StatMod.AddativeBonus(2,5)
		val bonus3 = StatMod.AddativeBonus(3,5)
		val expectedList = List(bonus1, bonus2, bonus3)
		val stat = new Stat(5) with StatDescription
		
		stat += bonus1 += bonus2 += bonus3
		assertEquals(stat.modifications, expectedList)
		stat -= bonus1 -= bonus2 -= bonus3
		assertEquals(stat.modifications, Nil)
		
		stat += bonus3 += bonus2 += bonus1
		assertEquals(stat.modifications, expectedList)
		stat --= (_=>true) //remove all mods
		assertEquals(stat.modifications, Nil)
		
		stat += bonus2 += bonus3 += bonus1
		assertEquals(stat.modifications, expectedList)
		stat --= (_=>true)
		assertEquals(stat.modifications, Nil)
	}
	
	@Test
	def testStatModPrioritiesAgain = {
		val bonus1 = StatMod.AddativeBonus(1,5)
		val bonus2 = StatMod.MultiplicativeBonus(2,2)
		val stat = new Stat(5) with StatDescription
		
		stat += bonus2 += bonus1
		assert(stat.max == 20)
		assert(stat.current == 20)
	}
	
	@Test
	def testDamageAfterBonuses = {
		val bonus = StatMod.AddativeBonus(1,20)
		val damage = StatMod.Damage(20)
		val stat = new Stat(20) with StatDescription
		stat += bonus 
		assert(stat.max == 40)
		assert(stat.current == 40)
		stat += damage
		assert(stat.max == 40)
		assert(stat.current == 20)
		stat -= bonus
		assert(stat.max == 20)
		assert(stat.current == 10)
	}
	
	@Test
	def testDamageBeforeBonuses = {
		val bonus = StatMod.AddativeBonus(1,10)
		val damage = StatMod.Damage(5)
		val stat = new Stat(10) with StatDescription
		stat += damage
		assert(stat.max == 10)
		assert(stat.current == 5)
		assertEquals(stat.modifications, Nil)
		stat += bonus
		assert(stat.max == 20)
		assert(stat.current == 10)
		assertEquals(stat.modifications, List(bonus))
		stat -= bonus
		assert(stat.max == 10)
		assert(stat.current == 5)
		assertEquals(stat.modifications, Nil)
	}
	
	@Test
	def testRestoreStat = {
		val restore = StatMod.Restore(10)
		val damage = StatMod.Damage(10)
		val stat = new Stat(15) with StatDescription
		stat += damage
		assert(stat.max == 15 && stat.current == 5)
		stat += restore
		assert(stat.max == 15 && stat.current == 15)
		assertEquals(stat.modifications, Nil)
	}
	
	@Test
	def testRestoreTooMuch = {
		val restore = StatMod.Restore(100)
		val damage = StatMod.Damage(10)
		val stat = new Stat(15) with StatDescription
		stat += damage
		assert(stat.max == 15 && stat.current == 5)
		stat += restore
		assert(stat.max == 15 && stat.current == 15)
		assertEquals(stat.modifications, Nil)
	}
	
	def tempCurrentBuff(amt:Double) = new StatMod[Double](1, StatModAlignment.Friendly, StatModDuration.Temporary, StatModSubject.Current) 
								with StatMod.ValueModifierAddDouble with ModDescription{ val amount = amt } 
	def tempCurrentMult(amt:Double) = new StatMod[Double](1, StatModAlignment.Friendly, StatModDuration.Temporary, StatModSubject.Current) 
								with StatMod.ValueModifierMultDouble with ModDescription{ val amount = amt } 
	
	@Test
	def testTempCurrentBuffsOverMax = {
		val buff = tempCurrentBuff(10)
		val stat = new Stat(10) with StatDescription
		//test adding and removing the buff
		stat += buff
		assert(stat.max == 10 && stat.current == 20)
		stat -= buff
		assert(stat.max == 10 && stat.current == 10)
	}
	
	@Test
	def testTempCurrentBuffWithDamageBefore = {
		val buff = tempCurrentBuff(10)
		val stat = new Stat(20) with StatDescription
		val damage = StatMod.Damage(10)
		stat += damage
		assert(stat.max == 20 && stat.current == 10)
		stat += buff
		assert(stat.max == 20 && stat.current == 20)
		stat -= buff
		assert(stat.max == 20 && stat.current == 10)
	}
	
	@Test
	def testTempCurrentBuffWithDamageAfter = {
		val stat = new Stat(20) with StatDescription
		val buff = tempCurrentBuff(10)
		val damage = StatMod.Damage(10)
		stat += buff
		assert(stat.max == 20 && stat.current == 30)
		stat += damage
		assert(stat.max == 20 && stat.current == 20)
		stat -= buff
		assert(stat.max == 20 && stat.current == 10)
	}
	
	@Test
	def testTempCurrentMultiplier = {
		val stat = new Stat(20) with StatDescription
		val damage = StatMod.Damage(10)
		val buff = tempCurrentMult(2)
		stat += buff
		assert(stat.max == 20 && stat.current == 40)
		stat += damage
		assert(stat.max == 20 && stat.current == 20)
		stat -= buff
		assert(stat.max == 20 && stat.current == 10)
	}
	
	@Test
	def testChangingBaseSimple = {
		var base = 10
		val stat = new Stat(base) with StatDescription
		assert(stat.max == 10 && stat.current == 10)
		base = 20
		assert(stat.max == 20 && stat.current == 20)
	}
	
	@Test
	def testChanginBaseWithMods = {
		var base = 10
		val stat = new Stat(base) with StatDescription
		val mod = StatMod.AddativeBonus(1,10)
		stat += mod
		assert(stat.max == 20 && stat.current == 20)
		base = 20
		assert(stat.max == 30 && stat.current == 30)
		val damage = StatMod.Damage(15)
		stat += damage
		assert(stat.max == 30 && stat.current == 15)
		base = 10
		assert(stat.max == 20 && stat.current == 10)
		stat -= mod
		assert(stat.max == 10 && stat.current == 5)
	}
	
	class StatModWithCriterea(critereaInitial:Double, p:Int, a:StatModAlignment.Value, d:StatModDuration.Value, s:StatModSubject.Value)
	extends StatMod[Double](p,a,d,s) with ValueModifierAddDouble with ModDescription{
		private var _criterea = critereaInitial
		def criterea = _criterea
		def amount = criterea
		def criterea_= (v:Double) = {_criterea = v; publish(new StatModCritereaChanged("criterea changed to " + v))}
	}
	
	@Test
	def testChangingCritereaMod = {
		var stat = new Stat(10) with StatDescription
		//mod is a buff to the max of the stat
		var mod = new StatModWithCriterea(10, 1, StatModAlignment.Friendly, StatModDuration.Permanent, StatModSubject.Max)
		stat += mod
		stat.max should be(20)
		stat.current should be (20)
		
		mod.criterea = 0
		stat.max should be (10)
		stat.current should be (10)
		
		mod.criterea = 5
		stat.max should be (15)
		stat.current should be (15)
	}
	
	@Test
	def testListenerRemovedFromStat = {
		var stat = new Stat(10) with StatDescription {
			var notifyCount = 0
			override def notify(pub:StatMod[Double], event: StatModCritereaChanged){
				notifyCount += 1
				super.notify(pub, event)
			}
		}
		var mod = new StatModWithCriterea(10, 1, StatModAlignment.Friendly, StatModDuration.Permanent, StatModSubject.Max)
		stat += mod
		stat.max should be(20)
		stat.current should be (20)
		stat.notifyCount should be (0)
		
		mod.criterea = 0
		stat.max should be (10)
		stat.current should be (10)
		stat.notifyCount should be (1)
		
		stat -= mod
		mod.criterea = 5
		stat.max should be (10)
		stat.current should be (10)
		stat.notifyCount should be (1)
	}
	
	@Test
	def testListenersAllRemovedFromStat = {
		val stat = new Stat(10) with StatDescription {
			var notifyCount = 0
			override def notify(pub:StatMod[Double], event: StatModCritereaChanged){
				notifyCount += 1
				super.notify(pub, event)
			}
		}
		val mod1 = new StatModWithCriterea(10, 1, StatModAlignment.Friendly, StatModDuration.Permanent, StatModSubject.Max)
		val mod2 = new StatModWithCriterea(10, 1, StatModAlignment.Friendly, StatModDuration.Permanent, StatModSubject.Max)
		stat += mod1 += mod2
		stat.max should be (30)
		stat.current should be (30)
		stat.notifyCount should be (0)
		
		mod1.criterea = 1
		mod2.criterea = 1
		stat.max should be (12)
		stat.current should be (12)
		stat.notifyCount should be (2)
		
		stat --= (_=>true)
		stat.max should be (10)
		stat.current should be (10)
		stat.notifyCount should be (2)
		
		mod1.criterea = 100
		mod2.criterea = 100
		stat.max should be (10)
		stat.current should be (10)
		stat.notifyCount should be (2)
	}
	
	@Test
	def testCritereaWithStaticMods = {
		var mod1 = new StatModWithCriterea(10, 1, StatModAlignment.Friendly, StatModDuration.Permanent, StatModSubject.Max)
		var mod2 = StatMod.MultiplicativeBonus(2, 2.0)
		val stat = new Stat(10) with StatDescription
		stat += mod2 += mod1
		
		stat.max should be (40)
		stat.current should be (40)
		
		mod1.criterea = 5
		stat.max should be (30)
		stat.current should be (30)
		
		stat -= mod1
		stat.max should be (20)
		stat.current should be (20)
		
		stat += mod1 -= mod2
		stat.max should be (15)
		stat.current should be (15)
	}
	//TODO: make derived stats and test them in similar situations
}
