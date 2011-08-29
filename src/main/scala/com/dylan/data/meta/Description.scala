/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.data.meta

import scala.xml.NodeSeq

trait Description {
	def name: String
	def description: NodeSeq
}
