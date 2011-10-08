package com.dylan.combat

import scala.collection.mutable.Stack

trait EffectStackExecutor {
	def executeEffects(effects: List[TargettedEffect]): Unit = executeEffects(Stack(effects.reverse: _*))

	def executeEffects(effects: Stack[TargettedEffect]) = {
		while (!effects.isEmpty) {
			val te = effects.pop
			val responses = te.target.react(te.effect)
			for (resp <- responses) resp match {
				case r @ StackPush(item) => effects.push(item)
				case r @ Message(msg) => messageRecieved(te.target, msg)
				case r @ Acceptance(applied) => effectApplied(te.target, applied)
			}
		}
	}

	protected def messageRecieved(source: Target, message: String): Unit
	protected def effectApplied(target: Target, effect: Effect): Unit
}