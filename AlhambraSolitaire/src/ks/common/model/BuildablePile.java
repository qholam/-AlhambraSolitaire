package ks.common.model;

import ks.common.model.Stack;

/**
 * A Model Element consisting of a pile of face down cards, on top
 * of which one can build downwards.
 * <p>
 * Creation date: (11/9/01 9:48:33 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class BuildablePile extends Stack {

	/** Used to uniquely identify named BuildablePile objects (if no name is assigned). */
	private static int buildablePileNameCounter = 1;

/**
 * BuildablePile constructor comment.
 */
public BuildablePile() {
	this (null);
}
/**
 * Construct an empty BuildablePile with given name.
 * <p>
 * @param name String
 */
public BuildablePile(String name) {
	super();

	// set name accordingly.
	if (name == null) {
		name = new String ("BuildablePile" + buildablePileNameCounter++);
	}
	
	setName (name);	
}
/**
 * Returns whether the top of the BuildablePile is face up. 
 * <p>
 * Note: an empty BuildablePile is returned as false
 * <p>
 * @return int
 */
public boolean faceUp() {
	if (empty()) return false;

	Card c = peek();
	return c.isFaceUp();
}
/**
 * Flips the faceup state of the topmost card on the BuildablePile.
 * <p>
 * Generates modelChanged action if BuildablePile is not empty.
 * <p>
 * @return boolean if able to flip the card on top of the BuildablePile
 */
public boolean flipCard() {
	if (empty()) return false;

	Card c = get();
	c.setFaceUp (!c.isFaceUp());
	add(c);
	
	hasChanged();   // state has changed
	return true;
}
/**
 * Return the number of cards in this BuildablePile that are face down.
 * <p>
 * @return int
 */
public int getNumFaceDown() {
	int numFaceDown = 0;

	int ct = count();
	for (int i=0; i < ct; i++) {
		Card c = peek (ct-i-1);
		if (c.isFaceUp() == false) {
			numFaceDown++;
		}
	}
	
	return numFaceDown;
}
/**
 * Return the number of cards in this BuildablePile that are face up.
 * <p>
 * @return int
 */
public int getNumFaceUp() {
	return count() - getNumFaceDown();
}

/**
 * Return String representation of BuildablePile.
 * <p>
 * facedown cards are shown with [..]
 */
public String toString() {
	if (numCards == 0) return "[BuildablePile:" + getName() + ":<empty>]";
	
	StringBuffer sb = new StringBuffer ("[BuildablePile:" + getName() + ":");
	for (int i = 0; i < numCards; i++) {
		sb.append (cards[i].toString());
		if (i < numCards-1) sb.append (",");
	}
	sb.append ("]");
	return sb.toString();
}
}
