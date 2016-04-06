package ks.common.controller;

import java.awt.event.MouseEvent;

import ks.common.games.Solitaire;
import ks.common.view.Container;
import ks.common.view.Widget;

/**
 * Default released adapter for use by all solitaire plug-ins.
 * <p>
 * The SolitaireReleasedAdapter is programmed to send back to the dragSource
 * any objects that were in progress when the mouse button was released over
 * the container.
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 * @since V2.0.2
 */
public class SolitaireReleasedAdapter extends java.awt.event.MouseAdapter {

	/** The game. */
	protected Solitaire theGame = null;
	
	/**
	 * SolitaireReleasedAdapter constructor comment.
	 * 
	 * @param   theGame   game under play.
	 */
	public SolitaireReleasedAdapter(Solitaire theGame) {
		super();

		this.theGame = theGame;
	}
	
	/**
	 * Coordinate reaction to the completion of a Drag Event that must be canceled.
	 * <p>
	 * It would have been a bit of a challenge to construct the appropriate move
	 * for a release, because cards in solitaire can be dragged from a number of 
	 * potential card sources. Since V1.6 of Kombat Solitaire, a dragged widget 
	 * can be returned to its originating source; this makes life easier for the 
	 * solitaire developer.
	 * <p>
	 * @param me java.awt.event.MouseEvent
	 */
	public void mouseReleased(MouseEvent me) {
		Container c = theGame.getContainer();

		// Return if there is no card being dragged chosen.
		Widget draggingWidget = c.getActiveDraggingObject(); 
		if (draggingWidget == Container.getNothingBeingDragged()) return;

		// Recover from wherever it came. 
		Widget fromWidget = c.getDragSource();
		if (fromWidget == null) {
			c.releaseDraggingObject();		
			return;
		}

		// Simply return the widget to where it came from.
		fromWidget.returnWidget (draggingWidget);

		// release the dragging object, (container will reset dragSource) and repaint
		c.releaseDraggingObject();		
		c.repaint();
	}
}
