package ks.common.view;

import java.awt.*;

import ks.common.model.MutableInteger;

/**
 * A Widget for displaying an integer (such as score or number of cards left).
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class IntegerView extends Widget {
	/** Default numbering for IntegerView names. */
	protected static int integerViewCounter = 1;

	/** Default fontsize for this widget [default = 48 point]. */
	protected int fontSize = 48;

	/** Font for this widget [default = 48 point, Plain, Dialog].  */
	protected Font font = new Font ("Dialog", Font.PLAIN, 48);

	/** Color for this widget [default = black].  */
	protected java.awt.Color color = java.awt.Color.black;
	
	/**
	 * Construct view of this Integer model.
	 */
	public IntegerView(MutableInteger value) {
		super(value);

		setName (new String ("IntegerView" + integerViewCounter++));
	}
	
	/**
	 * Get the color used within this IntegerView.
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * Get the font used within this IntegerView.
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
		MutableInteger theInteger = (MutableInteger) getModelElement(); 

		if (getImage() == null) {
			/** Create image of the right size. */
			java.awt.Component ct = container;
			if (ct == null) {
				System.err.println ("IntegerView::redraw() has no container.");
				return;
			}
			setImage (ct.createImage(width, height));
		}

		java.awt.Graphics g = getImage().getGraphics();

		// Erase old image

		// draw background according to skin visitor.
		container.getVisitor().visit(g, getBounds());

		// Set color and font for the numbered image.
		g.setFont (new java.awt.Font ("Dialog", java.awt.Font.PLAIN, fontSize));
		g.setColor (color);
		java.awt.FontMetrics fm = g.getFontMetrics();

		// Draw new number at center of the Widget
		String s = Integer.toString (theInteger.getValue());
		int realWidth = fm.stringWidth (s);

		// Ready to draw the String.
		g.drawString (s, (width-realWidth)/2, (height+fm.getHeight())/2);
		
		// done with this.
		g.dispose();
	}
	
	/**
	 * Set the color for use within this IntegerView.
	 * 
	 * @param newColor desired new color to use
	 */
	public void setColor (java.awt.Color newColor) {
		if (newColor == null) {
			throw new IllegalArgumentException ("IntegerView::setColor() received null parameter.");
		}

		color = newColor;
	}
	
	/**
	 * Set the font for use within this IntegerView; also updates fontSize.
	 * 
	 * @param newFont desired font family to use
	 */
	public void setFont(java.awt.Font newFont) {
		if (newFont == null) {
			throw new IllegalArgumentException ("IntegerView::setFont() received null parameter.");
		}

		font = newFont;
		fontSize = newFont.getSize();
	}
	
	/**
	 * Set the size of the font to be used within this IntegerView. All
	 * other attributes of the existing font are kept the same.
	 * 
	 * @param newFontSize desired new font size
	 */
	public void setFontSize(int newFontSize) {
		if (newFontSize < 0) {
			throw new IllegalArgumentException ("IntegerView::setFontSize() received invalid font size.");
		}

		fontSize = newFontSize;
		font = new Font (font.getFamily(), font.getStyle(), fontSize);
	}
}
