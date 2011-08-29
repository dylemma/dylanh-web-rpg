package com.dylan.data.stats

trait Dependent[T] {
	def observeCritereaChanged(oldValue: T, newValue: T): Unit
}

trait Criterea[T] {
	protected def initialCritereaValue: T

	private var currentCritereaValue = initialCritereaValue
	def value = currentCritereaValue
	def value_=(newValue: T) = {
		val oldValue = currentCritereaValue
		if (oldValue != newValue) {
			currentCritereaValue = newValue
			dependents.foreach { _.observeCritereaChanged(oldValue, newValue) }
		}
	}
	override def toString = "Criterea[%s]".format(value)

	protected var dependents: Set[Dependent[T]] = Set()
	def addDependent(dependent: Dependent[T]) = dependents = dependents + dependent
	def removeDependent(dependent: Dependent[T]) = dependents = dependents - dependent
}

object Criterea {
	def apply[A](item: A) = new Criterea[A] { def initialCritereaValue = item }
}

/**
 * Helper object which can create a Criterea object out of up to three independent criterea, given a
 * function that can combine them into one "result" value.
 */
object DependencyTransformer {
	trait TransformedCriterea[T] extends Criterea[T] {
		protected def calculateTransformation: T
		lazy val initialCritereaValue = calculateTransformation
		protected class DependencyObserver[A] extends Dependent[A] {
			def observeCritereaChanged(oldValue: A, newValue: A) = value = calculateTransformation
		}
	}

	/**
	 * Create a criterea object that transforms an existing criterea object to provide its own criterea
	 */
	def apply[A, Z](dependency: Criterea[A])(derive: (A) => Z): Criterea[Z] =
		new TransformedCriterea[Z] {
			protected def calculateTransformation = derive(dependency.value)
			dependency.addDependent(new DependencyObserver)
		}

	def apply[A, B, Z](dependency1: Criterea[A], dependency2: Criterea[B])(derive: (A, B) => Z) =
		new TransformedCriterea[Z] {
			protected def calculateTransformation = derive(dependency1.value, dependency2.value)
			dependency1.addDependent(new DependencyObserver)
			dependency2.addDependent(new DependencyObserver)
		}

	def apply[A, B, C, Z](dep1: Criterea[A], dep2: Criterea[B], dep3: Criterea[C])(derive: (A, B, C) => Z) =
		new TransformedCriterea[Z] {
			protected def calculateTransformation = derive(dep1.value, dep2.value, dep3.value)
			dep1.addDependent(new DependencyObserver)
			dep2.addDependent(new DependencyObserver)
			dep3.addDependent(new DependencyObserver)
		}
}