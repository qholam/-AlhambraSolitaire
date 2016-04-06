package ks.common.view;

import java.awt.*;

import ks.common.model.Card;
import ks.common.model.Element;
import ks.common.model.Pile;

/**
 * Creates view of a pile of cards (only top one being visible).
 * <p>
 * Cards are extracted from the top of a pile using <code>getCardViewForTopCard()</code> which
 * returns a ColumnView object even though only one card can be extracted at a time.
 * <p>
 * Note: Since v1.6, supports the <code>returnWidget(Widget)</code> interface to reverse
 *       the action of <code>getCardViewForTopCard()</code>.
 * <p>
 * Creation date: (10/27/01 1:20:11 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class PileView extends Widget {
	
	/** Default numbering for PileView names. */
	protected static int pileViewCounter = 1;

	/**
	 * PileView constructor comment.
	 * @param me ks.common.model.Element
	 */
	public PileView(Element me) {
		super(me);

		setName (new String ("PileView" + pileViewCounter++));
	}
	
	/**
	 * If the MouseEvent falls on the top card of this Column, remove the top card
	 * from the Model and return a CardView widget to manage the card as it is dragged
	 * on the screen. This method uses Widget's CardImages to make this calculation.
	 * <p>
	 * Note: the Column Model will be altered by this method if the mouseEvent maps to
	 *       the top card in the Pile View.
	 * <p>
	 * Don't forget to let container know that we are the source for the dragging widget.
	 * @param me java.awt.event.MouseEvent
	 */
	public CardView getCardViewForTopCard(java.awt.event.MouseEvent me) {

		Pile thePile = (Pile) getModelElement();
		int numCards = thePile.count();
		if (numCards == 0) return null;  // no chance in an empty Column!

		// Create a cardView widget for this card.
		Card theCard = thePile.get();
		CardView cv = new CardView (theCard);

		// Set Bounds of this cardView widget to the bounds of the Pile from our own coordinates.
		cv.setBounds (new java.awt.Rectangle (x, y, cards.getWidth(), cards.getHeight()));

		// use the same peer. NOTE: we must do this because we aren't adding this widget. As a dynamic
		// widget, I feel it would be wrong to add this to the static list of widgets for this container.
		// Note that this means that this widget has no recourse to mouse events.
		cv.setContainer (container);

		// remember that we are the originating Widget (since v1.6)
		container.setDragSource (this);

		return cv;  // all set.
	}
	
	/**
	 * redraw Widget image.
	 * <p>
	 * If the Pile is empty, a thin rectangle border is drawn. Any faceup/facedown cards
	 * in the Pile are drawn as appropriate.
	 */
	public void redraw() {
		Pile thePile = (Pile) getModelElement();

		// nothing to do if we have no cards. Be silent because this is something that happens
		// only during TESTING or during race conditions live.
		if (cards == null) {
			return;
		}
		
		// create offscreen Image if not already done so.
		if (offscreenImage == null) {
			if (container == null) {
				System.err.println ("PileView::redraw(). Invalid request to draw a Widget that is not part of a container.");
				return;
			}
			// Create image with width x height of CardImages
			offscreenImage = container.createImage (cards.getWidth(), cards.getHeight());
		}

		// clear to the background color of the viewing peer.
		java.awt.Graphics g = offscreenImage.getGraphics();

		// draw background according to skin visitor.
		container.getVisitor().visit(g, getBounds());

		// Peek at the top card from the pile
		Card c = thePile.peek();
		if (c != null) {
			Image img = null;
			if (c.isFaceUp()) {
				img = cards.getCardImage(c);
			} else {
				img = cards.getCardReverse();
			}
			if (c.isSelected()){
				g.drawImage (img, 0, 0, container);
				Color alpha = new Color(150,150,150, 150);
				g.setColor (alpha);
				g.fillRect (0, 0, cards.getWidth(), cards.getHeight());			
			}else{
				g.setColor (container.getBackground());
				g.fillRect (0, 0, cards.getWidth(), cards.getHeight());			
				g.drawImage (img, 0, 0, container);			
			}
		} else {
			// Create a thin rectangle outline to show outline of the pile.
			g.setColor (java.awt.Color.black);
			g.drawRect (0, 0, cards.getWidth(), cards.getHeight());
		}

		g.dispose();                // no longer needed

		// transfer image once done.
		if (getImage() == null) {
			// first time create image
			setImage (container.createImage (cards.getWidth(), cards.getHeight()));
		}

		Graphics lc = getImage().getGraphics();
		if (lc != null) {
			lc.drawImage (offscreenImage, 0, 0, container);
			lc.dispose();
		}	
	}
	
	/**
	 * Return the CardView widget's model element back onto the underlying Pile.
	 * <p>
	 * If parameter widget is not a CardView object, this method will throw an
	 * IllegalCastException.
	 * <p>
	 * @return boolean
	 * @param w ks.common.view.Widget
	 */
	public boolean returnWidget(Widget w) {
		CardView cardView = (CardView) w;
		Card theCard = (Card) cardView.getModelElement();
		if (theCard == null) {
			System.err.println ("PileView::returnWidget(): somehow CardView model element is null.");
			return false;
		}

		Pile thePile = (Pile) getModelElement();
		if (thePile == null) {
			System.err.println ("PileView::returnWidget(): somehow PileView has no underlying Pile element.");
			return false;
		}

		// Restore our model element's state.
		thePile.add(theCard);

		return true;
	}
}
