package ks.common.view;

import ks.common.model.Column;

/**
 * Represents a Fan of n cards from which only a single card may be selected,
 * but there may be more than n cards in the model element column.
 * <p>
 * As soon as a card is selected, the n+1th card (if it exists) is made visible.
 * <p>
 * Closest relative is the RowView. Because FanPileView extends RowView, the returnWidget()
 * method for RowView is used successfully.
 * <p>
 * Creation date: (12/1/01 9:03:44 AM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class FanPileView extends RowView {
	/** Default numbering for FanPileView names. */
	static private int fanPileViewCounter = 1;

	/** Number of cards to show. */
	protected int numCards; 

	/**
	 * Construct a FanPileView of numCards, based on the given Column model element.
	 * <p>
	 * @param numCards    number of cards in this column
	 * @param col         Column of cards
	 */
	public FanPileView(int numCards, Column col) {
		super (col);

		if (numCards < 1) {
			throw new IllegalArgumentException ("FanPileView::FanPileView (n,Col) received an invalid number of cards:" + numCards);
		}
		
		this.numCards = numCards;

		// set default name accordingly
		setName (new String ("FanPileView" + fanPileViewCounter++));
	}
	
	/**
	 * Return the first card to be drawn. In a FanPileView, the first card to be drawn is the
	 * card n cards below the top card.
	 * @since v1.6.10
	 */
	protected int firstDrawn () {
		Column col = (Column) getModelElement();
		int posOfTopCard = col.count();
		posOfTopCard = posOfTopCard - numCards;

		if (posOfTopCard < 0) return 0;
		return posOfTopCard;
	}
}
