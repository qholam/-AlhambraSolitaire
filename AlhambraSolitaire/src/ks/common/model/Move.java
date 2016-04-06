package ks.common.model;

import ks.common.games.Solitaire;

/**
 * Represents a move within a solitaire game. This class is the abstract base
 * class that provides the basic methods for representing any move within a
 * solitaire game.
 * <p>
 * Each move has a set of parameters, each of which is a Model <code>Element</code>
 * object. These parameters can be set and retrieved using <code>setParameter (String name,
 * Element e)</code> and <code>getParameter (String name)</code>. Note that the name of the
 * parameter in these methods is a case-sensitive string.
 * <p>
 * For each type of move in a solitaire game, you must create a subclass of this class 
 * to represent that move type in the game.
 * <p>
 * The primary use of this class is to provide an interface for undoing moves. However, since
 * the undo and do of moves are so closely related, this class is capable of handling both
 * activities.
 * <p>
 * In general the move object is constructed and then validates the desired move to be made,
 * using suitable parameters as determined by the necessary subclass. Then
 * the Move object is pushed onto the stack of moves played by the Solitaire game. During
 * the Undo phase, each move is popped from this stack, and the effect is undone, using the <code>undo()</code>
 * method.
 * <p>
 * The Move object is responsible for refreshing and/or repainting affected widgets; This can
 * most rapidly be done using the <code>refreshWidgets()</code> method.
 * <p>
 * The Move class is not responsible for the moves generated interactively by the user. In
 * particular, Move objects are only instantiated once it is validated that a given move
 * is allowed and has actually carried it out! This is an IMPORTANT POINT.
 * <p>
 * Creation date: (10/21/01 3:09:47 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public abstract class Move {

	/**
	 * Constructor provided for subclasses.
	 */
	protected Move() { }
	
	/**
	 * Each move knows how to execute itself.
	 * <p>
	 * Note: Each subclass must override this method and return true if the move is
	 * a valid move, false otherwise.
	 * <p>
	 * Creation date: (10/21/01 3:33:39 PM)
	 * @param game   the game being played.
	 * @return boolean
	 * @since V1.6.2
	 */
	public abstract boolean doMove (Solitaire game);
	
	/**
	 * Produce a string representation of the move.
	 * <p>
	 * Representation of the form:
	 * <p>
	 *    [Move class name]
	 * @return java.lang.String
	 */
	public String toString() {
		return "[Move " + this.getClass().getName() + "]";
	}
	
	/**
	 * Each move knows how to undo itself.
	 * <p>
	 * Note: Each subclass must override this method and return true if the move was 
	 * successfully undone; false otherwise.
	 * <p>
	 * Creation date: (10/21/01 3:33:39 PM)
	 * @param game   the game being played.
	 * @return boolean
	 */
	public abstract boolean undo(Solitaire game);
	
	/**
	 * Each move knows whether it is a valid move.
	 * <p>
	 * Note: Each subclass must override this method and return true if the move is
	 * a valid move, false otherwise.
	 * <p>
	 * Creation date: (10/21/01 3:33:39 PM)
	 * @param game   the game being played.
	 * @return boolean
	 */
	public abstract boolean valid (Solitaire game);
}
