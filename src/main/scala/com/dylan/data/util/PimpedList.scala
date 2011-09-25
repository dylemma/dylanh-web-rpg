package com.dylan.data.util

class PimpedList[A](list: List[A]) {

	/**
	 * Inserts `elem` into the first place where it is less than the next element, returning the newly created list.
	 */
	def orderedInsert(elem: A)(implicit ordering: Ordering[A]): List[A] = orderedInsert(elem, list)

	private def orderedInsert(elem: A, list: List[A])(implicit ordering: Ordering[A]): List[A] = list match {
		case head :: tail => {
			import ordering._
			if (elem < head) elem :: head :: tail
			else head :: orderedInsert(elem, tail)
		}
		case Nil => elem :: Nil
	}

	/**
	 * Finds the minimum element in the list and returns a tuple containing that element and a version of the list without that element
	 */
	def removeMin(implicit ord: Ordering[A]): (A, List[A]) = {
		val min = list.iterator.zipWithIndex.minBy(_._1)(ord)
		(min._1, dropIndex(min._2))
	}

	def dropIndex(index: Int) = {
		def helper(remaining: List[A], indexToDrop: Int, currentIndex: Int): List[A] = remaining match {
			case head :: tail => {
				if (indexToDrop == currentIndex) helper(tail, indexToDrop, currentIndex + 1)
				else head :: helper(tail, indexToDrop, currentIndex + 1)
			}
			case Nil => Nil
		}
		helper(list, index, 0)
	}
}