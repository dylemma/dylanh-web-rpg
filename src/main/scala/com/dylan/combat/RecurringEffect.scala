package com.dylan.combat
import scala.collection.mutable.Subscriber

trait RecurringEffect extends CombatEventSubscriber[CombatControllerFacade] {
	def withEffectiveness(e: Double): RecurringEffect
	def subject: Target
	def expired: Boolean

	/**
	 * Callback method. Called when a combat round begins.
	 */
	def roundBegan: Unit

	/**
	 * Callback method. Called when a combat round ends.
	 */
	def roundEnded: Unit

	/**
	 * Callback method. Called when this effect is added to a Target
	 */
	def effectAdded: Unit

	/**
	 * Called when this effect is removed. `forced` is true if the effect
	 * was forcefully removed, and false if it was removed due to expiration.
	 */
	def effectRemoved(forced: Boolean): Unit

}