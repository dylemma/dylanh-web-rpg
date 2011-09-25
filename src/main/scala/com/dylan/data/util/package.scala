package com.dylan.data

package object util {
	implicit def pimpList[A](list: List[A]) = new PimpedList(list)
}