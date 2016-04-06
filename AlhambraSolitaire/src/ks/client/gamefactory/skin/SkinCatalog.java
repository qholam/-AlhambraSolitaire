package ks.client.gamefactory.skin;


/**
 * Knows of the skins available to the client.
 * Creation date: (1/5/02 12:32:51 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class SkinCatalog {
	/** Perhaps a better place for this standard skin. */
	public static final String STANDARD_SKIN = "Standard";

	/** Perhaps a better place for this standard skin. */
	public static final String SCROLLING_BALLOONS = "Scrolling Balloons";

	/** Perhaps a better place for this standard skin. */
	public static final String BOUNCING_BALLS = "Bouncing Balls";

	/** Perhaps a better place for this standard skin. */
	public static final String PSYCHADELIC = "Psychadelic";
	
	/** Perhaps a better place for this standard skin. */
	public static final String FIXED_IMAGE = "Fixed Image";

	/** Multiple Balls. */
	public static final String MULTIPLE_BOUNCING_BALLS = "Multiple Bouncing Balls";

	
/**
 * SkinCatalog constructor comment.
 */
public SkinCatalog() {
	super();
}
/**
 * Return Enumeration of available skins.
 * A bit of a hack.
 * Creation date: (1/5/02 12:33:44 PM)
 * @return java.util.Enumeration
 */
public static java.util.Enumeration<String> skins() {
	java.util.Vector<String> v = new java.util.Vector<String>();
	v.addElement (STANDARD_SKIN);
	v.addElement (SCROLLING_BALLOONS);
	v.addElement (BOUNCING_BALLS);
	v.addElement (PSYCHADELIC);
	v.addElement (FIXED_IMAGE);
	v.addElement (MULTIPLE_BOUNCING_BALLS);
	return v.elements();
}
}
