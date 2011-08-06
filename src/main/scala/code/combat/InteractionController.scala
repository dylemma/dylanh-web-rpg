/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dylan.controller

/**
 * An Action acts on a State and produces a sequence of StateModifiers which
 * perform the State's reaction to the action.
 */
class InteractionMessage

class Action[S](val message:InteractionMessage, p: =>Double, function: (S)=>Seq[Reaction[S]]) extends Function1[S, Seq[Reaction[S]]] with Ordered[Action[S]]{
	def priority = p
	def apply(state:S) = function(state)
	def compare(that:Action[S]) = (this.priority - that.priority).signum
}

class Reaction[S](val message:InteractionMessage, function:(S)=>S) extends Function1[S, S]{
	def apply(state:S) = function(state)
}

trait ActionGenerator[S]{
	def generateActionList(state:S): Seq[Action[S]]
}

trait InteractionMessageHandler{
	def handleMessage(message:InteractionMessage):Unit
}

abstract class EvolvingStateController[S](initialState:S, endCondition:(S)=>Boolean) extends ActionGenerator[S] with InteractionMessageHandler{
	private var state = initialState
	
	def done = endCondition(state)
	
	def step = {
		var actions = generateActionList(state).sorted
		while(!actions.isEmpty){
			actions match {
				case action :: remainingActions => { //react to the head
						handleMessage(action.message)
						val reactions = action(state)
						for(reaction<-reactions){
							state = reaction(state)
							handleMessage(reaction.message)
						}
						actions = remainingActions
				}
				case Nil => {} // done
			}
		}
		
	}
}
