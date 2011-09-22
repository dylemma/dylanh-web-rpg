package com.dylan.data.geom

import scala.math.Numeric

case class Vector(x: Double, y: Double) {
	override def toString = "{%s, %s}".format(x, y)

	def +(v: Vector) = Vector(x + v.x, y + v.y)
	def -(v: Vector) = Vector(x - v.x, y - v.y)
	def *(d: Double) = Vector(x * d, y * d)
	def /(d: Double) = if (d == 0) throw new IllegalArgumentException("Division by 0") else Vector(x / d, y / d)

	def length = math.sqrt(x * x + y * y)
	def length2 = x * x + y * y

	def dist(v: Vector) = math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y))

	def unit = length match {
		case 0 => Vector(0, 0)
		case l => Vector(x / l, y / l)
	}

	def dot(v: Vector) = x * v.x + y * v.y
}