package ks.common.model;

import java.util.Random;

/**
 * Models a deck of cards as a <code>Stack</code>.
 * <p>
 * Note: Cards are only removed from the top of the deck, but during the initialization
 * routine, this subclass makes detailed use of the actual structure of cards within a
 * stack.
 * <p>
 * When a Deck is created, it is initially empty. The proper way to create a deck is
 * as follows:<p>
 * <pre>
 * Deck d = new Deck ("personalName");<br>
 * d.create (778726);<br>
 * </pre>
 * The <code>create</code> method will randomly shuffle the deck using a deterministic
 * algorithm using 778726 as the seed for pseudo-random shuffling.
 * <p>
 * Creation date: (9/30/01 10:42:44 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class Deck extends Stack {

	/** Used to uniquely identify named Decks (if no name is assigned). */
	private static int deckNameCounter = 1;

	/** Special order Aces then Deuces, etc.... */
	public static final int OrderByRank = -1;
	
	/** Special order. Clubs, then Diamonds, etc... */
	public static final int OrderBySuit = -2;
	
	/**
	 * Construct an empty deck. The name of the deck is automatically generated.
	 */
	public Deck() {
		this (null);
	}
	
	/**
	 * Construct an empty deck with the given name.
	 * <p>
	 * If the name parameter passed in is null, then a generated name will be used.
	 * @param name String 
	 */
	public Deck(String name) {
		super();

		// set name accordingly.
		if (name == null) {
			name = new String ("Deck" + deckNameCounter++);
		}

		setName (name);
	}
	
	/**
	 * Create a deck using the prespecified shuffling algorithm based
	 * upon this seed value.
	 * <p>
	 * This method first removes all cards and then deals with
	 * the initializations.
	 * 
	 * This method invokes the <code>initialize</code> method to properly construct the
	 * full deck before the shuffling algorithm commences. Note that this method makes 
	 * detailed use of the internal structure of a Stack.
	 * <p>
	 * Added "-1" as a special sorted deck by suit. Added "-2" as a special
	 * sorted deck by rank.
	 * 
	 * Creation date: (9/30/01 10:44:19 PM)
	 * @param seed int
	 */
	public void create(int seed) {
		removeAll();

		// set deck up to initial value.
		if (seed == OrderByRank) {
			insertFullDeckByRank(); 
			return;
		} 

		insertFullDeckBySuit();
		
		// leave in suit order.
		if (seed == OrderBySuit) {
			return;
		}
		
		// shuffle away!
		shuffle (seed);
	}

	/**
	 * Shuffle the deck using the given seed value as random seed.
	 * @param seed
	 */
	public void shuffle (int seed) {
		Random rnd = new Random(seed);
		
		// Generate 2048 random bytes (0..255)
		byte b[] = new byte[2048];
		rnd.nextBytes(b);

		// Treat each pair of values b[i] and b[i+1] as two cards that should be swapped.
		int size = count();
		for (int i = 0; i < 2048; i = i+2) {
			int idx1 = Math.abs(b[i] % size);
			int idx2 = Math.abs(b[i+1] % size);

			// Swap
			Card t = cards[idx1];
			cards[idx1] = cards[idx2];
			cards[idx2] = t;
		}

		// Done. This should produce 1024 random shufflings of the deck. Reasonable enough I suppose
	}

	/**
	 * Insert 52 cards into the deck, ACE -> KING, CLUBS through SPADES
	 * <p>
	 * Creation date: (11/5/01 9:06:23 PM)
	 * @since version 1.8
	 */
	protected void insertFullDeckBySuit() {
		// FORALL suits
		for (int s = Card.CLUBS; s <= Card.SPADES; s++) {
			// FORALL ranks
			for (int r = Card.ACE; r <= Card.KING; r++) {
				add (new Card (r, s));
			}
		}
	}

	/**
	 * Insert 52 cards into the deck, ACE -> KING, CLUBS through SPADES
	 * <p>
	 * Creation date: (11/5/01 9:06:23 PM)
	 * @since version 1.8
	 */
	protected void insertFullDeckByRank() {
		// FORALL ranks
		for (int r = Card.ACE; r <= Card.KING; r++) {
			// FORALL suits
			for (int s = Card.CLUBS; s <= Card.SPADES; s++) {
				add (new Card (r, s));
			}
		}
	}
}
