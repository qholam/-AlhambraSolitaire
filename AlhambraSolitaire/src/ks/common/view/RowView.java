package ks.common.view;

import java.awt.*;

import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.ElementListener;
import ks.common.model.Stack;

/**
 * Represents a row of cards on the screen. Note: This defaults to a row of
 * thirteen cards. If more cards are necessary, the RowView simply expands to show
 * them all.
 * <p>
 * A RowView is based on a Column model element, because the only difference is the
 * orientation on the screen. From this Column of cards, the first card drawn on the
 * screen is determined by the method <code>firstDrawn</code> (which defaults to 0 
 * but can be overridden by subclasses). The remaining cards in the column after this 
 * index value are all drawn. The method <code>numCoveredCards()</code> returns the
 * number of covered cards in a RowView.
 * <p>
 * Since V1.6.10, a RowView can be justified LEFT or RIGHT. Put together with the
 * direction, there are now four possibilities. For example, a LEFT oriented 
 * RowView could be flushed with the RIGHT edge of the Widget boundaries.
 *
 * TODO: WHEN EMPTY, DRAW BASED on JUSTIFICATION/DIRECTION
 *
 * @author George Heineman (heineman@cs.wpi.edu)
 */
public class RowView extends Widget implements ElementListener {

	/** The direction of the Row (LEFT or RIGHT [default]). */
	protected int direction = RIGHT;

	/** The justification of the Row (LEFT [default] or RIGHT). */
	protected int justification = LEFT;

	/** Default numbering for RowView names */
	protected static int rowViewCounter = 1;

	/** Widest width seen so far. */
	protected int widestWidth = -1;

	/** LEFT direction for the row. */
	public static final int LEFT = 0;

	/** RIGHT direction for the row. */
	public static final int RIGHT = 1;
	
	/**
	 * RowView constructor.
	 */
	public RowView(Column col) {
		super(col);

		setName (new String ("RowView" + rowViewCounter++));
	}
	
	/**
	 * Return the first card to be drawn. 
	 * <p>
	 * <code>RowView</code> provides a flexible way in which cards may be drawn.
	 * This may be overridden by subclasses, to return any number from 0 to count()-1.
	 */
	protected int firstDrawn () {
		return 0;
	}
	
	/**
	 * If the MouseEvent falls on the top card of this Column, remove the top card
	 * from the Model and return a CardView widget to manage the card as it is dragged
	 * on the screen.
	 * <p>
	 * Note: the Column Model will be altered by this method if the mouseEvent maps to
	 *       the top card in the Column View.
	 * @param me java.awt.event.MouseEvent
	 */
	public CardView getCardViewForTopCard(java.awt.event.MouseEvent me) {

		Column col = (Column) getModelElement();
		int numCards = col.count();
		if (numCards == 0) return null;  // no chance in an empty Column!

		// Determine if the mouse event falls on the top card.
		// Convert Mouse event to local coordinates within the Widget
		java.awt.Point p = new java.awt.Point (me.getX() - x, me.getY() - y);

		// Make sure to adjust for the number of covered cards.
		int deltaX = numCoveredCards() * cards.getOverlap();
		Rectangle r;

		if (direction == LEFT) {
			if (justification == LEFT) {
				r = new Rectangle (0, 0, cards.getWidth(), cards.getHeight());
			} else {
				r = new Rectangle (width - deltaX - cards.getWidth(), 0, cards.getWidth(), cards.getHeight());
			}
		} else {
			if (justification == LEFT) {
				r = new Rectangle (deltaX, 0, cards.getWidth(), cards.getHeight());
			} else {
				r = new Rectangle (width - cards.getWidth(), 0, cards.getWidth(), cards.getHeight());
			}
		}

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
		cv.setContainer (getContainer());

		return cv;  // all set.
	}
	
	/**
	 * Gets the direction of the row (either LEFT or RIGHT).
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * Gets the justification of the row (either flush LEFT or flush RIGHT).
	 */
	public int getJustification() {
		return justification;
	}
	
	/**
	 * If the MouseEvent falls within this row, return a Column of cards
	 * from the Model and return a RowView widget to manage these cards
	 * as they are dragged on the screen.
	 * <p>
	 * Note: the Column Model will be altered by this method.
	 * 
	 * @param me java.awt.event.MouseEvent
	 */
	public RowView getRowView (java.awt.event.MouseEvent me) {

		Column col = (Column) getModelElement();
		int numCards = col.count();
		if (numCards == 0) return null;  // no chance in an empty Column!

		// Determine if the mouse event falls on the top card.
		// Convert Mouse event to local coordinates within the Widget
		java.awt.Point p = new java.awt.Point (me.getX() - x, me.getY() - y);

		// Make sure to adjust for the number of covered cards.
		int overlap = cards.getOverlap();
		int deltaX = numCoveredCards() * overlap;
		Rectangle topR;			  // represents top card
		Rectangle coveredCardsR;  // represents row of covered cards.

		if (direction == LEFT) {
			if (justification == LEFT) {
				topR = new Rectangle (0, 0, cards.getWidth(), cards.getHeight());
				coveredCardsR = new Rectangle (cards.getWidth(), 0, deltaX, cards.getHeight());
			} else {
				topR = new Rectangle (width - deltaX - cards.getWidth(), 0, cards.getWidth(), cards.getHeight());
				coveredCardsR = new Rectangle (width - deltaX, 0, deltaX, cards.getHeight());
			}
		} else {
			if (justification == LEFT) {
				topR = new Rectangle (deltaX, 0, cards.getWidth(), cards.getHeight());
				coveredCardsR = new Rectangle (0, 0, deltaX, cards.getHeight());
			} else {
				topR = new Rectangle (width - cards.getWidth(), 0, cards.getWidth(), cards.getHeight());
				coveredCardsR = new Rectangle (width - cards.getWidth() - deltaX, 0, deltaX, cards.getHeight());
			}
		}

		Rectangle newBounds = topR;  // start with this as our bounds
		int numSelectedCards = 0;
		if (topR.contains (p)) {
			numSelectedCards = 1; // top card
		} else if (coveredCardsR.contains (p)) {
			// all that matters is the direction within covered Rectangle. Note that there is a boundary
			// case to consider. Since we are computing against overlaps, we have to avoid the case where 
			// the selected point is exactly on the top-most card in the overlap. It doesn't happen for 
			// the left-most facing cards because there selecting the left-most point is going to imply 
			// you only have a single card which means we have no strange case to consider. For the 
			// right-facing RowView, this is the point (0,0). To avoid the bizarre case we check
			// for x == 0 and reduce numSelectedCards accordingly.
			if (direction == LEFT) {
				numSelectedCards = 2 + (p.x - coveredCardsR.x)/overlap;
				newBounds.width += (numSelectedCards-1)*overlap;
			} else {
				int partial = coveredCardsR.x + coveredCardsR.width - p.x;
				numSelectedCards = 2 + (partial)/overlap;
				if (p.x == 0) { numSelectedCards--; }
				newBounds.width += (numSelectedCards-1)*overlap;
				newBounds.x -= 	(numSelectedCards-1)*overlap;
			}
		}

		if (numSelectedCards == 0)
			return null;  // wasn't within a valid region, return empty

		// Now we can do something! This selects and extracts cards from
		// the model.
		col.select (numSelectedCards);
		Stack stack = col.getSelected();
		RowView rv = new RowView (new Column (stack));

		// keep similar justification and direction
		rv.setJustification (justification);
		rv.setDirection (direction);

		// before setting the bounds, we must translate back into the global event coordinates (reversing earlier translation)
		// note that we must calculate the bounds for this RowView object in this method.
		newBounds.translate (x,y);
		rv.setBounds (newBounds);

		// use the same peer. NOTE: we must do this because we aren't adding this widget. As a dynamic
		// widget, I feel it would be wrong to add this to the static list of widgets for this container.
		// Note that this means that this widget has no recourse to mouse events.
		rv.setContainer (getContainer());

		return rv;  // all set.
	}
	
	/**
	 * Returns the number of cards currently covered in the output view. Used to evaluate
	 * the MouseEvent.
	 * <p>
	 * Throws IllegalArgumentException if <code>firstDrawn()</code> returns in appropriate
	 * value.
	 * @return int
	 */
	protected int numCoveredCards () {
		Column col = (Column) getModelElement();

		// count - 1 cards are under. But offset by which was drawn first.
		int fd = firstDrawn();
		if (fd < 0) {
			throw new IllegalArgumentException ("RowView::numCoveredCards() received invalid value for firstDrawn. Must be between 0 and " + (int) (col.count() - 1));
		}
		
		return col.count() - 1 - fd;
	}
	
	/**
	 * Redraw a row. Note that redraw must only be called Image.
	 * <p>
	 * This redraw algorithm uses the values returned by <code>firstDrawn</code> and <code>lastDrawn</code>.
	 * In particular, for the default case, [0, size] form the boundaries, but other subclasses
	 * may choose a different subset of cards to draw (in particular <code>FanPile</code>).
	 * <p>
	 * Because RowView objects may fluctuate in size, we never know our maximal width when creating
	 * the image: we start with a default size, but then this may grow... While
	 * we can't expand larger than our original Widget size, we should still
	 * accommodate as much as we can. Thus, we keep a "high water mark" for the
	 * largest width value seen so far, and grow our offscreenImage accordingly.
	 * <p>
	 * To make programming easier, the smallest size a RowView image can be is 13 cards...
	 * <p>
	 * A finely drawn frame is drawn around an empty RowView image.
	 * Also see FanPile
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
		if (col == null) throw new IllegalArgumentException ("RowView::redraw() encountered null Column Model Element.");
		int size = col.count();

		// Determine default values to use
		int cardWidth = cards.getWidth();
		int overlap = cards.getOverlap();

		// calculate our size
		int imageWidth = cards.getWidth();
		if (size > 0) imageWidth += (size - 1) * overlap;

		if (imageWidth > widestWidth) {
			if (widestWidth < 13 * cardWidth) {
				imageWidth = 13 * cardWidth;
			}

			// create offscreen Image if we need more space.
			if (container == null) {
				System.err.println ("RowView::redraw(). Invalid request to draw a Widget that is not part of a container.");
				return;
			}
			offscreenImage = container.createImage (imageWidth, height);
			createWidgetImage = true;
			widestWidth = imageWidth;
		}

		// clear to the background color of the viewing peer. Always fill to the
		// greatest extent seen so far (which is widestWidth).
		java.awt.Graphics g = offscreenImage.getGraphics();

		g.setColor (container.getBackground());
		g.fillRect (0, 0, widestWidth, height);

		// For each card in the Column, draw at increasing offsets, overlapping as we go down.
		// We are always drawing in order of the column: that is, the last card drawn will be
		// the top card in the deck. The only variability, then, is where to draw the first card

		// the start[] array determines the starting point in the iteration for LEFT or RIGHT.
		// going LEFT, we want to start at the location determined by the number of covered cards.
		// going RIGHT, we can safely start flush at the edge.
		// For justified. If a LEFT-Facing Column is RIGHT-justified, we must flush against
		// the edge of the widget; similarly, if a RIGHT-facing column is LEFT justified, flush
		// against the edge.
		int numCovered = numCoveredCards();
		int start[] = {numCovered, 0};
		int totalNum = size - firstDrawn();

		int offset[] = {-1, 1};
		if (size > 0) {
			for (int i = start[direction], ct = 0; ct < totalNum; i += offset[direction], ct++) {
				Card c = col.peek (firstDrawn() + ct);
				Image img;
				if (c.isFaceUp()) {
					img = cards.getCardImage(c);
				} else {
					img = cards.getCardReverse();
				}

				if (direction == LEFT) {
					if (justification == LEFT) {
						g.drawImage (img, i*overlap, 0, container);
					} else {
						g.drawImage (img, width - cards.getWidth() - ct* overlap, 0, container);
					}
				} else {
					if (justification == LEFT) {
						g.drawImage (img, i*overlap, 0, container);
					} else {
						g.drawImage (img, width - cards.getWidth() - numCovered * overlap + i*overlap, 0, container);
					}
				}
			}
		} else {
			// Create a thin rectangle outline to show outline of the Column.
			g.setColor (java.awt.Color.black);
			if (justification == LEFT) {
				g.drawRect (0, 0, cards.getWidth(), cards.getHeight());
			} else {
				g.drawRect (width-cards.getWidth()-1, 0, cards.getWidth(), cards.getHeight());
			}
		}

		g.dispose();              // no longer needed

		// transfer image once done.
		if (createWidgetImage) {
			// create whenever our width extends too far...
			setImage (container.createImage (widestWidth, height));
		}

		Image img = getImage();
		if (img != null) {
			Graphics lc = img.getGraphics();
			if (lc != null) {
				lc.drawImage (offscreenImage, 0, 0, container);
				lc.dispose();
			}
		}
	}
	
	/**
	 * Return the CardView widget's model element back onto the underlying Column. From
	 * a <code>RowView</code> object, it is possible to generate both
	 * <code>CardView</code> and <code>RowView</code> objects.
	 * <p>
	 * @return boolean
	 * @param w ks.common.view.Widget
	 */
	public boolean returnWidget(Widget w) {
		// handle both kinds of actions:
		if (w instanceof CardView) {
			CardView cardView = (CardView) w;
			Card theCard = (Card) cardView.getModelElement();
			if (theCard == null) {
				System.err.println ("RowView::returnWidget(): somehow CardView model element is null.");
				return false;
			}

			Column theCol = (Column) getModelElement();
			if (theCol == null) {
				System.err.println ("RowView::returnWidget(): somehow RowView has no underlying Column element.");
				return false;
			}

			// Restore our model element's state.
			Card replacingCard = (Card) cardView.getModelElement();
			theCol.add(replacingCard);
			return true;
		}

		if (w instanceof RowView) {
			RowView rowView = (RowView) w;
			Column draggingCol = (Column) rowView.getModelElement();
			if (draggingCol == null) {
				System.err.println ("RowView::returnWidget(): somehow RowView model element is null.");
				return false;
			}

			Column theCol = (Column) getModelElement();
			if (theCol == null) {
				System.err.println ("RowView::returnWidget(): somehow RowView has no underlying Column element.");
				return false;
			}

			// Restore our model element's state.
			theCol.push (draggingCol);
			return true;
		}

		// no idea? Must be called by mistake
		System.err.println ("RowView::returnWidget() called within inappropriate widget.");
		return false;
	}
	
	/**
	 * Sets the direction of the row (facing LEFT or RIGHT).
	 * @param newDirection    LEFT or RIGHT direction for widget.
	 */
	public void setDirection(int newDirection) {
		direction = newDirection;
	}
	
	/**
	 * Sets the justification of the row (flush LEFT or flush RIGHT).
	 * @param newJustification    flush LEFT or RIGHT
	 */
	public void setJustification(int newJustification) {
		justification = newJustification;
	}
}
