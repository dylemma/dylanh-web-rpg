package com.dylan.combat

/**
 * An EffectReactor may react with any of three types of responses:
 * - StackPush - means that the EffectStackExecutor needs to push a new
 *               TargettedEffect to the stack
 * - Acceptance - means that the Target has accepted an effect, e.g. taken
 *                damage, been healed, or had an enchant added/removed
 * - Message - means that the EffectStackExecutor needs to react to a message
 */

sealed trait EffectResponse
final case class StackPush(push: TargettedEffect) extends EffectResponse
final case class Acceptance(applied: Effect) extends EffectResponse
final case class Message(message: String) extends EffectResponse