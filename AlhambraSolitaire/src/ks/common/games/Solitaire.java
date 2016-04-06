package ks.common.games;

import java.awt.Dimension;
import java.util.Enumeration;

import ks.common.controller.SolitaireMouseMotionAdapter;
import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.model.Element;
import ks.common.model.Model;
import ks.common.model.Move;
import ks.common.model.MutableInteger;
import ks.common.view.CardImages;
import ks.common.view.CardImagesLoader;
import ks.common.view.Container;
import ks.common.view.Widget;

/**
 * The ultimate superclass of all Solitaire games.
 * <p>
 * Each solitaire plugin is a subclass of <code>Solitaire</code>. The most
 * important method for developers is <code>initialize</code> which is invoked
 * when the game is started (or when a new hand is requested).
 * <p>
 * <code>initialize</code> must satisfy three responsibilities:
 * <ol>
 * <li>Initialize the model</li>
 * <li>Initialize the view</li>
 * <li>Initialize the controllers</li>
 * </ol>
 * <p>
 * Most <code>initialize</code> methods will follow this suggested format:
 * <br>
 * 
 * <pre>
 * &lt;blockquote&gt;
 *  public void initialize() {
 *    initializeModel(getSeed());
 *    initializeView();
 *    initializeControllers();
 *  
 *    // Prepare initial game setup.
 *    ...
 *  }
 *  &lt;/blockquote&gt;
 * </pre>
 * 
 * where <code>initializeModel</code>,<code>initializeView</code>, and
 * <code>initializeControllers</code> are protected methods of the plugin.
 * <p>
 * <b>Initialize the Model </b>
 * <p>
 * Each solitaire plugin constructs a model of basic elements to be found in any
 * solitaire variation -- a deck, buildable columns, a pile of cards. Each model
 * element has a name unique to the model. In the example above, note how the
 * <code>initializeModel</code> takes the random seed which will be used to
 * construct the appropriate deck shuffled using a randomized algorithm using
 * this seed value.
 * <p>
 * <b>Initialize the View </b>
 * <p>
 * Each model element can be represented by exactly one View Widget within the
 * Container, the entity in which the solitaire plugin executes. Each View
 * widget is placed at a specific (x,y) location within the coordinates of the
 * container, and has a calculated width, and height. Some Widgets have
 * additional attributes that may be set by the user.
 * <p>
 * <b>Initialize the Controllers </b>
 * <p>
 * The real power of a solitaire plugin is in the way controller objects are
 * constructed and attached to View Widgets. These controllers react to various
 * Mouse Events within the Container -- MousePress, MouseRelease, MouseDrag,
 * MouseMove, MouseEntered, and MouseExited. The controllers manage the user's
 * interaction with the game, and thus the way the game is played.
 * <p>
 * Once a game is constructed, it becomes active within a container by adding it
 * to the container. Each game knows its seed value, as well as the increment by
 * which new seed values are generated with each successive hand.
 * <p>
 * Each Solitaire game has several default model elements, which users can
 * construct views onto and place at whichever (x,y) coordinates they desire:
 * <p>
 * <ol>
 * <li>numberLeft -- MutableInteger</li>
 * <li>score -- MutableInteger</li>
 * </ol>
 * <p>
 * <p>
 * Creation date: (10/1/01 11:35:04 PM)
 * 
 * @author George Heineman (heineman@cs.wpi.edu)
 */
abstract public class Solitaire {

	/** The Container to manager the game. */
	protected Container container = null;

	/** CardImages as allocated by the loader. */
	protected CardImages cardImages = null;
	
	/** Default deck type to use. Override by subclasses. */
	public static final String defaultDeckType = "oxymoron";

	/** random number generator to use for game. */
	protected int seed;

	/** Every Solitaire game has a score named 'score'. */
	protected MutableInteger score;

	/** Name of special score object. */
	public static final String scoreName = "score";

	/** Every Solitaire game records the number of cards left. */
	protected MutableInteger numLeft;

	/** Name of special numLeft object. */
	public static final String numLeftName = "numLeft";

	/** Stack of recent Moves */
	protected java.util.Stack<Move> moves = new java.util.Stack<Move>();

	/** The Model. */
	protected Model model = new Model();

	/**
	 * Default Constructor for Solitaire subclasses
	 */
	protected Solitaire() {
		super();
	}

	/**
	 * Adds the given Model Element to the model maintained by this Solitaire
	 * Variation.
	 * 
	 * @exception IllegalArgumentException
	 *                if given ModelElement is null.
	 * @param me
	 *            The Element that forms part of the Solitiare plugin model.
	 * @return boolean whether Model Element was successfully added.
	 */
	protected boolean addModelElement(Element me) {
		if (me == null) {
			throw new IllegalArgumentException(
					"Solitaire::addModelElement() invoked with null Element.");
		}

		return model.addElement(me);
	}

	/**
	 * Adds the given Widget to the view maintained by this Solitaire Variation.
	 * <p>
	 * Make sure that you have the container remove all widgets before
	 * attempting to reinitialize a solitaire game.
	 * <p>
	 * Every widget being added is automatically registered with the 
	 * default undo adapter and mouseMotion adapters.
	 * 
	 * @param w
	 *            the Widget to add to the visible solitaire plugin.
	 */
	protected void addViewWidget(Widget w) {
		if (container == null) {
			throw new IllegalArgumentException(
					"Solitaire::addModelElement() invoked with null Container.");
		}

		if (w == null) {
			throw new IllegalArgumentException(
					"Solitaire::addModelElement() invoked with null Widget.");
		}

		container.addWidget(w);
		
		// install default undo and mouseMotion adapters.
		w.setUndoAdapter (new SolitaireUndoAdapter(this));
		w.setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
	}

	/**
	 * Return the specific card images to use when calculating the dimensions
	 * within the View.
	 * <p>
	 * 
	 * @return CardImages images to use for this game.
	 */
	public CardImages getCardImages() {
		if (cardImages == null) {
			cardImages = CardImagesLoader.getDeck(getContainer(), getDeckType());
		}
	
		return cardImages;
	}

	/**
	 * Returns the Container of the game.
	 * 
	 * @return The Container in which the solitaire variation is played.
	 */
	public Container getContainer() {
		return container;
	}


	/**
	 * Returns the Model Element maintained by this Solitaire Variation with
	 * desired name.
	 * 
	 * @exception IllegalArgumentException
	 *                if desired name is invalid (i.e., null).
	 * @param name
	 *            A String representing the name of the desired Model Element
	 * @return The Element with the desired name, or null if not found.
	 */
	public Element getModelElement(String name) {
		if (name == null) {
			throw new IllegalArgumentException(
					"Solitaire::getModelElement() invoked with null name.");
		}

		return model.getElement(name);
	}

	/**
	 * Returns all Model Element maintained by this Solitaire variation.
	 * 
	 * @return Enumeration of Element objects.
	 */
	public Enumeration<Element> getModelElements() {
		return model.elements();
	}

	/**
	 * Return all moves made so far within this game.
	 * <p>
	 * 
	 * @return Enumeration Move objects that make up the history of the game.
	 */
	public Enumeration<Move> getMoves() {
		return moves.elements();
	}

	/**
	 * Used to create decks consistently.
	 */
	public int getSeed() {
		return seed;
	}
	
	/**
	 * Every Solitaire variation must return its name.
	 * 
	 * @return String the name of the solitaire variation.
	 */
	public abstract String getName();

	/**
	 * Return the Element representing the number of cards left.
	 * 
	 * @return The MutableInteger Element storing the number of cards left to
	 *         play.
	 */
	public MutableInteger getNumLeft() {
		return numLeft;
	}

	/**
	 * Returns the preferred size needed for this solitaire game to function.
	 * <p>
	 * Each subclass should override this method if more space is needed
	 * initially when the solitaire version is initialized.
	 * <p>
	 * The default value returned is (769, 635).
	 * 
	 * @return java.awt.Dimension the desired dimension of the game.
	 */
	public Dimension getPreferredSize() {
		// default starting dimensions...
		return new Dimension(769, 635);
	}

	/**
	 * Return the model Element representing the score for a solitaire game.
	 * 
	 * @return the MutableInteger Element representing the score for the game.
	 */
	public MutableInteger getScore() {
		return score;
	}

	/**
	 * Return the score for a solitaire game as an int.
	 * 
	 * @return int represents the value of the <code>score</code>
	 *         MutableInteger
	 */
	public int getScoreValue() {
		return score.getValue();
	}

	
	/**
	 * Every Solitaire variation may support a version. By default, this will
	 * always return "1.0";
	 * 
	 * @return String
	 */
	public String getVersion() {
		return "1.0";
	}

	/**
	 * Returns Enumeration of all Widgets maintained by this Solitaire
	 * variation.
	 * 
	 * @return Enumeration of Element objects.
	 */
	public Enumeration<Widget> getWidgets() {
		if (container == null) {
			throw new IllegalArgumentException(
					"Solitaire::getWidgets() invoked with null Container.");
		}

		return container.getWidgets();
	}

	/**
	 * Each solitaire game knows when it has won.
	 * 
	 * @return boolean true if the player has won the game.
	 */
	abstract public boolean hasWon();

	/**
	 * Each solitaire game provides its own initialization routine.
	 * <p>
	 * By the time this method is called, the game is part of its Container.
	 */
	abstract public void initialize();

	/**
	 * Restart solitaire game. Note that next hands will be synchronized
	 * because no solitaire variation uses randomness and the seed 
	 * will be advanced the same across all tables.
	 * 
	 * Seed is simply incremented with each successive hand.
	 */
	public void nextHand() {
		seed++;
		
		// start over
		resetHand();
	}

	/**
	 * Returns most recent move and removes it from list of Moves.
	 * <p>
	 * If there are no moves present in this game, null is returned.
	 * <p>
	 * This method must be protected since only <code>undoMove()</code> should
	 * have access.
	 * <p>
	 * 
	 * @return the most recent Move made in this solitaire game.
	 */
	protected Move popMove() {
		// Return null if the stack of moves is empty.
		if (moves.isEmpty())
			return null;

		// pop most recent.
		return (Move) moves.pop();
	}

	/**
	 * Pushes given move onto our stack of existing moves.
	 * 
	 * @return boolean
	 * @param m
	 *            A Move object representing the most recent Move made in the
	 *            solitaire game.
	 */
	public boolean pushMove(Move m) {
		moves.push(m);
		return true;
	}

	/**
	 * Refresh all elements whose Widgets are now dirty. Not all Widgets are
	 * redrawn.
	 */
	public void refreshWidgets() {
		container.refreshWidgets();
	}


	/**
	 * User has requested to play hand again.
	 */
	public void resetHand() {

		// remove all widgets from the container.
		Container c = getContainer();
		if (c == null) {
			System.err.println("Solitaire::resetHand(): Container is unexpectedly null.");
			return;
		}

		// remove all widgets and model elements to start fresh
		c.removeAllWidgets();
		model.removeAllElements();

		// reset moves
		moves = new java.util.Stack<Move>();

		// set score and numCardsLeft
		setScore();
		setNumberCardsLeft();
		
		// Finally, cover the Container for any events not handled by a widget:
		c.setMouseMotionAdapter(new SolitaireMouseMotionAdapter(this));
		c.setUndoAdapter (new SolitaireUndoAdapter(this));
		c.setMouseAdapter (new SolitaireReleasedAdapter(this));
		
		// re-initialize and redraw everything
		updateGameInformationFromContainer(c);
		initialize();

		c.repaint();
	}

	/**
	 * Set the container in which this Solitaire game will be displayed and be
	 * active.
	 * 
	 * New to V2.2 some solitaire variations need to know the number
	 * of players that are currently playing. Since this information
	 * can be determined from the container, it only makes sense to
	 * update that information upon set.
	 * 
	 * Note that when a game is restarted, this information may 
	 * indeed be invalid, so we take care to do this right.
	 * 
	 * @param cont     the Container in which this solitaire game will be executed.
	 */
	public void setContainer(Container cont) {
		container = cont;
		updateGameInformationFromContainer(container);
	}

	/**
	 * Some solitaire variations need to know specific information
	 * regarding the number of players on a table, for instance.
	 * 
	 * This method is invoked exactly once at the beginning of
	 * a game's initialization.
	 * 
	 * Subclasses should override this method.
	 */
	protected void updateGameInformationFromContainer(Container c) {
		// noop. Done to avoid forcing subclasses to implement noop if not needed.
	}

	/**
	 * Set the model element for number of cards left.
	 * <p>
	 * If this method is called multiple times, only the value is transferred to
	 * the existing model element <code>numLeft</code>.
	 */
	private void setNumberCardsLeft() {
		MutableInteger mu = new MutableInteger (numLeftName, 0);
		if (model.addElement(mu) == false) {
			// just extract value and replace. This invocation will generate a
			// stateChange event.
			numLeft.setValue(mu.getValue());
		} else {
			// otherwise, must set.
			numLeft = mu;
		}
	}

	/**
	 * Set the model element for score of the game.
	 * <p>
	 * If this method is called multiple times, only the value is transferred to
	 * the existing model element <code>score</code>.
	 */
	private void setScore() {
		MutableInteger mu = new MutableInteger (scoreName, 0);
		if (model.addElement(mu) == false) {
			// we already have a score, so transfer value. This 
			// invocation will			// generate a stateChange event.
			score.setValue(mu.getValue());
		} else {
			score = mu;
		}
	}

	/**
	 * Externally impose a seed so multiple instantiations can generate 
	 * same boards.
	 *   
	 * @param seed 
	 */
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	/**
	 * Solitaire game stores all moves and enables them to be undone. Once a
	 * request to undo is received, this takes care of it.
	 * 
	 * @return boolean true means the move was successfully undone; false
	 *         otherwise.
	 */
	public boolean undoMove() {
		Move m = popMove();
		// unable to undo
		if (m == null) {
			// signal our disapproval.
			java.awt.Toolkit.getDefaultToolkit().beep();
			return false;
		}

		// Undo and refresh all widgets.
		boolean status = m.undo(this);
		if (status) {
			refreshWidgets();
		} else {
			// if we can't undo the move, we push it back onto the stack
			pushMove(m);
		}

		// return results.
		return status;
	}

	/**
	 * Add delta to the number of cards left. If delta is negative, we are
	 * removing cards.
	 * <p>
	 * There is no check on the value
	 * 
	 * @param delta
	 *            an integer representing the value to add to the number of
	 *            cards left. A negative number means reduce the number of cards
	 *            left.
	 */
	public void updateNumberCardsLeft(int delta) {
		numLeft.setValue(numLeft.getValue() + delta);
	}

	/**
	 * Add delta to the score. If delta is negative, we are removing points.
	 * <p>
	 * There is no check on the score value.
	 * 
	 * @param delta
	 *            an integer representing the value to add to player's score. A
	 *            negative number means lower the score.
	 */
	public void updateScore(int delta) {
		int sc = score.getValue() + delta;
		score.setValue(sc);
		
		// update changes
        container.updateScore(sc);
	}

	/**
	 * Standard Configuration.getValue(deckTypeVariable) contains deck type.
	 * <p>
	 * Note: overload this method if your plugin requires a different deck type.
	 * <p>
	 * Note: this method is a bit of a hack since it uses the global
	 * Configuration information. Ideally, this should be moved out and 'set'
	 * from the outside instead of 'get' from here in the inside. Ah well...
	 * <p>
	 * 
	 * @since V2.1
	 * @return String deckType to use for this plugin.
	 */
	public String getDeckType() {
		return defaultDeckType;
	}

	/** 
	 * Should a game have specialized resources, then it must override this method to
	 * release them once done.
	 */
	public void dispose() {
		cardImages = null;
	}

}