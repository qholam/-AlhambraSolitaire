package ks.common.model;

/**
 * Representation of a stack of cards that will appear either vertically
 * or horizontally placed on the screen, slightly overlapping each other.
 * <p>
 * Creation date: (9/30/01 10:51:34 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class Column extends Stack {
	
	/** Used to uniquely identify named Columns (if no name is assigned). */
	private int columnNameCounter = 1;

/**
 * Create column with default name.
 */
public Column() {
	super();

	/** Don't forget to assign a name, even if it is a default one. */
	setName ("Column" + columnNameCounter++);
}
/**
 * Construct a column from the given Stack.
 * <p>
 * Creation date: (9/30/01 11:13:53 PM)
 * @param cards   Stack of initial cards.
 */
public Column(Stack cards) {
	this();

	// simply push the stack on
	if (cards == null) {
		throw new IllegalArgumentException ("Column::Column(Stack,name) received null Stack parameter.");
	}
	
	push (cards);
}
/**
 * Construct a column with the specified name from the given Stack.
 * <p>
 * Creation date: (9/30/01 11:13:53 PM)
 * @param name java.lang.String
 */
public Column(Stack cards, String name) {
	super(name);

	// simply push the stack on
	if (cards == null) {
		throw new IllegalArgumentException ("Column::Column(Stack,name) received null Stack parameter.");
	}
	
	push (cards);
}
/**
 * Construct a column with the specified name.
 * <p>
 * Creation date: (9/30/01 11:13:53 PM)
 * @param name java.lang.String
 */
public Column(String name) {
	super(name);
}
/**
 * Return String representation of Column.
 * <p>
 * Creation date: (9/30/01 10:54:11 PM)
 * @return int
 */
public String toString() {
	if (numCards == 0) return "[Column:" + getName() + ":<empty>]";

	StringBuffer sb = new StringBuffer ("[Column:" + getName() + ":");
	for (int i = 0; i < numCards; i++) {
		sb.append (cards[i].toString());
		if (i < numCards-1) sb.append (",");
	}
	sb.append ("]");
	return sb.toString();
}
}
