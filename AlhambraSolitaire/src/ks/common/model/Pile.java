package ks.common.model;

/**
 * Represents a stack of cards where only the topmost card is visible.
 * <p>
 * Creation date: (10/27/01 1:14:50 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class Pile extends Stack {
	
	/** Used to uniquely identify named Piles (if no name is assigned). */
	private static int pileNameCounter = 1;
/**
 * Construct a default Pile object.
 */
public Pile() {
	this (null);
}
/**
 * Construct a Pile with the given name.
 * <p>
 * @param name java.lang.String
 */
public Pile(String name) {
	super();

	// set name accordingly.
	if (name == null) {
		name = new String ("Pile" + pileNameCounter++);
	}
	
	setName (name);	
}
/**
 * Return String representation of Pile.
 * <p>
 * Creation date: (9/30/01 10:54:11 PM)
 */
public String toString() {
	if (numCards == 0) return "[Pile:" + getName() + ":<empty>]";
	
	StringBuffer sb = new StringBuffer ("[Pile:" + getName() + ":");
	for (int i = 0; i < numCards; i++) {
		sb.append (cards[i].toString());
		if (i < numCards-1) sb.append (",");
	}
	sb.append ("]");
	return sb.toString();
}
}
