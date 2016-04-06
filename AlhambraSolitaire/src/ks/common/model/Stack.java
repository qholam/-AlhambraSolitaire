package ks.common.model;

/**
 * Models a stack of cards.
 * <p>
 * A stack of cards is a linear sequence of cards with a bottom (position 0) and a top
 * card (position n-1 where n is the number of cards in the stack). Each card 
 * maintains its own state (i.e., its value, whether it is face up, whether it is 
 * selected).
 * <p>
 * Cards can be added to the top of the stack with the <code>add (Card c)</code> 
 * or the <code>push (Stack s)</code> method. If a <code>Stack</code> of cards is 
 * pushed onto the top of the stack, the bottom-most card of the stack s is added 
 * to the top of the target stack. Any state changing operation in the stack (add,
 * get, push, getSelected, select) resets the selected status of all cards in the 
 * stack.
 * <p>
 * The <code>Card get()</code> method removes and returns the top card in the stack.
 * <p>
 * The <code>boolean select()</code> method selects just the top card in the stack.
 * The <code>boolean select(int n)</code> method selects the top n cards, if 
 * available. The selected state of the desired cards is altered.
 * <p>
 * The <code>Stack getSelected()</code> method removes and returns a stack of cards,
 * s, from the top of the stack such that all cards in s are selected, if any are 
 * selected.
 * <p>
 * Stack has some basic helper methods that may prove useful, such as 
 * <code>alternatingColors, ascending, descending, sameColor, sameRank, 
 * sameSuit</code>. The methods <code>rank()</code> and <code>suit()</code> return 
 * the rank and suit (respectively) of the topmost card in the Stack, if one exists;
 * if not, then Integer.MAX_VALUE is returned by default.
 * <p>
 * Creation date: (9/30/01 9:45:50 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */

public class Stack extends Element {
	
	/** initial size of this pile in memory. */
	protected int maxPileSize = 13;

	/** The Cards in the pile. */
	protected Card[] cards;
	
	/** The number of actual cards in this pile. */
	protected int numCards = 0;
	
	/** How many cards in the stack are selected (default to none) */
	protected int numSelectedCards = 0;

	/** Each new Stack object can be given a name (or assigned a default one based on this counter). */
	private static int stackNameCounter = 1;
	
	/** If Stack is forced to grow, this is the amount by which it grows. */
	private static final int delta = 13;
/**
 * Construct an empty stack with auto-generated name.
 */
public Stack() {
	this (null);
}
/**
 * Create an empty Stack with the given name.
 * <p>
 * @param name     the name of this Stack model element.
 */
public Stack(String name) {
	super();

	// Allocate space for the initial pile
	cards = new Card[maxPileSize];

	/** use given name. */
	if (name == null) {
		name = new String ("Stack" + stackNameCounter++);
	}
	setName (new String (name));
}
/**
 * Add a card into the stack.
 * <p>
 * Generates modelChanged action if c is non-null
 * <p>
 * @param c ks.common.model.Card
 */
public void add(Card c) {
	if (c == null) {
		throw new IllegalArgumentException ("Stack::add() received null card to add.");
	}
	
	if (numCards > maxPileSize-1) {
		growStack(delta);
	}

	/** Add card to pile. */
	cards [numCards++] = c;

	clearSelections();   // clear our selections...
	hasChanged();        // we have changed state...
}
/**
 * Determines whether Cards in the Stack are all alternating in color.
 * <p>
 * Returns true if the stack is empty.
 * <p>
 * @return boolean
 */
public boolean alternatingColors() {
	if (numCards == 0) return true;
		
	return alternatingColors(0, numCards);
}

/**
 * Determines whether Cards in the Stack are all alternating in color.
 * <p>
 * Throws exception if Stack is empty.
 * <p>
 * @param start the start of the range (zero as first card in Stack)
 * @param end the end of the range (not included in the check, and no greater than count())
 * @return boolean
 * @since V2.0  as helper method that can be exposed 
 */
public boolean alternatingColors(int start, int end) {
	if ((start+1 > end) || (end > numCards) || (end < 1)|| (start > numCards-1) || (start < 0)) 
		throw new IllegalArgumentException ("Stack::alternatingColors(start,end) received invalid [start:" + start + ", end:" + end + ") values.");

	if (numCards == 0) return true;   // no good return to be had!
		
	for (int i = start+1; i < end; i++) {
		
		// Any suit out of order, return FALSE
		if (! cards[i].oppositeColor (cards[i-1]))
			return false;
	}
	
	return true;
}

/**
 * Determines whether Cards in the Stack are all ascending in rank order.
 * <p>
 * Returns true if the stack is empty.
 * <p>
 * @return boolean
 */
public boolean ascending() {
	if (numCards == 0) return true;
		
	return ascending (0, numCards);
}

/**
 * Determines whether Cards in the Stack are all ascending in rank order.
 * <p>
 * Throws exception if Stack is empty.
 * <p>
 * @return boolean
 * @param start the start of the range (zero as first card in Stack)
 * @param end the end of the range (not included in the check, and no greater than count())
 * @since V2.0  as helper method that can be exposed 
 */
public boolean ascending(int start, int end) {
	if ((start+1 > end) || (end > numCards) || (end < 1)|| (start > numCards-1) || (start < 0)) 
		throw new IllegalArgumentException ("Stack::ascending(start,end) received invalid [start:" + start + ", end:" + end + ") values.");

	int prevRank = cards[start].getRank();
	
	for (int i = start+1; i < end; i++) {
		// Any rank out of order, return FALSE
		if (cards[i].getRank() != ++prevRank) {
			return false;
		}
	}
	return true;
}

/**
 * Ensures that all cards in the stack are non-selected.
 * <p>
 */
protected void clearSelections () {
	numSelectedCards = 0;

	// reset status for all cards in the deck
	for (int i = 0; i < numCards; i++) {
		cards[i].setSelected (false);
	}
}
/**
 * Returns the number of Cards in the Stack.
 * <p>
 * @return int
 */
public int count() {
	return numCards;
}
/**
 * Determines whether Cards in the Stack are all descending in rank order.
 * <p>
 * Returns true if the stack is empty.
 * <p>
 * @return boolean
 */
public boolean descending() {
	if (numCards == 0) return true;
	
	return descending (0, numCards);
}

/**
 * Determines whether Cards in the Stack are all descending in rank order.
 * <p>
 * Throws Exception if the stack is empty.
 * <p>
 * @return boolean
 * @param start the start of the range (zero as first card in Stack)
 * @param end the end of the range (not included in the check, and no greater than count())
 * @since V2.0  as helper method that can be exposed 
 */
public boolean descending(int start, int end) {
	if ((start+1 > end) || (end > numCards) || (end < 1)|| (start > numCards-1) || (start < 0)) 
		throw new IllegalArgumentException ("Stack::descending(start,end) received invalid [start:" + start + ", end:" + end + ") values.");
	
	int prevRank = cards[start].getRank();
	
	for (int i = start+1; i < end; i++) {
		// Any rank out of order, return FALSE
		if (cards[i].getRank() != --prevRank) {
			return false;
		}
	}
	return true;
}

/**
 * Reset all card selections.
 * <p>
 * Equivalent to select (0).
 * <p>
 * Generates modelChanged action.
 * <p>
 * @return boolean
 * @since v1.6.12
 */
public boolean deselect() {
	return select(0);
}
/**
 * Determines whether the Stack is empty.
 * <p>
 * @return boolean
 */
public boolean empty() {
	return (numCards == 0);
}
/**
 * Remove top card from the pile and return it to the callee (or return null if empty).
 * <p>
 * Generates modelChanged action only if stack is non-empty.
 * <p>
 * @return ks.common.model.Card
 */
public Card get() {
	if (numCards == 0) return null;

	Card c = cards[--numCards];
	cards[numCards] = null;       // remove reference from array.

	clearSelections();            // clear our selections...	
	hasChanged();  				  // we have changed state.
	return c;
}
/**
 * Returns the number of Cards that are selected from the stack.
 * <p>
 * @return int
 */
public int getNumSelectedCards() {
	return numSelectedCards;
}
/**
 * Return Stack of the selected cards.
 * <p>
 * The cards in the Stack returned by this function are all de-selected.
 * <p>
 * Generates modelChanged action only if there were some selected cards.
 * <p>
 * @return ks.common.model.Stack
 */
public Stack getSelected() {
	Stack s = new Stack();
	if (numSelectedCards == 0) return s;

	// simply get top cards one at a time. We use this double loop to avoid creating
	// copies of cards using peek.
	int keepTrack = numSelectedCards;   // get() method will reset numSelectedCarsds
	for (int i = 1; i <= keepTrack; i++) {
		Card c = get();
		c.setSelected (false);
		s.add (c);
	}

	// one card? Then simply return as is.
	if (numSelectedCards == 1) return s;
	
	// reverse order to be appropriate.
	Stack returnStack = new Stack();
	while (s.empty() == false) {
		returnStack.add (s.get());
	}	

	numSelectedCards = 0;		 // 'unselect' cards
	hasChanged();				 // we have changed state.

	return returnStack;
}
/**
 * Increases array size by the specified amount.
 * <p>
 * updates <code>maxPileSize</code>
 * <p>
 * @param delta int
 */
protected void growStack(int delta) {
	maxPileSize += delta;
	Card oldCards[] = cards;
	cards = new Card [maxPileSize];
	for (int i = 0; i < maxPileSize - delta; i++) {
		cards[i] = oldCards[i];
	}
}
/**
 * Peek and return a copy of the top-most card (or null if empty).
 * <p>
 * Now the card being peek'd is returned with appropriate faceUp and selected status.
 * 
 * @return ks.common.model.Card
 */
public Card peek() {
	// empty Stack, so return null Card
	if (numCards == 0) return null;
	
	Card c = new Card (cards[numCards-1]);
	c.setFaceUp(cards[numCards-1].faceUp);
	c.setSelected(cards[numCards-1].selected);
	
	return c;
}
/**
 * Peek and return a copy of the card identified by idx in the stack.
 * 
 * Note that:
 * <br>
 *   <code>peek() == peek (count() - 1)</code>
 * <br>
 * @return ks.common.model.Card
 * @param idx int (a number between 0 and numCards-1)
 */
public Card peek(int idx) {
	// empty Stack, so return null Card
	if (numCards == 0) return null;

	if ((idx < 0) || (idx > numCards-1))
		throw new IllegalArgumentException ("Card::peek (int) received illegal argument:" + idx);
	
	return new Card (cards[idx]);
}
/**
 * Push Stack onto existing stack; the stack parameter object is unchanged.
 * <p>
 * Generates modelChanged action only if Stack s is non-empty.
 * <p>
 * @param s ks.common.model.Stack
 */
public void push(Stack s) {
	if (s == null) return;  // nothing to do.

	int size = s.count();
	if (size == 0) return;  // nothing to do.
	
	// must add stack from the bottom up...
	for (int i = 0; i < size; i++) {
		add (s.peek (i));
	}

	hasChanged();  // we have changed state...
}
/**
 * Returns the rank of the top card. If no card, then returns MAX_VALUE
 * <p>
 * @return int
 * @since V1.5.1 returns MAX_VALUE on error
 * @since V2.0 throws IllegalArgumentException on empty stack.
 */
public int rank() {
	if (numCards == 0) {
		throw new IllegalArgumentException ("Stack::rank() invalid on empty Stack.");
	}
	return cards[numCards-1].getRank();
}
/**
 * Remove All cards from the stack.
 * <p>
 * Generates modelChanged action only if stack had cards.
 */
public void removeAll() {
	if (numCards == 0) return;

	// remove references to cards and reset numCards.
	for (int i = 0; i < numCards; i++) {
		cards[i] = null;
	}

	numCards = 0;
	numSelectedCards = 0;
	hasChanged();  				  // we have changed state.
}
/**
 * Determines whether Cards in the Stack are all of the same color. 
 * <p>
 * Note that an empty stack returns <code>true</code> as the degenerate case.
 * @return boolean
 */
public boolean sameColor() {
	if (numCards <= 1) return true;
	return sameColor (0, numCards);
}

/**
 * Determines whether Cards in the Stack are all of the same color. 
 * <p>
 * Throws an exception on an empty stack.
 * @return boolean
 * @param start the start of the range (zero as first card in Stack)
 * @param end the end of the range (not included in the check, and no greater than count())
 * @since V2.0  as helper method that can be exposed 
 */
public boolean sameColor(int start, int end) {
   if ((start+1 > end) || (end > numCards) || (end < 1)|| (start > numCards-1) || (start < 0)) 
	   throw new IllegalArgumentException ("Stack::sameColor(start,end) received invalid [start:" + start + ", end:" + end + ") values.");

	for (int i = start+1; i < end; i++) {
		// Any color out of order, return FALSE
		if (!cards[i].sameColor (cards[i-1]))
			return false;
	}
	
	return true;
}

/**
 * Determines whether Cards in the Stack are all of the same rank. Note that
 * an empty stack returns <code>true</code> as the degenerate case.
 * <p>
 * @return boolean
 */
public boolean sameRank() {
	if (numCards <= 1) return true;
	return sameRank (0, numCards);
}

/**
 * Determines whether Cards in the Stack are all of the same rank. 
 * <p>
 * Throws an exception on an empty stack.
 * <p>
 * @return boolean
 * @param start the start of the range (zero as first card in Stack)
 * @param end the end of the range (not included in the check, and no greater than count())
 * @since V2.0  as helper method that can be exposed 
 */
public boolean sameRank(int start, int end) {
	if ((start+1 > end) || (end > numCards) || (end < 1)|| (start > numCards-1) || (start < 0)) 
		throw new IllegalArgumentException ("Stack::sameRank(start,end) received invalid [start:" + start + ", end:" + end + ") values.");

	for (int i = 1; i < numCards; i++) {
		// Any rank out of order, return FALSE
		if (!cards[i].sameRank (cards[i-1]))
			return false;
	}
	
	return true;
}

/**
 * Determines whether Cards in the Stack are all of the same suit. Note that
 * an empty stack returns <code>true</code> as the degenerate case.
 * Creation date: (9/30/01 10:16:30 PM)
 * @return boolean
 */
public boolean sameSuit() {
	if (numCards <= 1) return true;
	
	return sameSuit (0, numCards);
}

/**
 * Determines whether Cards in the Stack are all of the same suit. 
 * <p>
 * Throws an exception on an empty stack.
 * <p>
 * @return boolean
 * @param start the start of the range (zero as first card in Stack)
 * @param end the end of the range (not included in the check, and no greater than count())
 * @since V2.0  as helper method that can be exposed 
 */
public boolean sameSuit(int start, int end) {
	if ((start+1 > end) || (end > numCards) || (end < 1)|| (start > numCards-1) || (start < 0)) 
		throw new IllegalArgumentException ("Stack::sameRank(start,end) received invalid [start:" + start + ", end:" + end + ") values.");
	
	for (int i = start+1; i < end; i++) {
		// Any suit out of order, return FALSE
		if (!cards[i].sameSuit(cards[i-1]))
			return false;
	}
	
	return true;
}

/**
 * Select the top-most card in the Stack.
 * <p>
 * Generates modelChanged action if Stack is non-empty.
 * <p>
 * @return boolean
 */
public boolean select() {
	if (numCards == 0) return false;

	return select(1);
}
/**
 * Select the top n cards in the Stack, where n is a number from 1 - count().
 * <p>
 * Generates modelChanged action (because some games might wish to show
 *     selected cards in a different light than non-selected cards).
 * <p>
 * @param newNumSelectedCards int
 * @return boolean false if not enough cards in the stack to be selected.
 * @since V1.5.1 (was previously called setNumSelectedCards(int))
 */
public boolean select(int newNumSelectedCards) {
	if (newNumSelectedCards < 0) throw new IllegalArgumentException ("Stack::select() received negative argument.");

	if (newNumSelectedCards > numCards) return false;
	
	numSelectedCards = newNumSelectedCards;

	// First select top newNumSelectedCards, and then deselect remaining cards...
	int idx = numCards - 1;
	while (idx >= 0) {
		if (newNumSelectedCards-- == 0) break;
		cards[idx--].setSelected (true);
	}
	
	while (idx >= 0) {
		cards[idx--].setSelected (false);
	}

	hasChanged();  // we have changed state...
	return true;
}
/**
 * Return the suit of the topmost card in the Stack. If no card, then returns MAX_VALUE
 * <p>
 * @return int
 * @since V1.5.1 returns MAX_VALUE on error
 * @since V2.0 throws IllegalArgumentException if no cards in stack.
 */
public int suit() {
	if (numCards == 0) {
		throw new IllegalArgumentException ("Stack::suit() invalid on empty Stack.");
		//return Integer.MAX_VALUE;
	}

	return cards[numCards-1].getSuit();
}
/**
 * Return String representation of Stack
 * @return String
 * @since V1.5.1 
 */
public String toString () {
	if (numCards == 0) return "[Stack:<empty>]";

	StringBuffer sb = new StringBuffer ("[Stack:");
	for (int i = 0; i < numCards; i++) {
		sb.append (cards[i].toString());
		if (i < numCards-1) sb.append (",");
	}
	sb.append ("]");
	return sb.toString();
}
}
