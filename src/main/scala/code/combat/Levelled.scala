package com.dylan.battles

import scala.collection.mutable.Publisher

case class LevelUpEvent(newLevel:Int)

/**
 * Trait for objects that can gain experience and level up. Objects using this
 * trait must define `totalExperienceForLevel(level:Int):Long`
 */
trait Levelled extends Publisher[LevelUpEvent]{
	//current level and getter
	private var _currentLevel:Int = 0
	def currentLevel = _currentLevel
	def nextLevel = currentLevel + 1
	//total experience and getter
	private var _totalExperience:Long = 0
	def totalExperience = _totalExperience
	//abstract- tells how much exp is needed to be at a certain level
	def totalExperienceForLevel(level:Int): Long
	//figure out how much exp you need
	def experienceToLevel(level:Int) = totalExperienceForLevel(level) - totalExperience
	def experienceToNext = experienceToLevel(nextLevel)
	//gain exp and level up
	def gainExperience(exp:Long) = {
		_totalExperience += exp
		while(totalExperience >= totalExperienceForLevel(nextLevel)) levelUp
	}
	private def levelUp = {
		_currentLevel += 1
		publish(LevelUpEvent(currentLevel))
	}
}

object Levelled {
	def unapply(item:Levelled) = Some(item.currentLevel)
}