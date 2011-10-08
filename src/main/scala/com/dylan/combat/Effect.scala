package com.dylan.combat

sealed trait Effect {
	/**
	 * The source of an effect may be nothing, or may be from a combattant using a skill.
	 */
	def source: Option[(Combattant, Skill)]

	/**
	 * Return a modified version of this effect, where its effectiveness has been multiplied by a factor of `e`
	 */
	def withEffectiveness(e: Double): Effect
}

// Damage
object Damage extends Enumeration {
	val fire, cold, wind, earth, blunt, sharp, tru = Value
	type Kind = Value
}

case class Damage(amount: Double, kind: Damage.Kind, source: Option[(Combattant, Skill)], direct: Boolean = true) extends Effect {
	def withEffectiveness(e: Double) = Damage(amount * e, kind, source, direct)
}

// Heal
case class Heal(amount: Double, source: Option[(Combattant, Skill)], direct: Boolean) extends Effect {
	def withEffectiveness(e: Double) = Heal(amount * e, source, direct)
}

// Add a RecurringEffect
case class AddRecurringEffect(source: Option[(Combattant, Skill)], effect: RecurringEffect) extends Effect {
	def withEffectiveness(e: Double) = AddRecurringEffect(source, effect.withEffectiveness(e))
}

// Remove a RecurringEffect 
case class RemoveRecurringEffect(source: Option[(Combattant, Skill)], effect: RecurringEffect) extends Effect {
	def withEffectiveness(e: Double) = this
}