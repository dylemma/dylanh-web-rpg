package com.dylan.data.stats
import com.dylan.data.meta.Description
import com.dylan.data.algebra.PolynomialFunction

private[stats] object RpgStats {

	// ---------------- STRENGTH ---------------
	val strengthName = "strength"
	val strengthDescription = <span><b>Strength</b> determines the amount of health a player has, and how much damage it deals in melee combat.</span>
	class StrengthStat(base: Double) extends Stat(Criterea(base)) {
		def name = strengthName
		def description = strengthDescription
	}

	// ----------------- ENDURANCE ---------------
	val enduranceName = "endurance"
	val enduranceDescription = <span><b>Endurance</b> determines the amount of health regeneration a player has, as well as how much physical defense it has</span>
	class EnduranceStat(base: Double) extends Stat(Criterea(base)) {
		def name = enduranceName
		def description = enduranceDescription
	}

	// -------------------- INTELLIGENCE --------------------
	val intelligenceName = "intelligence"
	val intelligenceDescription = <span><b>Intelligence</b> directly affects how much mana a player has, as well as how much magic damage it can deal in combat</span>
	class IntelligenceStat(base: Double) extends Stat(Criterea(base)) {
		def name = intelligenceName
		def description = intelligenceDescription
	}

	// ---------------- WILLPOWER ------------------
	val willpowerName = "willpower"
	val willpowerDescription = <span><b>Willpower</b> affects how fast a player's mana regenerates, as well as how much magic defense it has.</span>
	class WillpowerStat(base: Double) extends Stat(Criterea(base)) {
		def name = willpowerName
		def description = willpowerDescription
	}

	// --------------- AGILITY --------------
	val agilityName = "agility"
	val agilityDescription = <span><b>Agility</b> determines how likely a player is to evade physical attacks and how much damage the player deals in ranged combat.</span>
	class AgilityStat(base: Double) extends Stat(Criterea(base)) {
		def name = agilityName
		def description = agilityDescription
	}

	// ------------- SPEED -------------
	val speedName = "speed"
	val speedDescription = <span><b>Speed</b> determines how soon a player takes his turn in combat.</span>
	class SpeedStat(base: Double) extends Stat(Criterea(base)) {
		def name = speedName
		def description = speedDescription
	}

	// ---------------- HP ----------------
	val hitPointsName = "hp"
	val hitPointsDescription = <span><b>HP</b> (or hit points) is how much health a player has. When a player takes damage, its HP drops, and when its HP hits 0, the player is dead</span>
	class HitPointsStat(maxHpCriterea: Criterea[Double]) extends DepRepStat(maxHpCriterea) {
		def name = hitPointsName
		def description = hitPointsDescription
	}

	// ------------ MANA -------------
	val manaName = "mana"
	val manaDescription = <span><b>Mana</b> is the source of a player's magical power. Casting magic spends Mana points</span>
	class ManaStat(maxManaCriterea: Criterea[Double]) extends DepRepStat(maxManaCriterea) {
		def name = manaName
		def description = manaDescription
	}

	// ------------- LEVEL -------------
	val levelName = "level"
	val levelDescription = <span><b>Level</b> is the player's level. As you gain experience, your character will grow in level, becoming more powerful each time</span>
	class LevelStat(levelCriterea: Criterea[Int]) extends Stat(levelCriterea) {
		def name = levelName
		def description = levelDescription
	}
}

case class ValuesPerStat[A](
	valueForStrength: A,
	valueForEndurance: A,
	valueForIntelligence: A,
	valueForWillpower: A,
	valueForAgility: A,
	valueForSpeed: A)

class ExperienceCriterea extends Criterea[Int] {
	protected lazy val initialCritereaValue = 0
	def +=(exp: Int) = value = value + exp
	override def toString = "[%d experience]".format(value)
}

class PerLevelStatBonus(val level: Criterea[Int], val amtPerLevel: Double) extends DependentStatMod[Double, Int]({ lv => in => in + lv * amtPerLevel }, level) with StatModMeta.Bonus {
	val name = "Level Bonus"
	val description = <span>Grants an extra <b>{ amtPerLevel }</b> points per level</span>
	val priority = 0 //should be the highest priority mod in the stat's stack
}

class RpgStats(baseValues: ValuesPerStat[Double], perLevelGains: ValuesPerStat[Double]) {
	//The baseline stats: str, end, int, wil, agi, spd
	val strength = new RpgStats.StrengthStat(baseValues.valueForStrength)
	val endurance = new RpgStats.EnduranceStat(baseValues.valueForEndurance)
	val intelligence = new RpgStats.IntelligenceStat(baseValues.valueForIntelligence)
	val willpower = new RpgStats.WillpowerStat(baseValues.valueForWillpower)
	val agility = new RpgStats.AgilityStat(baseValues.valueForAgility)
	val speed = new RpgStats.SpeedStat(baseValues.valueForSpeed)

	//The derived, depletable stats: hp and mana
	protected def deriveMaxHp(strength: Double, endurance: Double) = strength * 10 + endurance * 5
	protected def deriveMaxMana(intelligence: Double, willpower: Double) = intelligence * 10 + willpower * 5
	val hp = new RpgStats.HitPointsStat(DependencyTransformer(strength, endurance)(deriveMaxHp))
	val mana = new RpgStats.ManaStat(DependencyTransformer(intelligence, willpower)(deriveMaxMana))

	//Experience and Level
	//Experience needed for level `x` is defined as 10(x-1)^2 = 10x^2 - 20x + 10 
	protected val experienceFunction = PolynomialFunction(10, -20, 10)
	def expForLevel(level: Int) = experienceFunction.valueAt(level)
	def levelForExp(exp: Int) = experienceFunction.solveFor(exp).max.floor.toInt
	val experience = new ExperienceCriterea
	val level = new RpgStats.LevelStat(DependencyTransformer(experience)(levelForExp))

	//Level Bonuses
	val strengthPerLevelBonus = new PerLevelStatBonus(level, perLevelGains.valueForStrength)
	val endurancePerLevelBonus = new PerLevelStatBonus(level, perLevelGains.valueForEndurance)
	val intelligencePerLevelBonus = new PerLevelStatBonus(level, perLevelGains.valueForIntelligence)
	val willpowerPerLevelBonus = new PerLevelStatBonus(level, perLevelGains.valueForWillpower)
	val agilityPerLevelBonus = new PerLevelStatBonus(level, perLevelGains.valueForAgility)
	val speedPerLevelBonus = new PerLevelStatBonus(level, perLevelGains.valueForSpeed)

	strength += strengthPerLevelBonus
	endurance += endurancePerLevelBonus
	intelligence += intelligencePerLevelBonus
	willpower += willpowerPerLevelBonus
	agility += agilityPerLevelBonus
	speed += speedPerLevelBonus
}