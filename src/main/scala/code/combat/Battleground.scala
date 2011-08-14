package code.dylan.combat

import scala.collection.immutable.NumericRange
import com.dylan.data.graphs._

case class Position(x: Double, y: Double) {
	def dist(there: Position) = {
		val dx = there.x - x
		val dy = there.y - y
		math.sqrt(dx * dx + dy * dy)
	}
	def +(rhs: Position) = Position(x + rhs.x, y + rhs.y)
}

case class Adjacency(a: Position, b: Position) {
	override def equals(that: Any) = that match {
		case Adjacency(i, j) => (a == i && b == j) || (a == j && b == i)
		case _ => false
	}
}

object Battleground {

	class GridGraph(xs: NumericRange[Double], ys: NumericRange[Double]) extends Graph[Position, Adjacency] with WeightedEdges[Adjacency, Double] {
		lazy val nodes: Set[Position] = (for (x <- xs; y <- ys) yield Position(x, y)) toSet
		def edges(incidentOn: Position): Set[Adjacency] =
			(for (
				dx <- -xs.step to xs.step by xs.step;
				dy <- -ys.step to ys.step by ys.step;
				if (dx != 0.0 || dy != 0.0);
				neighbor <- Some(incidentOn + Position(dx, dy));
				if (nodes.contains(neighbor))
			) yield Adjacency(incidentOn, incidentOn + Position(dx, dy))) toSet

		lazy val edges: Set[Adjacency] = nodes.map(edges(_)).flatten
		def contains(pos: Position) = xs.contains(pos.x) && ys.contains(pos.y)
		def edgeEnds(edge: Adjacency) = (edge.a, edge.b)
		def weight(edge: Adjacency) = edge.a.dist(edge.b)
	}
}

class Battleground(structure: Graph[Position, Adjacency]) {

}