package ks.common.view;

import java.awt.Color;
import java.awt.Font;

import ks.common.model.MutableString;

/**
 * A Widget for displaying a string, based on MutableString model.
 * <p>
 *  
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */ 
public class StringView extends Widget {
	/** Default numbering for StringView names. */
	protected static int stringViewCounter = 1;

	/** Default fontsize for this widget [default = 48 point]. */
	protected int fontSize = 48;

	/** Font for this widget [default = 48 point, Plain, Dialog].  */
	protected Font font = new Font ("Dialog", Font.PLAIN, 48);

	/** Color for this widget [default = black].  */
	protected java.awt.Color color = java.awt.Color.black;
	
	/**
	 * Construct view of this String model.
	 */
	public StringView(MutableString value) {
		super(value);

		setName (new String ("StringView" + stringViewCounter++));
	}
	
	/**
	 * Construct view of this String.
	 */
	public StringView(String value) {
		this (new MutableString (value));

		setName (new String ("StringView" + stringViewCounter++));
	}
	
	/**
	 * Get the color used within this StringView.
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Get the font used within this StringView.
	 */
	public Font getFont() {
		return font;
	}
	
	/**
	 * Return size of font being used.
	 */
	public int getFontSize() {
		return fontSize;
	}
	
	/**
	 * Create image if we must. The existing Image is cleared and reused.
	 */
	public void redraw() {

		// Type the Model Element.
		MutableString theString = (MutableString) getModelElement(); 

		if (getImage() == null) {
			/** Create image of the right size. */
			java.awt.Component ct = container;
			if (ct == null) {
				System.err.println ("StringView::redraw() has no container.");
				return;
			}
			setImage (ct.createImage(width, height));
		}

		java.awt.Graphics g = getImage().getGraphics();
		
		// Erase old image
		g.setColor (container.getBackground());
		g.fillRect (0,0, width, height);

		// Set color and font for the numbered image.
		g.setFont (new java.awt.Font ("Dialog", java.awt.Font.PLAIN, fontSize));
		g.setColor (color);
		java.awt.FontMetrics fm = g.getFontMetrics();

		// Draw new string at center of the Widget
		String s = theString.getValue();
		int realWidth = fm.stringWidth (s);

		// Ready to draw the String.
		g.drawString (s, (width-realWidth)/2, (height+fm.getHeight())/2);

		g.dispose();
	}
	
	/**
	 * Set the color for use within this StringView.
	 * 
	 * @param newColor java.awt.Color
	 */
	public void setColor (java.awt.Color newColor) {
		if (newColor == null) {
			throw new IllegalArgumentException ("StringView::setColor() received null parameter.");
		}

		color = newColor;
	}
	
	/**
	 * Set the font for use within this StringView; also updates fontSize
	 * 
	 * @param newFont java.awt.Font
	 */
	public void setFont(java.awt.Font newFont) {
		if (newFont == null) {
			throw new IllegalArgumentException ("StringView::setFont() received null parameter.");
		}

		font = newFont;
		fontSize = newFont.getSize();
	}
	
	/**
	 * Set the size of the font to be used within this StringView. All
	 * other attributes of the existing font are kept the same.
	 * 
	 * @param newFontSize int
	 */
	public void setFontSize(int newFontSize) {
		if (newFontSize < 0) {
			throw new IllegalArgumentException ("StringView::setFontSize() received invalid font size.");
		}

		fontSize = newFontSize;
		font = new Font (font.getFamily(), font.getStyle(), fontSize);

	}
}
