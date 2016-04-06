package ks.common.games;

import ks.common.model.Move;

/**
 * This is the superclass for all games that provide the necessary interfaces to
 * enable a brute force backtracking algorithm to solve the game.
 * <p>
 * To turn your solitaire plugin into a Solvable Solitaire plugin, simply
 * provide the following method:
 * <p>
 * <code>Enumeration availableMoves()</code>
 * <p>
 * This method should return an Enumeration of Move objects that represent valid
 * moves from the current solitaire state.
 * <p>
 * In addition, you must ensure that your move objects have a
 * <code>boolean doMove(Solitaire game)</code> method. This feature was not
 * ready before the class started, so I have provided in the Move class a
 * default method that returns No moves; simply override this method to make
 * your plugin solvable. The <code>doMove()</code> method effects the given
 * move, and is the opposite of the <code>undo()</code> method in the Move
 * class (or is it vice versa?).
 * <p>
 * To date, numerous plugins provide solvable implementations
 * <p>
 * Creation date: (11/21/01 9:13:44 PM)
 * 
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public interface SolvableSolitaire {

	/**
	 * Return an Enumeration of valid moves currently available.
	 * <p>
	 * 
	 * @return java.util.Enumeration
	 */
	java.util.Enumeration<Move> availableMoves();
}