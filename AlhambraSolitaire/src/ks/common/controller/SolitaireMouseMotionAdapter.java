package ks.common.controller;


import ks.common.games.Solitaire;
import ks.common.view.Container;
import ks.common.view.Widget;

/**
 * Default MouseMotion Adapter for all solitaire plug-ins.
 * <p>
 * This controller will react to MouseDragged events and automatically update
 * the viewing of any widgets that are being dragged within the container by
 * using the <code>standardDragController</code> from the Solitare class.
 * <p>
 * As of Version 1.5.1, methods have been moved from the Solitaire class into the
 * controller where they belonged.
 * <p>
 * Creation date: (10/27/01 4:37:58 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class SolitaireMouseMotionAdapter extends java.awt.event.MouseMotionAdapter {

	/** The game being played. */
	protected Solitaire theGame = null;
	
	/**
	 * SolitaireMouseMotionAdapter constructor comment.
	 */
	public SolitaireMouseMotionAdapter(Solitaire theGame) {
		super();

		this.theGame = theGame;
	}

	/**
	 * Coordinate reaction to a Drag Event.
	 * <p>
	 * Added code for V1.6.10 to refreshWidgets() on the first real drag event. This will ensure
	 * that any changes to the source widget (causing the change) will be refreshed and drawn to
	 * the screen appropriately.
	 * <p>
	 * Creation date: (10/27/01 4:38:53 PM)
	 * @param me java.awt.event.MouseEvent
	 */
	public void mouseDragged(java.awt.event.MouseEvent me) {

		Container c = theGame.getContainer();

		/** first return if nothing is currently being dragged. */
		Widget w = c.getActiveDraggingObject();
		if (w == null) {
			// sanity check. should never happen.
			throw new IllegalArgumentException ("SolitaireMouseMotionAdapter::mouseDragged(). null active dragging object.");
		}
		if (w == Container.getNothingBeingDragged()) return;

		// Find the anchor point within the image to use as delta offsets.
		java.awt.Point anchor = c.getDraggingAnchor();

		w.setXY (me.getX() - anchor.x, me.getY() - anchor.y);	

		c.repaintBackground();
		c.repaint();
	}
}
