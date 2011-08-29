///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.dylan.data
//
//import scala.math._
//
///**
// * Direction represents a cardinal direction, for use in a Graph.
// */
//case class Direction(name:String)
//object Direction{
//	object Up extends Direction("Up")
//	object Down extends Direction("Down")
//	object Left extends Direction("Left")
//	object Right extends Direction("Right")
//	object UpRight extends Direction("UpRight")
//	object DownLeft extends Direction("DownLeft")
//	object UpLeft extends Direction("UpLeft")
//	object DownRight extends Direction("DownRight")
//	
//	/**
//	 * Note: Trying to define EdgeDirections as `object Up extends EdgeDirection(Down)`
//	 * was leading to `null` in the "opposite" field. `oppositeOf` was created to get
//	 * around that, with the implicit conversion to `DirectionWithOpposite`
//	 */
//	def oppositeOf(dir:Direction):Direction = dir match {
//		case Up => Down
//		case Down => Up
//		case Left => Right
//		case Right => Left
//		case UpRight => DownLeft
//		case DownLeft => UpRight
//		case UpLeft => DownRight
//		case DownRight => UpLeft
//		case _ => throw new IllegalArgumentException(dir + " has no opposite")
//	}
//	
//	case class DirectionWithOpposite(dir:Direction){
//		def opposite = oppositeOf(dir)
//	}
//	
//	implicit def DirectionToDirWithOpposite(dir:Direction) = DirectionWithOpposite(dir)
//	
//	def fromVector(x:Double, y:Double):Direction = {
//		if(y==0 && x==0) throw new IllegalArgumentException
//		if(y == 0){
//			if(x == 0) throw new IllegalArgumentException("Expected nonzero direction: Got " + x + ", " + y)
//			if(x < 0) Left
//			else Right
//		}
//		else if(y > 0){
//			if(x == 0) Up
//			else if(x < 0) UpLeft
//			else UpRight
//		}
//		else {
//			if(x == 0) Down
//			else if(x < 0) DownLeft
//			else DownRight
//		}
//	}
//}
//
//case class Position(x:Double, y:Double){
//	def dist(there:Position) = sqrt((there.x-x)*(there.x-x) + (there.y-y)*(there.y-y))
//	def +(that:Position) = Position(x+that.x, y+that.y)
//}
//
//case class Edge(direction:Direction, length:Double, target:Position)
//object Edge{
//	def apply(start:Position, dir:Position):Edge = {
//		val target = start + dir
//		val length = start.dist(target)
//		val direction = Direction.fromVector(dir.x, dir.y)
//		Edge(direction, length, target)
//	}
//}
