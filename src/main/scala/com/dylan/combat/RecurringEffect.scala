package com.dylan.combat

trait RecurringEffect {
	def turnBegan: Unit
	def turnEnded: Unit

	def effectAdded: Unit

	/**
	 * Called when this effect is removed. `forced` is true if the effect
	 * was forcefully removed, and false if it was removed due to expiration.
	 */
	def effectRemoved(forced: Boolean): Unit

	def skillUsed(skill: Skill, user: Combattant): Unit

	def expired: Boolean

	//TODO: what kinds of things can the effect listen for?
}