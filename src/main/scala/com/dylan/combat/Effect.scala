package com.dylan.combat

sealed trait Effect {
	def source: Option[(Combattant, Skill)]
	def withEffectiveness(e: Double): Effect
}

// Damage
object Damage extends Enumeration {
	val fire, cold, wind, earth, blunt, sharp = Value
	type Kind = Value
}

case class Damage(amount: Double, kind: Damage.Kind, source: Option[(Combattant, Skill)], direct: Boolean = true) extends Effect {
	def withEffectiveness(e: Double) = Damage(amount * e, kind, source, direct)
}

// Heal
case class Heal(amount: Double, source: Option[(Combattant, Skill)]) extends Effect {
	def withEffectiveness(e: Double) = Heal(amount * e, source)
}

//TODO Add Recurring Effect

//TODO Remove Recurring Effect 