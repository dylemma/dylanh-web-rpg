package com.dylan.combat

trait Effect {
	def source: Combattant
	def withEffectiveness(e: Double): Effect
}

//TODO Damage

//TODO Heal

//TODO Add Recurring Effect

//TODO Remove Recurring Effect