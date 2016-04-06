package ks.common.view;

import java.awt.*;

import ks.common.model.BuildablePile;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Element;
import ks.common.model.Stack;

/**
 * Visually represents a BuildablePile element on the screen. A BuildablePile is
 * a Model Element that contains a pile of cards face down, and then a face up card
 * that can be built into a column.
 * <p>
 * Cards are extracted from a BuildablePileView as ColumnView widgets; thus a single
 * card pulled off a BuildablePileView is still a ColumnView.
 * <p>
 * <code>returnWidget(Widget w)</code> returns the dragged widget back to its source.
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class BuildablePileView extends Widget {

	/** Default numbering for BuildablePileView names */
	protected static int buildablePileViewCounter = 1;

	/** Amount to stack vertical cards. */
	protected int smallOverlap = 6;

	/** Longest height seen so far */
	protected int longestHeight = -1;

	/**
	 * BuildablePileView constructor comment.
	 * @param me ks.common.model.Element
	 */
	public BuildablePileView(Element me) {
		super(me);

		setName (new String ("BuildablePileView" + buildablePileViewCounter++));

		// calculate smallOverlap here?
	}
	
	/**
	 * Returns the small vertical overlap used to place cards one on the other.
	 * 
	 * @return
	 */
	public int getSmallOverlap() {
		return smallOverlap;
	}
	
	/**
	 * If the MouseEvent falls on a faceup card for this BuildablePileView, remove the Column
	 * and return a ColumnView widget to manage the Column as it is dragged
	 * on the screen. 
	 * <p>
	 * If no faceup cards are available, then a null ColumnView is returned.
	 * <p>
	 * Note: The BuildablePile Model will be altered by this method if the mouseEvent maps to
	 *       a faceup card in this model.
	 * <p>
	 * @param me java.awt.event.MouseEvent
	 */
	public ColumnView getColumnView(java.awt.event.MouseEvent me) {

		BuildablePile theBP = (BuildablePile) getModelElement();
		int numCards = theBP.count();
		if (numCards == 0) {
			return null;  // no chance in an empty Column!
		}

		// Determine if the mouse event falls on a faceup Card.
		// Convert Mouse event to local coordinates within the Widget
		Point p = new Point (me.getX() - x, me.getY() - y);

		int numFaceDown = theBP.getNumFaceDown();
		int deltaY = numFaceDown * smallOverlap;
		int numFaceUp = theBP.getNumFaceUp();

		// no chance for returning ColumnView if no faceup cards.
		if (numFaceUp == 0) {
			return null; 
		}

		// wasn't on a faceup Card.
		java.awt.Rectangle r = new java.awt.Rectangle (0, deltaY, cards.getWidth(), deltaY + cards.getHeight() + (numFaceUp-1) * cards.getOverlap());
		if (!r.contains (p)) {
			return null;
		}

		// Select expecting a number from 1..theBP.count(). Since the top card is visible in its
		// entirety, nth may be less than zero (especially if the user clicks towards the bottom of
		// the topmost card. Deal with this situation accordingly.
		int nth = numFaceUp - ((p.y - deltaY) / cards.getOverlap());
		if (nth < 1) nth = 1;

		// update rectangle r to only include the selected cards. Don't forget
		// to shorten height by the number of facedown cards.
		r.y += (numFaceUp - nth) * cards.getOverlap();
		r.height -= (numFaceUp - nth) * cards.getOverlap() + deltaY;

		// Is there any error checking to do here?
		theBP.select (nth);
		Stack s = theBP.getSelected();

		Column col = new Column ();
		col.push (s);

		ColumnView cv = new ColumnView (col);

		// before setting the bounds, we must translate back into the global event coordinates (reversing earlier translation)
		r.translate (x,y);
		cv.setBounds (r);

		// use the same peer. NOTE: we must do this because we aren't adding this widget. As a dynamic
		// widget, I feel it would be wrong to add this to the static list of widgets for this container.
		// Note that this means that this widget has no recourse to mouse events.
		cv.setContainer (container);

		// remember that we are the originating Widget (since v1.6)
		container.setDragSource (this);

		return cv;  // all set.
	}
	
	/**
	 * Redraw a buildable pile. Note that redraw must only be called Image.
	 * The view will be a few pixels wider than a card, to be able to convey
	 * the image of how many cards are upside down in the BuildablePile.
	 * <p>
	 * If empty, a border outline is drawn (since v1.6.10).
	 */
	public void redraw() {
		// determine if we must create our widget image
		boolean createWidgetImage = false;

		// nothing to do if we have no cards. Be silent because this is something that happens
		// only during TESTING or during race conditions live.
		if (cards == null) {
			return;
		}
		
		// Type the model element.
		BuildablePile bp = (BuildablePile) getModelElement();

		// check for null model element
		if (bp == null) {
			throw new IllegalArgumentException("BuildablePileView::redraw() encountered null BuildablePile Model Element.");
		}
		
		// Find out how many cards are int the Column
		int size = bp.count();
		int cardHeight = cards.getHeight();
		int overlap = cards.getOverlap();

		int imageHeight = cards.getHeight();
		if (size > 0) imageHeight += (size - 1) * overlap;

		// create off-screen Image if not already done so.
		if (imageHeight > longestHeight) {
			// ensure that will always have off-screen image of up to 13 cards in height
			if (longestHeight < 13 * cardHeight) {
				imageHeight = 13 * cardHeight;
			}

			if (container == null) {
				System.err.println ("BuildablePileView::redraw(). Invalid request to draw a Widget that is not part of a container.");
				return;
			}
			offscreenImage = container.createImage (width, imageHeight);
			createWidgetImage = true;
			longestHeight = imageHeight;
		}

		// clear to the background color of the viewing peer.
		if (offscreenImage == null) return;  // inability to have image means we can't continue.
		java.awt.Graphics g = offscreenImage.getGraphics();
		
		// draw background according to skin visitor.
		container.getVisitor().visit(g, getBounds());
		
		// Count the cards that are face down in the pile
		int numFaceDown = bp.getNumFaceDown();

		// Draw face down cards, overlapping only slightly
		Image img = cards.getCardReverse();
		boolean anythingDrawn = false;
		for (int i=0; i < numFaceDown; i++) {
			g.drawImage (img, 0, i*smallOverlap, container);
			anythingDrawn = true;
		}

		// For each card in the Column, draw at increasing offsets, overlapping as we go down.
		for (int i = numFaceDown; i < bp.count(); i++) {
			anythingDrawn = true;
			Card c = bp.peek (i);
			img = cards.getCardImage(c);
			g.drawImage (img, 0, numFaceDown * smallOverlap + (i-numFaceDown)*overlap, container);
		}

		if (anythingDrawn == false) {
			// Create a thin rectangle outline to show outline of the BuildablePileView.
			int w = cards.getWidth();
			int h = cards.getHeight();

			// Draw full rectangle
			g.setColor (java.awt.Color.black);
			g.drawRect (0, 0, w, h);
		}

		g.dispose();            // no longer needed	

		// transfer image once done.
		if (createWidgetImage) {
			setImage (container.createImage (width, longestHeight));
		}

		// protect against non-existent images during rapid creation...
		Image imgh = getImage();
		if (imgh != null) {
			Graphics lc = imgh.getGraphics();
			if (lc != null) {
				lc.drawImage (offscreenImage, 0, 0, container);
				lc.dispose();
			}
		}
	}
	
	/**
	 * Return the ColumnView widget's model element back onto the underlying BuildablePile.
	 * <p>
	 * The only dragged Widget from a BuildablePileView is a ColumnView.
	 * <p>
	 * @return boolean
	 * @param w ks.common.view.Widget
	 * @since V1.6.10
	 */
	public boolean returnWidget(Widget w) {
		ColumnView colView = (ColumnView) w;
		Column theCol = (Column) colView.getModelElement();
		if (theCol == null) {
			System.err.println ("BuildablePileView::returnWidget(): somehow BuildablePileView model element is null.");
			return false;
		}

		BuildablePile bp = (BuildablePile) getModelElement();
		if (bp == null) {
			System.err.println ("BuildablePileView::returnWidget(): somehow BuildablePileView has no underlying BuildablePile element.");
			return false;
		}

		// Restore our model element's state, one card at a time, from the bottom up.
		// Note: this will output appropriate Model Changed event.
		for (int i = 0; i < theCol.count(); i++) {
			Card c = theCol.peek (i);
			bp.add (c);
		}

		return true;
	}
}
