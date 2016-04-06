package ks.common.model;

/**
 * A String-like object that can be set and accessed.
 * 
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class MutableString extends Element {
	
	/** The value being managed. */
	protected String theString = "";

	/** Used to uniquely identify named MutableStrings (if no name is assigned). */
	private static int mutableNameCounter = 1;
/**
 * MutableInteger constructor comment.
 */
public MutableString(String s) {

	this (new String ("MutableString" + mutableNameCounter++), s);

}
/**
 * MutableInteger constructor comment.
 * <p>
 * @since V1.6.8
 */
public MutableString(String name, String value) {
	super();

	if (name == null) {
		name = new String ("MutableString" + mutableNameCounter++);
	}
	
	// set the name. 
	setName (name);

	// keep track of the value.
	theString = new String (value);
}
/**
 * Retrieve value.
 * <p>
 * @return String
 */
public String getValue() {
	return theString;
}
/**
 * Update the value for this entity.
 * <p>
 * Generates modelChanged action if newValue is different from oldValue.
 * <p>
 * There is no effect if newString is <code>null</code>.
 * <p>
 * @param newString String
 */
public void setValue(String newString) {
	// do nothing if the same.
	if (newString == null) return;
	if (newString.equals (theString)) return;
	
	theString = newString;
	hasChanged();  // we have changed state.
}
/**
 * Return value of Mutable String together with its Parent's toString().
 * <p>
 * @return java.lang.String
 */
public String toString() {
	return super.toString() + "=" + theString;
}
}
