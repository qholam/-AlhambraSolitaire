package ks.common.games;

import ks.common.games.Solitaire;

/**
 * Default undo adapter for use by all solitaire plug-ins.
 * <p>
 * The Solitaire Undo adapter is programmed to receive RightClick mouse events
 * and invoke the <code>undoMove()</code> method from the Solitaire Plug-in.
 * <p>
 * Creation date: (10/27/01 4:32:35 PM)
 * 
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class SolitaireUndoAdapter extends ks.common.controller.UndoAdapter {

	/** The game being played. */
	protected Solitaire theGame = null;

	/**
	 * SolitaireUndoAdapter constructor comment.
	 */
	public SolitaireUndoAdapter(Solitaire theGame) {
		super();

		this.theGame = theGame;
	}

	/**
	 * Undoes the actual move on the game being played. If a particular move
	 * can't be undone, then false is returned.
	 * <p>
	 * This request is common to all solitaire games, and we are leaving
	 * undoMove() in the Solitaire class. Perhaps in the future we might wish to
	 * consolidate it into this controller, but we'll leave it here for now.
	 * <p>
	 * Creation date: (10/27/01 4:33:33 PM)
	 * 
	 * @return boolean
	 */
	public boolean undoRequested() {
		return theGame.undoMove();
	}
}