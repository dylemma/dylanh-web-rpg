package com.dylan.combat
import com.dylan.combat.skills.CombatSkill
import com.dylan.data.stats.StatModMeta._

sealed abstract class CombatEvent

case class SkillUsed(user: Combattant, skill: CombatSkill, target: Combattant) extends CombatEvent

case object TurnBegan extends CombatEvent
case object TurnEnded extends CombatEvent

case class EnvironmentEffect(skill: CombatSkill, target: Combattant) extends CombatEvent

case class DamageApplied(damage: Damage, affiliation: Affiliation, user: Combattant)

