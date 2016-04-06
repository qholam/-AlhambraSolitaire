package ks.common.view;

import java.awt.Rectangle;

import ks.common.model.Deck;

/**
 * A Widget for displaying a Deck on the screen.
 * <p>
 * Note: <code>returnWidget</code> not implemented for DeckView because there
 * are no drag actions for removing cards from this DeckView.
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */

public class DeckView extends Widget {
	/** Default numbering for DeckView names. */
	protected static int deckViewCounter = 1;
	
	/**
	 * DeckView constructor comment.
	 */
	public DeckView(Deck d) {
		super(d);

		setName (new String ("DeckView" + deckViewCounter++));
	}
	
	/**
	 * An empty DeckView is drawn as a rectangle with a thick border. A non-empty DeckView
	 * shows the reverse image of a card.
	 */
	public void redraw() {
		boolean createWidgetImage = false;  // used to determine if we must create our widget image

		// create offscreen Image if not already done so.
		if (offscreenImage == null) {
			if (container == null) {
				System.err.println ("DeckeView::redraw(). Invalid request to draw a Widget that is not part of a container.");
				return;
			}
			// Create image with width x height of CardImages
			offscreenImage = container.createImage (cards.getWidth(), cards.getHeight());
			createWidgetImage = true;
		}

		// clear to the background color of the viewing peer.
		java.awt.Graphics g = offscreenImage.getGraphics();
		
		Deck d = (Deck) getModelElement();
		if (d.empty()) {
			// Create a black rectangle outline (with border = 4) where deck used to be once empty.
			container.getVisitor().visit(g, getBounds());
			
			// Draw full border 
			g.setColor (java.awt.Color.black);
			g.fillRect (0, 0, getWidth(), 4);
			g.fillRect (0, getHeight()-4, getWidth(), 4);
			g.fillRect (0, 4, 4, getHeight());
			g.fillRect (getWidth()-4, 4, 4, getHeight());
		} else {
			// This redraw simply overwrites with the image coming from the resource.
			g.drawImage (cards.getCardReverse(), 0, 0, container);
		}

		// done with this
		g.dispose();
		
		// transfer image once done. Can't be set until created.
		if (createWidgetImage) {
			setImage (offscreenImage);
		}

	}
}
