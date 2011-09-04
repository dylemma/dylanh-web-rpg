package com.dylan.combat

sealed abstract class ApplicableEffect
//Possible ApplicableEffects include Damage, Healing, and Status (apply a recurring effect, which listens for certain turn events)

class DamageEffect(amount: Double) extends ApplicableEffect
class HealingEffect(amount: Double) extends ApplicableEffect

abstract class EffectEvent
object EffectEvent {
	case object TurnBegin extends EffectEvent
	case object TurnEnd extends EffectEvent
	case object EffectApplied extends EffectEvent
	case object EffectUnapplied extends EffectEvent
	case class CustomEvent(message: Any) extends EffectEvent
}

abstract class RecurringEffect extends ApplicableEffect {
	def respondToEvent(event: EffectEvent) = event match {
		case EffectEvent.TurnBegin => respondToTurnBegin
		case EffectEvent.TurnEnd => respondToTurnEnd
		case EffectEvent.EffectApplied => respondToEffectApplied
		case EffectEvent.EffectUnapplied => respondToEffectUnapplied
		case EffectEvent.CustomEvent(message) => respondToMessage(message)
	}

	def respondToTurnBegin: Unit
	def respondToTurnEnd: Unit
	def respondToEffectApplied: Unit
	def respondToEffectUnapplied: Unit
	def respondToMessage(message: Any): Unit
}

trait EffectTarget {
	def respondTo(effect: ApplicableEffect): TargetResponse
}

sealed abstract class TargetResponse

class AcceptingResponse(execute: Unit) extends TargetResponse
class RejectingResponse extends TargetResponse
class RetaliationResponse(retaliation: ApplicableEffect, target: EffectTarget) extends TargetResponse
class SideEffectResponse(execute: Unit) extends TargetResponse
class MultipleResponse(responses: TargetResponse*) extends TargetResponse