package com.dylan.data.algebra

trait PolynomialFunction {
	def valueAt(x: Double): Double
	def solveFor(y: Double): List[Double]
}

object PolynomialFunction {
	def apply(c: Double): PolynomialFunction = new ZeroOrderFunction(c)
	def apply(a: Double, b: Double): PolynomialFunction = {
		if (a == 0) apply(b)
		else new FirstOrderFunction(a, b)
	}
	def apply(a: Double, b: Double, c: Double): PolynomialFunction = {
		if (a == 0) apply(b, c)
		else new SecondOrderFunction(a, b, c)
	}

	/**
	 * Function in the form of y = c
	 */
	class ZeroOrderFunction(c: Double) extends PolynomialFunction {
		def valueAt(x: Double) = c
		def solveFor(y: Double) = List(0.0)
	}

	/**
	 * Function in the form of y = ax+b
	 */
	class FirstOrderFunction(a: Double, b: Double) extends PolynomialFunction {
		def valueAt(x: Double) = a * x + b
		def solveFor(y: Double) = List((y - b) / a)
		override def toString = a + " * x + " + b
	}

	/**
	 * Function in the form of y = ax^2 + bx + c
	 */
	class SecondOrderFunction(a: Double, b: Double, c: Double) extends PolynomialFunction {
		def valueAt(x: Double) = a * x * x + b * x + c
		def solveFor(y: Double): List[Double] = {
			val desc = math.sqrt(b * b - 4 * a * c)
			//ignore that
			val descriminant = math.sqrt(b * b - 4 * a * (c - y))
			val soln1 = (-b + descriminant) / (2 * a)
			val soln2 = (-b - descriminant) / (2 * a)
			List(soln1, soln2)
		}
	}
}