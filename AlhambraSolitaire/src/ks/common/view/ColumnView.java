package ks.common.view;

import java.awt.Image;

import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.ElementListener;
import ks.common.model.Stack;

/**
 * Represents a column of cards (face up) on the screen. The space will expand to
 * fit the required image size.
 * <p>
 * Overrides the <code>returnWidget(Widget)</code> method to replace the Card that was
 * extracted during <code>getCardViewForTopCard()</code>
 * <p>
 * Images do not reflect 'selected' status of the underlying cards.
 * @author George Heineman (heineman@cs.wpi.edu)
 */
public class ColumnView extends Widget implements ElementListener {
	/** Default numbering for ColumnView names. */
	protected static int columnViewCounter = 1;
	
	/** Longest height seen so far. */
	protected int longestHeight = -1;

	/** Must record whether the single card being removed was top or bottom. */
	private boolean topRemoved = true;
	
	/**
	 * ColumnView constructor.
	 */
	public ColumnView(Column col) {
		super(col);

		setName (new String ("ColumnView" + columnViewCounter++));
	}
	
	/**
	 * If the MouseEvent falls on the top card of this Column, remove the top card
	 * from the Model and return a CardView widget to manage the card as it is dragged
	 * on the screen. We must pass CardImages as a parameter to make this calculation.
	 * <p>
	 * Note: the Column Model will be altered by this method if the mouseEvent maps to
	 *       the top card in the Column View.
	 * <p>
	 * Note: The Widget returned by this method can be restored back to the underlying 
	 *       model element by invoking <code>returnWidget(Widget)</code> (since v1.6).
	 * @param me java.awt.event.MouseEvent
	 */
	public CardView getCardViewForTopCard(java.awt.event.MouseEvent me) {

		Column col = (Column) getModelElement();
		int numCards = col.count();
		if (numCards == 0) return null;  // no chance in an empty Column!

		// Determine if the mouse event falls on the top card.
		//    1. Convert Mouse event to local coordinates within the Widget
		java.awt.Point p = new java.awt.Point (me.getX() - x, me.getY() - y);

		int deltaY = (numCards - 1) * cards.getOverlap();
		java.awt.Rectangle r = new java.awt.Rectangle (0, deltaY, cards.getWidth(), cards.getHeight());
		if (r.contains (p) == false) return null;  // wasn't on the top card, so return.

		// Now we can do something!
		Card theCard = col.get();
		CardView cv = new CardView (theCard);

		// before setting the bounds, we must translate back into the global event coordinates (reversing earlier translation)
		r.translate (x,y);
		cv.setBounds (r);

		// use the same peer. NOTE: we must do this because we aren't adding this widget. As a dynamic
		// widget, I feel it would be wrong to add this to the static list of widgets for this container.
		// Note that this means that this widget has no recourse to mouse events.
		cv.setContainer (container);

		// remember that we are the originating Widget (since v1.6)
		container.setDragSource (this);

		// remember top or bottom. ONLY GOOD for duration of mouse pressed!
		topRemoved = true;

		return cv;  // all set.
	}
	
	/**
	 * If the MouseEvent falls on the bottom card of this Column, remove the bottom card
	 * from the Model and return a CardView widget to manage the card as it is dragged
	 * on the screen. We must pass CardImages as a parameter to make this calculation.
	 * <p>
	 * Note: the Column Model will be altered by this method if the mouseEvent maps to
	 *       the top card in the Column View.
	 * <p>
	 * Note: The Widget returned by this method can be restored back to the underlying 
	 *       model element by invoking <code>returnWidget(Widget)</code> (since v1.6).
	 * @param me java.awt.event.MouseEvent
	 */
	public CardView getCardViewForBottomCard(java.awt.event.MouseEvent me) {

		Column col = (Column) getModelElement();
		int numCards = col.count();
		if (numCards == 0) return null;  // no chance in an empty Column!

		// Determine if the mouse event falls on the bottom card.
		//    1. Convert Mouse event to local coordinates within the Widget
		java.awt.Point p = new java.awt.Point (me.getX() - x, me.getY() - y);

		java.awt.Rectangle r;
		if (numCards == 1) {
			r = new java.awt.Rectangle (0, 0, cards.getWidth(), cards.getHeight());
			if (r.contains (p) == false) return null;  // wasn't on the bottom card, so return.
		} else {
			r = new java.awt.Rectangle (0, 0, cards.getWidth(), cards.getOverlap());
			if (r.contains (p) == false) return null;  // wasn't on the bottom card, so return.
			
			// once extracted, this WILL be a full-sized card.
			r = new java.awt.Rectangle (0, 0, cards.getWidth(), cards.getHeight());
		}
		
		// only way to insert at bottom...
		Stack st = new Stack();
		while (col.count() > 1) {
			st.add(col.get());
		}
		Card theCard = col.get();
		while (st.count() > 0) {
			col.add(st.get());
		}
		
		CardView cv = new CardView (theCard);
		
		// before setting the bounds, we must translate back into the global event coordinates (reversing earlier translation)
		r.translate (x,y);
		cv.setBounds (r);

		// use the same peer. NOTE: we must do this because we aren't adding this widget. As a dynamic
		// widget, I feel it would be wrong to add this to the static list of widgets for this container.
		// Note that this means that this widget has no recourse to mouse events.
		cv.setContainer (container);

		// remember that we are the originating Widget (since v1.6)
		container.setDragSource (this);

		// remember top or bottom. ONLY GOOD for duration of mouse pressed!
		topRemoved = false;
		
		return cv;  // all set.
	}
	
	/**
	 * If the MouseEvent falls on a non-top card of this Column, construct a column of these
	 * cards and return a CardView widget to manage the column as it is dragged on the screen.
	 * <p>
	 * Note: the Column Model will be altered by this method if the mouseEvent maps to
	 *       the top card in the Column View.
	 * <p>
	 * Note: The Widget returned by this method can be restored back to the underlying model
	 *       element by invoking <code>returnWidget(Widget)</code> (since v1.6).
	 * @param me java.awt.event.MouseEvent
	 */
	public ColumnView getColumnView(java.awt.event.MouseEvent me) {

		Column col = (Column) getModelElement();
		int numCards = col.count();
		if (numCards == 0) return null;  // no chance in an empty Column!

		// Determine if the mouse event falls on the top card.
		//    1. Convert Mouse event to local coordinates within the Widget
		java.awt.Point p = new java.awt.Point (me.getX() - x, me.getY() - y);

		int deltaY = (numCards - 1) * cards.getOverlap();
		java.awt.Rectangle r = new java.awt.Rectangle (0, deltaY, cards.getWidth(), cards.getHeight());

		int count = 0;
		if (r.contains (p) == true) {
			count = 1; // only the top card being returned.
		} else {
			count = numCards - (p.y / cards.getOverlap());
		}

		// too few cards in the column. Nothing to do!
		if (count < 0) {
			return null;
		}

		// Now we can do something!
		col.select (count);
		Stack s = col.getSelected();

		// create a new Column from these cards
		Column retCol = new Column("retCol");
		for (int i = 0; i < s.count(); i++) {
			retCol.add (s.peek (i));
		}

		ColumnView colView = new ColumnView (retCol);

		// before setting the bounds, we must translate back into the global event coordinates (reversing earlier translation)
		r.translate (x,y);
		colView.setBounds (r);

		// take into account the extra height from the selected cards
		int addY = (retCol.count() - 1) * cards.getOverlap();
		colView.setHeight (colView.getHeight() + addY);
		colView.setY (colView.getY() - addY);

		// use the same peer. NOTE: we must do this because we aren't adding this widget. As a dynamic
		// widget, I feel it would be wrong to add this to the static list of widgets for this container.
		// Note that this means that this widget has no recourse to mouse events.
		colView.setContainer (getContainer());

		// remember that we are the originating Widget (since v1.6)
		getContainer().setDragSource (this);

		return colView;  // all set.
	}
	/**
	 * Redraw a ColumnView. Note that redraw must only be called Image.
	 * <p>
	 * If a column is empty, a thin rectangle outline will be drawn showing the base of the column. (since V1.6.10)
	 * <p>
	 * Because ColumnView objects may fluctuate in size, we never know our maximal height
	 * when creating the image: we start with a default size, but then this may grow... While
	 * we can't expand larger than our original Widget size, we should still
	 * accommodate as much as we can. Thus, we keep a "high water mark" for the
	 * largest height value seen so far, and grow our offscreenImage accordingly.
	 * This functionality was added in V1.6.10.
	 * <p>
	 * To make programming easier, the smallest size a ColumnView image can be is 13 cards.
	 * @since v1.5.1 correctly draws faceup/facedown cards.
	 */
	public void redraw() {
		boolean createWidgetImage = false;  // used to determine if we must create our widget image

		// nothing to do if we have no cards. Be silent because this is something that happens
		// only during TESTING or during race conditions live.
		if (cards == null) {
			return;
		}
		
		// Type the model element.
		Column col = (Column) getModelElement();
		if (col == null) throw new IllegalArgumentException ("ColumnView::redraw() encountered null Column Model Element.");

		// Find out how many cards are int the Column
		int size = col.count();
		int cardHeight = cards.getHeight();
		int overlap = cards.getOverlap();

		int imageHeight = cards.getHeight();
		if (size > 0) imageHeight += (size - 1) * overlap;

		// create offscreen Image if necessary.
		if (imageHeight > longestHeight) {
			// ensure that will always have offscreen image of up to 13 cards in height (for easier programming)
			if (longestHeight < 13 * cardHeight) {
				imageHeight = 13 * cardHeight;
			}

			if (container == null) {
				System.err.println ("ColumnView::redraw(). Invalid request to draw a Widget that is not part of a container.");
				return;
			}

			offscreenImage = container.createImage (width, imageHeight);
			createWidgetImage = true;
			longestHeight = imageHeight;
		}

		// clear to the background color of the viewing peer.
		java.awt.Graphics g = offscreenImage.getGraphics();
		g.setColor (container.getBackground());
		g.fillRect (0, 0, width, longestHeight);   // always fill to maximal value seen so far.

		// For each card in the Column, draw at increasing offsets, overlapping as we go down.
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				Card c = col.peek (i);
				Image img;
				if (c.isFaceUp()) {
					img = cards.getCardImage(c);
				} else {
					img = cards.getCardReverse();
				}
				g.drawImage (img, 0, i*overlap, container);
			}
		} else {
			// Create a thin rectangle outline to show outline of the Column.
			g.setColor (java.awt.Color.black);
			g.drawRect (0, 0, cards.getWidth()-1, cards.getHeight()-1);
		}

		// transfer image once done.
		if (createWidgetImage) {
			// Create whenever we are directed to (if height extends...)
			setImage (offscreenImage);
		}

		// graphics object no longer needed.
		g.dispose();
	}
	/**
	 * Return the CardView widget's model element back onto the underlying Column. Since 
	 * a ColumnView can also extract a ColumnView widget, we dynamically handle the
	 * difference here.
	 * <p>
	 * @return boolean
	 * @param w ks.common.view.Widget
	 */
	public boolean returnWidget(Widget w) {
		if (w instanceof ColumnView) {
			ColumnView colView = (ColumnView) w;
			Column theCol = (Column) colView.getModelElement();
			if (theCol == null) {
				System.err.println ("ColumnView::returnWidget(): somehow ColumnView model element is null.");
				return false;
			}

			Column src = (Column) getModelElement();
			if (src == null) {
				System.err.println ("ColumnView::returnWidget(): somehow ColumnView has no underlying Column element.");
				return false;
			}

			// Restore our model element's state, one card at a time.
			for (int i = 0; i < theCol.count(); i++) {
				Card c = theCol.peek (i);
				src.add (c);
			}

			return true;
		}
		
		// must be CardView. But could be either top or bottom.
		CardView cardView = (CardView) w;
		Card theCard = (Card) cardView.getModelElement();
		if (theCard == null) {
			System.err.println ("ColumnView::returnWidget(): somehow CardView model element is null.");
			return false;
		}

		Column src = (Column) getModelElement();
		if (src == null) {
			System.err.println ("ColumnView::returnWidget(): somehow ColumnView has no underlying Column element.");
			return false;
		}

		// Restore our model element's state.
		Card replacingCard = (Card) cardView.getModelElement();
		
		if (topRemoved) {
			src.add(replacingCard);
		} else {
			// only way to insert at bottom...
			Stack st = new Stack();
			while (src.count() > 0) {
				st.add(src.get());
			}
			src.add(replacingCard);
			while (st.count() > 0) {
				src.add(st.get());
			}
		}

		return true;
	}
}
