package com.dylan.combat

case class Damage(amount: Double, kind: DamageKind.Value, direct: Boolean) {
	def *(factor: Double) = Damage(amount * factor, kind, direct)
	def asDirect = Damage(amount, kind, true)
	def asIndirect = Damage(amount, kind, false)
}

object DamageKind extends Enumeration {
	//TODO there will be more damage types eventually
	val physical, elementalFire, elementalWater, elementalAir, elementalEarth = Value
}