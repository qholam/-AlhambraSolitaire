package ks.common.games;

import java.util.Vector;
import java.util.Enumeration;

import ks.common.model.Move;
import ks.common.view.Container;

/**
 * Starts separate thread to try each available move until the game is over.
 * Make use of the fact that there are no drags, only instantaneous moves. So we
 * can simply call refreshAllWidgets() to ensure all changes are reflected.
 * <p>
 * Creation date: (11/21/01 10:25:52 PM)
 * 
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class SolitaireSolver implements Runnable {

	// record a state of possible moves, and our current one to process.
	class PairMove implements Enumeration<Move> {
		protected int idx; // where in Vector we are.

		protected Vector<Move> moves; // the vector of moves

		public PairMove(Vector<Move> v) {
			this.idx = 0;
			this.moves = v;
		}

		// reset back to first
		public void reset() {
			idx = 0;
		}

		// more to go?
		public boolean hasMoreElements() {
			return idx < moves.size();
		}

		// next Move in the set.
		public Move nextElement() {
			return moves.elementAt(idx++);
		}
	}

	/** Solvable game. */
	protected Solitaire theGame = null;

	/** Solvable game through its SolvableSolitaire interface. */
	protected SolvableSolitaire solvableInterface = null;

	/** Number of moves tried so far. */
	protected int numMoves = 0;

	/** Stop when this is raised to true. */
	protected boolean terminate = false;

	/** Thread in which the loadAllCards will run. */
	protected java.lang.Thread thread = null;

	/**
	 * SolitaireSolver constructor.
	 * <p>
	 * The given Solitaire game must implement <code>SolitaireSolver</code>
	 * otherwise an exception will result.
	 */
	public SolitaireSolver(Solitaire theGame) {
		super();

		if (theGame instanceof SolvableSolitaire) {
			this.theGame = theGame;
			this.solvableInterface = (SolvableSolitaire) theGame;
		} else {
			throw new IllegalArgumentException(
					"SolitaireSolver::SolitaireSolver() given Solitaire plugin that does not implement SolitaireSolver.");
		}
	}

	/**
	 * Do the dirty work. Creation date: (10/2/01 5:17:01 PM)
	 */
	public void run() {

		// keep count.
		numMoves = 0;

		PairMove pm = new PairMove(unpack(solvableInterface.availableMoves()));
		while (pm.hasMoreElements()) {

			if (terminate)
				break; // if user terminates prematurely, stop now.

			Move m = (Move) pm.nextElement();
			if (tryMove(m) == false) {
				// undo and move on...
				// NOTE: can't just ask m to undo, since the game stack won't be
				// updated. We thus ask the game to undo the last move made.
				// Subtle, subtle defect: Nov-11-2003.
				theGame.undoMove();
				theGame.refreshWidgets();
			} else {
				// we have won! Let container know...
				Container cont = theGame.getContainer();
				cont.wonGame();
				break;
			}
		}

		System.err.println("SolitaireSolver tried " + numMoves + " positions.");
	}

	/**
	 * Launch the thread to execute run. Creation date: (10/2/01 5:17:12 PM)
	 */
	public void start() {
		if (thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	/**
	 * Stop this thread.
	 */
	public void stop() {
		terminate = true;
	}

	/**
	 * Try a move. Return true if more moves to be made, false otherwise.
	 * <p>
	 * Note: if a move cannot be undone, this algorithm will still attempt to
	 * move forward.
	 * 
	 * @param m
	 *            the <code>Move</code> object representing the move to
	 *            attempt.
	 */
	protected boolean tryMove(Move m) {

		numMoves++;
		m.doMove(theGame);
		theGame.pushMove(m);
		theGame.refreshWidgets();

		if (theGame.hasWon())
			return true; // if we have won, we are done!

		if (terminate)
			return false; // if user terminates prematurely, stop now.

		PairMove pm = new PairMove(unpack(solvableInterface.availableMoves()));
		while (pm.hasMoreElements()) {
			Move nextMove = (Move) pm.nextElement();
			if (tryMove(nextMove) == false) {
				// we have run out of moves on this track. Undo and move on...
				theGame.undoMove();
			} else {
				return true; // we have won!
			}
		}

		return false;
	}

	/**
	 * Extract Enumeration into a Vector
	 */
	protected Vector<Move> unpack(Enumeration<Move> en) {
		Vector<Move> v = new Vector<Move>();
		if (en == null)
			return v;

		while (en.hasMoreElements()) {
			v.addElement(en.nextElement());
		}
		return v;
	}
}