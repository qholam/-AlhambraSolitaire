package ks.common.controller;

/**
 * Class that defines the interface for processing Undo events.
 *
 * Creation date: (10/11/01 7:23:29 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class UndoAdapter { 
	
	/**
	 * UndoAdapter constructor comment.
	 */
	public UndoAdapter() {
		super();
	}
	
	/**
	 * Empty method that will be overridden by subclasses with real functionality
	 *
	 * If a move can't be undone, then false must be returned; otherwise true signals success.
	 * 
	 * Creation date: (10/11/01 7:25:48 PM)
	 */
	public boolean undoRequested() { 
		return false; 
	}
}
