package ks.common.view;

import java.awt.*;
import java.util.Hashtable;

import ks.common.model.Card;
import ks.common.model.CardEnumeration;
import ks.common.view.CardImages;

/**
 * Load up all the images for a specific deck of cards (or the default if none selected).
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class CardImagesLoader implements Runnable {
	/** Debug. */
	static boolean DEBUG = false;

	/** Where images will be stored */
	protected static final String imageDirectory = "images/";

	/** Loaded object containing images. This is set by thread.*/
	protected CardImages loadedImages = null;

	/** Decks that have been loaded so far. */
	protected static Hashtable<String,CardImages> availableDecks = new Hashtable<String,CardImages>();

	/** Default CardImages location. */
	protected static final String defaultName = "default";

	/** Has thread loaded all cards yet?. */
	protected boolean readyStatus = false;

	/** Has thread failed to load any card. */
	protected boolean failedStatus = false;
	
	/** Default name for image of reverse of card. */
	protected static final String backResourceName = "Back";

	/** Deck type. Used within thread. */
	protected String deckType = defaultName;

	/** Peer for image observation. */
	protected Component peer = null;

	/** Thread in which the loadAllCards will run. */
	protected java.lang.Thread thread = null;

	/** Interface for writing information on progress of load. */
	protected ICardImagesStatus output = null;

	/**
	 * Load the default Deck.
	 */
	public CardImagesLoader(Component p) {
		this (p, defaultName);
	}
	
	/**
	 * Load a specific deck.
	 * <p>
	 * If deckName is null, the default deck is selected.
	 */
	public CardImagesLoader(Component peer, String deckName) {
		super();

		deckType = deckName;
		if (deckType == null) {
			deckType = defaultName;
		}
		this.peer = peer;
	}
	
	/**
	 * Return CardImages object for this deck (whether in cache or freshly loaded).
	 * <p>
	 * If the deck is to be loaded, then peer is used as an <code>ImageObserver</code>.
	 * @param deckName String
	 * @param peer java.awt.Component 
	 * @return CardImages
	 */
	public static CardImages getDeck (Component peer, String deckName) {
		return getDeck (peer, deckName, null);
	}

	/**
	 * Return CardImages object for this deck (whether in cache or freshly loaded) using given ICardImageStatus observer.
	 * <p>
	 * If the deck is to be loaded, then peer is used as an <code>ImageObserver</code>.
	 * @param deckName String
	 * @param peer java.awt.Component 
	 * @return CardImages
	 */
	public static CardImages getDeck (Component peer, String deckName, ICardImagesStatus output) {
		if (DEBUG) System.out.println ("within getDeck.");
		if (deckName == null) {
			// make sure we signal that we have loaded!
			if (output != null) output.end();
			return null;
		}

		// see if we have this loaded. If so, return it now.
		CardImages cards = (CardImages) availableDecks.get (deckName);
		if (cards != null) {
			// make sure we signal that we have loaded!
			if (output != null) output.end();
			return cards;
		}

		// try to load images
		if (DEBUG) System.out.println ("Trying to load cards.");

		CardImagesLoader cil = new CardImagesLoader(peer, deckName);
		cil.setStatusAgent (output);
		cil.start();

		// wait until cards are all fetched
		if (DEBUG) System.out.println ("Waiting for cards to be fetched.");
		while (! cil.ready()) {
			if (cil.failed()) {
				break;
			}
			try {
				Thread.sleep (250);  // wait until ready or interrupted.
			} catch (InterruptedException ie) {
				System.err.println ("CardImagesLoader::getDeck(). Unable to completely load deck: " + deckName);
				break;
			}
		}

		// by this point, cil will complete and call 'output.end()'.

		cards = cil.getLoadedCardImages();
		if (cards == null) {
			return null;
		}
		cards.setName (deckName);

		// insert into hashtable for future reference...
		synchronized (availableDecks) {
			availableDecks.put (deckName, cards);
		}
		return cards;
	}
	
	/**
	 * Return images loaded by thread.
	 * <p>
	 * This method is only accessible from within the view package.
	 * Creation date: (10/1/01 8:48:31 PM)
	 * @return java.awt.Image
	 */
	synchronized CardImages getLoadedCardImages () {
		return loadedImages;
	}
	
	/**
	 * Determine if cards have been loaded.
	 */
	public boolean ready () {
		return readyStatus; 
	}
	
	/**
	 * Determine if any card failed to load.
	 */
	public boolean failed() {
		return failedStatus;
	}
	
	/**
	 * Retrieve all images. This thread will self-terminate once all are loaded.
	 * Creation date: (10/2/01 5:17:01 PM)
	 */
	public void run() {
		if (DEBUG) System.out.println ("CardImagesLoader running.");
		// The media Tracker ensures all images are fully loaded: This avoids the
		// arbitrary race conditions that may happen when running a plugin before all
		// images have been properly loaded,
		java.awt.MediaTracker mt = new java.awt.MediaTracker (peer);

		// Create a CardImages to house the deck of card images.
		CardImages ci = new CardImages(this, mt);

		CardEnumeration ce = new CardEnumeration ();
		int idx = 1;
		while (ce.hasMoreElements()) { 
			Card c = (Card) ce.nextElement();
			String key = c.getName();

			// keep track on the screen...
			if (output != null) 
				output.load (c.getName());

			// extract from resource file (prepend images directory)
			java.net.URL url = this.getClass().getResource ("/" + imageDirectory + deckType + "/" + key + ".gif");
			if (url == null) {
				failedStatus = true;
			}
			Image img = java.awt.Toolkit.getDefaultToolkit().getImage(url);
			mt.addImage (img, idx++);
			ci.setCardImage (c, img);
		}

		// Also get Back (already in the images directory)
		java.net.URL u = this.getClass().getResource ("/" + imageDirectory + deckType + "/" + backResourceName + ".gif");
		if (u == null) {
			failedStatus = true;
		}
		Image img = java.awt.Toolkit.getDefaultToolkit().getImage(u);
		mt.addImage (img, idx++);
		ci.setCardReverse (img);

		try {
			mt.waitForAll();
		} catch (InterruptedException ie) {
		}

		// Modify in static class [Note: in future release, allow containers to know cardImages...]
		//availableDecks.put (deckType, ci);

		// keep around so thread can return this value in synchronized method getLoadedCardImages
		loadedImages = ci;

		if (output != null) {
			output.end();
		}

		// we are done.
		readyStatus = true;
		if (DEBUG) System.out.println ("CardImagesLoader done.");
	}
	
	/**
	 * Set Agent so we can announce the images we are loading...
	 * <p>
	 * Creation date: (10/1/01 8:48:31 PM)
	 */
	public void setStatusAgent (ICardImagesStatus output) {
		this.output = output;
	}
	
	/**
	 * Launch the thread to execute run.
	 * Creation date: (10/2/01 5:17:12 PM)
	 */
	public void start() {
		thread = new Thread(this); 
		thread.start(); 
	}

	public void remove(CardImages cardImages) {
		// remove from hashtable so it won't be detected 
		synchronized (availableDecks) {
			String deckName = cardImages.getName();
		
			availableDecks.remove (deckName);
		}
	}
}
