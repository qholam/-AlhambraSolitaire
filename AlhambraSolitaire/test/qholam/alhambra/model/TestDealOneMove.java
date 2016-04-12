package qholam.alhambra.model;

import junit.framework.TestCase;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;

public class TestDealOneMove extends TestCase{
	MultiDeck deck;
	Pile waste;
	
	/*
	 * Sets up the deck and waste pile for testing of this move
	 */
	public void setUp(){
		//create an empty deck
		deck = new MultiDeck(2);
		
	}
	
	public void testDealOne(){
		DealOneMove move = new DealOneMove(deck, waste);
	}
}
