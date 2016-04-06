package ks.common.view;

import ks.common.model.Card;

/**
 * Represents a single card.
 * <p>
 * Note: There is no <code>returnWidget(Widget)</code> method because there are
 * no draggable events that would extract a widget from a CardView.
 * <p>
 * Creation date: (10/4/01 6:09:25 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class CardView extends Widget {
	/** 
	 * Default numbering for CardView names 
	 * @since V1.6.10
	 */
	protected int cardViewCounter = 1;
	
	/**
	 * CardView constructor comment.
	 */
	public CardView(Card card) {
		super(card);

		setName (new String ("CardView" + cardViewCounter++));
	}
	
	/**
	 * Simply copy Card image.
	 * @since v1.5.1 properly handles faceup/facedown cards.
	 */
	public void redraw() {

		Card theCard = (Card) getModelElement();
		if (theCard == null) {
			System.err.println ("CardView::redraw() unexpectedly encountered CardView widget with no Card model element.");
			return;
		}

		if (theCard.isFaceUp()) {
			// This redraw simply overwrites with the image coming from the resource.
			setImage (cards.getCardImage (theCard));  // must have an object to fetch Image resource.
		} else {
			setImage (cards.getCardReverse());  // must have an object to fetch Image resource.
		}
	}
}
