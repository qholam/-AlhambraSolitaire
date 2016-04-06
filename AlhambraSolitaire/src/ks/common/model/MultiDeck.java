package ks.common.model;

/**
 * Models an object consisting of multiple decks.
 * <p>
 * Creation date: (12/28/01 11:13:50 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class MultiDeck extends Deck {
	/** Used to uniquely identify named MultiDecks (if no name is assigned). */
	private static int multiDeckNameCounter = 1;

	/** Number of decks being used. */
	protected int numDecks;
	
	/**
	 * Construct an empty MultiDeck object. The name of the MultiDeck is automatically generated.
	 */
	public MultiDeck(int numDecks) {
		this (null, numDecks);
	}
	
	/**
	 * Create an empty MultiDeck object with the given name and number of decks.
	 * <p>
	 * @param name java.lang.String
	 * @param numDecks int
	 */
	public MultiDeck(String name, int numDecks) {
		super(name);

		if (numDecks < 1) throw new IllegalArgumentException ("MultiDeck::MultiDeck() received invalid number of decks in constructor.");

		this.numDecks = numDecks;

		// set name accordingly.
		if (name == null) {
			name = new String ("MultiDeck" + multiDeckNameCounter++);
		}

		setName (name);	
	}
	
	/**
	 * Set multiDeck to be in order from ACE -> King, Clubs -> Spades, one for each deck.
	 * <p>
	 * Creation date: (11/5/01 9:06:23 PM)
	 */
	@Override
	protected void insertFullDeckBySuit() {
		for (int i = 0; i < numDecks; i++) {	
			super.insertFullDeckBySuit();
		}
	}
	
	/**
	 * Set multiDeck to be in order from Aces, then Deuces...
	 * <p>
	 * Creation date: (11/5/01 9:06:23 PM)
	 */
	@Override
	protected void insertFullDeckByRank() {
		for (int i = 0; i < numDecks; i++) {	
			super.insertFullDeckByRank();
		}
	}
}
