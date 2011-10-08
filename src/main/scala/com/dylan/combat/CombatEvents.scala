package com.dylan.combat
import scala.collection.mutable.Publisher
import scala.collection.mutable.Subscriber

/**
 * An event class for damage being applied
 */
case class DamageAppliedEvent[C](target: Target, damage: Damage, context: C)

/**
 * An event class for healing being applied
 */
case class HealAppliedEvent[C](target: Target, heal: Heal, context: C)

/**
 * An event class for recurring effects being added
 */
case class RecurringEffectAddEvent[C](target: Target, effect: AddRecurringEffect, context: C)

/**
 * An event class for recurring effects being removed
 */
case class RecurringEffectRemoveEvent[C](target: Target, effect: RemoveRecurringEffect, context: C)

/**
 * An event class for skill usage
 */
case class SkillUsedEvent[C](skill: Skill, user: Combattant, target: Target, context: C)

trait CombatEventPublisher[C] {
	protected def eventContext: C

	def subscribe(sub: CombatEventSubscriber[C]) = {
		for (s <- sub.skillUseSubscriber) SkillUsePublisher.subscribe(s)
		for (s <- sub.damageSubscriber) DamageAppliedPublisher.subscribe(s)
		for (s <- sub.healSubscriber) HealAppliedPublisher.subscribe(s)
		for (s <- sub.recurringEffectAddSubscriber) RecurringEffectAddPublisher.subscribe(s)
		for (s <- sub.recurringEffectRemoveSubscriber) RecurringEffectRemovePublisher.subscribe(s)
	}

	def unsubscribe(sub: CombatEventSubscriber[C]) = {
		for (s <- sub.skillUseSubscriber) SkillUsePublisher.removeSubscription(s)
		for (s <- sub.damageSubscriber) DamageAppliedPublisher.removeSubscription(s)
		for (s <- sub.healSubscriber) HealAppliedPublisher.removeSubscription(s)
		for (s <- sub.recurringEffectAddSubscriber) RecurringEffectAddPublisher.removeSubscription(s)
		for (s <- sub.recurringEffectRemoveSubscriber) RecurringEffectRemovePublisher.removeSubscription(s)
	}

	/**
	 * Publisher object for Skill Usage Events
	 */
	private object SkillUsePublisher extends Publisher[SkillUsedEvent[C]] {
		def skillUsed(skill: Skill, user: Combattant, target: Target) =
			publish(SkillUsedEvent(skill, user, target, eventContext))
	}

	protected def publishSkillUsed(skill: Skill, user: Combattant, target: Target) = SkillUsePublisher.skillUsed(skill, user, target)

	/**
	 * Publisher object for Damage Application Events
	 */
	private object DamageAppliedPublisher extends Publisher[DamageAppliedEvent[C]] {
		def damageApplied(target: Target, damage: Damage) =
			publish(DamageAppliedEvent(target, damage, eventContext))
	}

	protected def publishDamageApplied(target: Target, damage: Damage) = DamageAppliedPublisher.damageApplied(target, damage)

	/**
	 * Publisher object for Heal Application Events
	 */
	private object HealAppliedPublisher extends Publisher[HealAppliedEvent[C]] {
		def healApplied(target: Target, heal: Heal) =
			publish(HealAppliedEvent(target, heal, eventContext))
	}

	protected def publishHealApplied(target: Target, heal: Heal) = HealAppliedPublisher.healApplied(target, heal)

	/**
	 * Publisher object for RecurringEffect Addition Events
	 */
	private object RecurringEffectAddPublisher extends Publisher[RecurringEffectAddEvent[C]] {
		def effectAdded(target: Target, effect: AddRecurringEffect) =
			publish(RecurringEffectAddEvent(target, effect, eventContext))
	}

	protected def publishEffectAdded(target: Target, effect: AddRecurringEffect) = RecurringEffectAddPublisher.effectAdded(target, effect)

	/**
	 * Publisher object for RecurringEffect Removal Events
	 */
	private object RecurringEffectRemovePublisher extends Publisher[RecurringEffectRemoveEvent[C]] {
		def effectRemoved(target: Target, effect: RemoveRecurringEffect) =
			publish(RecurringEffectRemoveEvent(target, effect, eventContext))
	}

	protected def publishEffectRemoved(target: Target, effect: RemoveRecurringEffect) = RecurringEffectRemovePublisher.effectRemoved(target, effect)
}

trait CombatEventSubscriber[C] {
	def skillUseSubscriber: Option[Subscriber[SkillUsedEvent[C], Any]]
	def damageSubscriber: Option[Subscriber[DamageAppliedEvent[C], Any]]
	def healSubscriber: Option[Subscriber[HealAppliedEvent[C], Any]]
	def recurringEffectAddSubscriber: Option[Subscriber[RecurringEffectAddEvent[C], Any]]
	def recurringEffectRemoveSubscriber: Option[Subscriber[RecurringEffectRemoveEvent[C], Any]]
}

trait DefaultCombatEventSubscriber[C] {
	def skillUseSubscriber = None
	def damageSubscriber = None
	def healSubscriber = None
	def recurringEffectAddSubscriber = None
	def recurringEffectRemoveSubscriber = None
}