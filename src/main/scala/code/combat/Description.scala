/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.meta

import scala.xml.NodeSeq

trait Description {
	def name: String
	def description: NodeSeq
}
