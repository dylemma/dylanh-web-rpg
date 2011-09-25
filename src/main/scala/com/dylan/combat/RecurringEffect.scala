package com.dylan.combat

trait RecurringEffect {
	def turnBegan: Unit
	def turnEnded: Unit

	def effectAdded: Unit
	def effectRemoved: Unit

	//TODO: what kinds of things can the effect listen for?
}