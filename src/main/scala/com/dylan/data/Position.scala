package com.dylan.data

case class Position(x: Double, y: Double) {
	def dist(there: Position) = {
		val dx = there.x - x
		val dy = there.y - y
		math.sqrt(dx * dx + dy * dy)
	}
	def +(rhs: Position) = Position(x + rhs.x, y + rhs.y)
	def -(rhs: Position) = Position(x - rhs.x, y - rhs.y)
	def length = dist(Position(0, 0))
}

case class Adjacency(a: Position, b: Position) {
	override def equals(that: Any) = that match {
		case Adjacency(i, j) => (a == i && b == j) || (a == j && b == i)
		case _ => false
	}
}