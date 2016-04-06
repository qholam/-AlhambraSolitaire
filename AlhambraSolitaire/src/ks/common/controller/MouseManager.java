package ks.common.controller;

import java.awt.event.*;
import ks.common.view.Container;

/**
 * Coordinates the response to both Mouse and MouseMotion events by
 * composing two MouseAdapters together.
 *
 * Note that the dragging state must be propagated into the Container because no more than
 * one widget at a time can be dragging and drag controllers may need to ask if anything
 * is being dragged.
 *
 * Because Undo events are signaled through right clicks, they become another event type.
 *
 * Creation date: (10/2/01 11:17:54 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class MouseManager {

	/** Handle Clicked, Pressed, Released, Entered, Exited. */
	protected MouseAdapter eventManager = null;

	/** Handle Moved, Dragged. */
	protected MouseMotionAdapter moveEventManager = null;

	/** Remember our home. */
	protected Container home = null;

	/** Determines whether there is a drag in progress. */
	protected boolean dragging = false;

	/** Coordinates of the last Drag event. */
	protected java.awt.Point lastDrag;

	/** The undo adapter associated with this mouseManager. */
	protected UndoAdapter undoAdapter;
	
	/**
	 * MouseManager constructor comment.
	 */
	public MouseManager(Container c) {
		super();

		// Always remember our container structure so we can propagate state.
		home = c;
	}
	
	/**
	 * Return point of last drag from the container.
	 * Creation date: (10/10/01 12:40:31 AM)
	 * @param newLastDrag java.awt.Point
	 */
	protected java.awt.Point getLastDrag() {
		if (home == null) {
			throw new IllegalArgumentException ("MouseManager::getLastDrag() called when no container is set.");
		}

		// return container status
		return home.getLastDrag ();
	}
	
	/**
	 * Returns the Undo Adapter already in place.
	 *
	 * Creation date: (10/11/01 7:26:51 PM)
	 * @return ks.common.controller.UndoAdapter
	 */
	public UndoAdapter getUndoAdapter() {
		return undoAdapter;
	}
	
	/**
	 * Deliver the event to the proper controller.
	 * <p>
	 * Also maintain the state value of 'dragging'.
	 * <p>
	 * Mouse clicks with the Right button are converted into UNDO events; however,
	 * a right-click event is ignored if any other mouse button is down to prevent
	 * an undo from being requested as another move is currently in the middle of
	 * being made (since V1.6.12).
	 * <p>
	 * To enable troubleshooting, all Exceptions are caught here...
	 * Creation date: (10/2/01 11:21:07 PM)
	 * @param me java.awt.event.MouseEvent
	 */
	public void handleMouseEvent(MouseEvent me) {

		// deal with RIGHT button events separately
		if ((me.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK) {
			if (me.getID() == MouseEvent.MOUSE_PRESSED) {

				// ignore undo requests if a drag is in process...
				if (getLastDrag() != null) {
					// alert user that something will not be processed.
					java.awt.Toolkit.getDefaultToolkit().beep();
					return;
				}

				// undo right away
				if (undoAdapter != null)
					undoAdapter.undoRequested();
			}
			return;
		}

		try {
			switch (me.getID()) {
			/** MouseListener events. */
			case MouseEvent.MOUSE_CLICKED:
				if (eventManager != null) eventManager.mouseClicked(me);
				setLastDrag (null);  // certainly no dragging going on here.
				return;

			case MouseEvent.MOUSE_ENTERED:
				if (eventManager != null) eventManager.mouseEntered(me); 
				return;

			case MouseEvent.MOUSE_EXITED:
				if (eventManager != null) eventManager.mouseExited(me); 
				return;

			case MouseEvent.MOUSE_RELEASED:
				if (eventManager != null) eventManager.mouseReleased(me); 
				setLastDrag (null);  // cancel dragging AFTER processing release
				return;

			case MouseEvent.MOUSE_PRESSED:
				// at this point, we don't know if user is dragging, so we
				// place our bets wisely: First we handle the press event, then
				// we would like to call the initDrag() method, which should take
				// care to draw the moving widget directly on the screen. However,
				// because of a delay in processing, the mouse button may have been
				// rapidly dragged to a new location. Since there is no ability to
				// find where the mouse currently is, we will defer this capability
				// Serious flicker will only occur in those widgets that have both 
				// mouseClicked and mousePressed routines, if not coded properly.
				if (eventManager != null) eventManager.mousePressed(me);
				return;

				/** MouseMotionListener events. */
			case MouseEvent.MOUSE_MOVED:
				if (moveEventManager != null) moveEventManager.mouseMoved (me); 
				return;

			case MouseEvent.MOUSE_DRAGGED:
				if (moveEventManager != null) moveEventManager.mouseDragged(me); 

				// record last drag event (must be AFTER processing) and remember that we are in a drag session.
				setLastDrag (new java.awt.Point (me.getPoint()));
				return;
			}
		} catch (RuntimeException e) {
			// To enable troubleshooting, any runtime exceptions are trapped and output
			System.err.println ("MouseManager::handleMouseEvent trapped Runtime Exception."); 
			e.printStackTrace();
		}
	}
	
	/**
	 * Update the status in the container for this MouseManager
	 * Creation date: (10/10/01 12:40:31 AM)
	 * @param newLastDrag java.awt.Point
	 */
	protected void setLastDrag(java.awt.Point newLastDrag) {
		if (home == null) {
			throw new IllegalArgumentException ("MouseManager::setLastDrag(Point) called when no container is set.");
		}

		// update in container.
		home.setLastDrag (newLastDrag);
	}
	
	/**
	 * Deploy this Mouse Adapter to handle (Click, Press, Release, Entered, Exit)
	 *
	 * Creation date: (10/2/01 11:49:38 PM)
	 * @param ma java.awt.event.MouseAdapter
	 */
	public void setMouseAdapter(MouseAdapter ma) {
		eventManager = ma;
	}
	
	/**
	 * Deploy this Mouse Motion Adapter to handle (Move, Drag)
	 * Creation date: (10/2/01 11:50:25 PM)
	 * @param ma java.awt.event.MouseMotionAdapter
	 */
	public void setMouseMotionAdapter(MouseMotionAdapter ma) {
		moveEventManager = ma;
	}
	
	/**
	 * Sets the Undo Adapter.
	 * Creation date: (10/11/01 7:26:51 PM)
	 * @param newUndoAdapter ks.common.controller.UndoAdapter
	 */
	public void setUndoAdapter(UndoAdapter newUndoAdapter) {
		undoAdapter = newUndoAdapter;
	}
}
