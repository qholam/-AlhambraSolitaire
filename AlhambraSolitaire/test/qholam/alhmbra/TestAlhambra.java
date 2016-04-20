package qholam.alhmbra;

import java.awt.event.MouseEvent;

import ks.client.gamefactory.GameWindow;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Deck;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;
import ks.common.view.CardView;
import ks.common.view.ColumnView;
import ks.common.view.PileView;
import ks.launcher.Main;
import ks.tests.KSTestCase;
import qholam.alhambra.Alhambra;
import qholam.alhambra.model.DealOneMove;
import qholam.alhambra.model.ReassembleDeckMove;
import qholam.alhambra.model.ReserveToDownFoundationMove;
import qholam.alhambra.model.ReserveToUpFoundationMove;
import qholam.alhambra.model.ReserveToWastePileMove;
import qholam.alhambra.model.WasteToDownFoundationMove;
import qholam.alhambra.model.WasteToUpFoundationMove;

public class TestAlhambra extends KSTestCase{
	Alhambra game;
	MultiDeck deck;
	MultiDeck emptyDeck;
	Pile waste;
	Pile waste2;
	Pile waste3;
	Pile foundation;
	Pile emptyFoundation;
	Pile completeDown;
	Pile completeUp;
	Column reserve;
	GameWindow gw;
	
	/*
	 * Sets up the deck and waste pile for testing of this move
	 */
	public void setUp(){
		//create cards
		Card c1 = new Card(Card.ACE, Card.SPADES);
		Card c2 = new Card(Card.TWO, Card.SPADES);
		Card c3 = new Card(Card.QUEEN, Card.SPADES);
		Card c4 = new Card(Card.KING, Card.SPADES);
		Card c5 = new Card(Card.THREE, Card.HEARTS);
		Card c6 = new Card(Card.THREE, Card.HEARTS);
		Card c7 = new Card(Card.TWO, Card.HEARTS);
		Card c8 = new Card(Card.KING, Card.HEARTS);
		
		//from top down this deck will have: queen spades, two spades, ace spades
		deck = new MultiDeck(2);
		deck.add(c1);
		deck.add(c2);
		deck.add(c3);
		
		//create an empty deck
		emptyDeck = new MultiDeck(2);
		
		//create an empty waste pile
		waste = new Pile();
		
		//create a non empty waste pile
		waste2 = new Pile();
		waste2.add(c4);
		waste2.add(c5);
		
		//waste used to test wrap around(king wrapping to ace)
		waste3 = new Pile();
		waste3.add(c8);
		
		//create reserve
		reserve = new Column();
		reserve.add(c7);
		
		//create foundation with initial three of hearts
		foundation = new Pile();
		foundation.add(c6);
		
		//create an empty down foundation
		emptyFoundation = new Pile();
		
		//simulate a complete down foundation(ace on top)
		completeDown = new Pile();
		completeDown.add(c1);
		
		//simulate a complete up foundation(king on top)
		completeUp = new Pile();
		completeUp.add(c8);
		
		//create new instance of Alhambra game
		game = new Alhambra();
		//initialize game window
		gw = Main.generateWindow(game, Deck.OrderByRank); 
		gw.setVisible(true);
		
	}
	
	/*
	 * Test the deal one move entity class
	 */
	public void testDealOne(){ 
		//create a valid move from non empty deck to waste
		DealOneMove move1 = new DealOneMove(deck, waste);
		//create an invalid move that tries to deal a card from an empty deck
		DealOneMove move2 = new DealOneMove(emptyDeck, waste);
		
		//move should be valid
		assertTrue(move1.valid(game));
		//move should be invalid
		assertFalse(move2.valid(game));
		
		//move should not be undoable
		assertFalse(move1.undo(game));
		
		//move should be able to be done
		assertTrue(move1.doMove(game));
		//check that deck was correctly changed
		assertTrue(deck.count() == 2);
		assertEquals(deck.peek(), new Card(Card.TWO, Card.SPADES));
		//check that waste pile was correctly changed
		assertTrue(waste.count() == 1);
		assertEquals(waste.peek(), new Card(Card.QUEEN, Card.SPADES));
		
		//move should be able to be undone now
		assertTrue(move1.undo(game));
		//check that deck was correctly updated
		assertTrue(deck.count() == 3);
		assertEquals(deck.peek(), new Card(Card.QUEEN, Card.SPADES));
		//check that waste was correctly updated
		assertTrue(waste.empty());	
	}
	
	/*
	 * Test the ReassembleDeckMove
	 */
	
	public void testReassembleDeckMove(){
		//create a valid move
		ReassembleDeckMove move1 = new ReassembleDeckMove(emptyDeck, waste2);
		//create an invalid move(non empty deck)
		ReassembleDeckMove move2 = new ReassembleDeckMove(deck, waste2);
		//create an invalid move (empty waste pile)
		ReassembleDeckMove move3 = new ReassembleDeckMove(emptyDeck, waste);
		
		//move1 should be valid
		assertTrue(move1.valid(game));
		//move2 and move3 should be invalid
		assertFalse(move2.valid(game));
		assertFalse(move3.valid(game));
		
		//none of the moves are undoable
		assertFalse(move1.undo(game));
		assertFalse(move2.undo(game));
		assertFalse(move3.undo(game));
		
		//move1 should be made successfully
		assertTrue(move1.doMove(game));
		//check if emptyDeck was correctly changed
		assertEquals(emptyDeck.count(), 2);
		//check if waste2 was correctly changed
		assertTrue(waste2.empty()); 
	}
	
	/*
	 * Test ReserveToDownFoundationMove
	 */
	
	public void testReserveToDownFoundationMove(){
		//create a valid move
		ReserveToDownFoundationMove move1 = new ReserveToDownFoundationMove(reserve, foundation, new Card(Card.TWO, Card.HEARTS));
		//create an invalid move(drag card cannot be added to waste
		ReserveToDownFoundationMove move2 = new ReserveToDownFoundationMove(reserve, foundation, new Card(Card.TWO, Card.SPADES));
		//create an invalid move(foundation is empty)
		ReserveToDownFoundationMove move3 = new ReserveToDownFoundationMove(reserve, emptyFoundation, new Card(Card.TWO, Card.HEARTS));
		//create an invalid move(foundation is complete)
		ReserveToDownFoundationMove move4 = new ReserveToDownFoundationMove(reserve, completeDown, new Card(Card.TWO, Card.HEARTS));
		
		//move1 should be valid
		assertTrue(move1.valid(game));
		//move2 and move3 should not be valid
		assertFalse(move2.valid(game));
		assertFalse(move3.valid(game));
		assertFalse(move4.valid(game));
		
		//move1 should be able to be done
		assertTrue(move1.doMove(game));
		//check to see if foundation was changed correctly
		assertEquals(foundation.count(), 2);
		assertEquals(foundation.peek(), new Card(Card.TWO, Card.HEARTS));
		
		//move1 should be able to be undone
		assertTrue(move1.undo(game));
		//check that foundation was changed correctly
		assertEquals(foundation.count(), 1);
		assertEquals(foundation.peek(), new Card(Card.THREE, Card.HEARTS));
		//check that reserve ws changed correctly
		assertEquals(reserve.count(), 2);
		assertEquals(reserve.peek(), new Card(Card.TWO, Card.HEARTS));
	}
	
	/*
	 * Test ReserveToUpFoundationMove
	 */
	public void testReserveToUpFoundationMove(){
		//create a valid move
		ReserveToUpFoundationMove move1 = new ReserveToUpFoundationMove(reserve, foundation, new Card(Card.FOUR, Card.HEARTS));
		//create an invalid move(foundation is empty)
		ReserveToUpFoundationMove move2 = new ReserveToUpFoundationMove(reserve, emptyFoundation, new Card(Card.ACE, Card.SPADES));
		//create an invalid move(foundation is complete
		ReserveToUpFoundationMove move3 = new ReserveToUpFoundationMove(reserve, completeUp, new Card(Card.ACE, Card.SPADES));

		
		//move1 should be valid
		assertTrue(move1.valid(game));
		
		//move2 and move3 should be invalid
		assertFalse(move2.valid(game));
		assertFalse(move3.valid(game));
		
		//move1 should be able to be done
		assertTrue(move1.doMove(game));
		//check that foundation was correctly changed
		assertEquals(foundation.count(), 2);
		assertEquals(foundation.peek(), new Card(Card.FOUR, Card.HEARTS));
		
		//move1 should be able to be undone
		assertTrue(move1.undo(game));
		//check that foundation was correctly changed
		assertEquals(foundation.count(), 1);
		assertEquals(foundation.peek(), new Card(Card.THREE, Card.HEARTS));
		//check to see that the reserve was correctly changed
		assertEquals(reserve.count(), 2);
		assertEquals(reserve.peek(), new Card(Card.FOUR, Card.HEARTS));
	}
	
	/*
	 * Test ReserveToWastePileMove
	 */
	public void testReserveToWastePileMove(){
		//create a valid move
		ReserveToWastePileMove move1 = new ReserveToWastePileMove(reserve, waste2, new Card(Card.FOUR, Card.HEARTS));
		//create an invalid move(empty waste)
		ReserveToWastePileMove move2 = new ReserveToWastePileMove(reserve, waste, new Card(Card.FIVE, Card.HEARTS));
		//create an invalid move(card being dragged doesnt follow logic to be added)
		ReserveToWastePileMove move3 = new ReserveToWastePileMove(reserve, waste2, new Card(Card.FIVE, Card.HEARTS));
		//test wrap around(king goes to ace)
		ReserveToWastePileMove move4 = new ReserveToWastePileMove(reserve, waste3, new Card(Card.ACE, Card.HEARTS));
		
		//move1 is valid
		assertTrue(move1.valid(game));
		//move2 and move3 should be invalid 
		assertFalse(move2.valid(game));
		assertFalse(move3.valid(game));
		
		//move1 can be done
		assertTrue(move1.doMove(game));
		//check that waste pile was correctly changed
		assertEquals(waste2.count(), 3);
		assertEquals(waste2.peek(), new Card(Card.FOUR, Card.HEARTS));
		
		//move4 can be done
		assertTrue(move4.doMove(game));
		//check that waste pile was correctly changed
		assertEquals(waste3.count(), 2);
		assertEquals(waste3.peek(), new Card(Card.ACE, Card.HEARTS));
		
		//move1 should be able to be undone
		assertTrue(move1.undo(game));
		//check that waste pile was correctly changed
		assertEquals(waste2.count(), 2);
		assertEquals(waste2.peek(), new Card(Card.THREE, Card.HEARTS));
		//check that reserve pile was correctly changed
		assertEquals(reserve.count(), 2);
		assertEquals(reserve.peek(), new Card(Card.FOUR, Card.HEARTS));
	}
	
	/*
	 *  Test WasteToDownFoundtionMove
	 */
	public void testWasteToDownFoundationMove(){
		//create a valid move
		WasteToDownFoundationMove move1 = new WasteToDownFoundationMove(waste, foundation, new Card(Card.TWO, Card.HEARTS));
		//create an invalid move(drag card cannot be added to waste
		WasteToDownFoundationMove move2 = new WasteToDownFoundationMove(waste, foundation, new Card(Card.TWO, Card.SPADES));
		//create an invalid move(foundation is empty)
		WasteToDownFoundationMove move3 = new WasteToDownFoundationMove(waste, emptyFoundation, new Card(Card.TWO, Card.HEARTS));
		//create an invalid move(foundation is complete)
		WasteToDownFoundationMove move4 = new WasteToDownFoundationMove(waste, completeDown, new Card(Card.TWO, Card.HEARTS));
				
		//move1 should be valid
		assertTrue(move1.valid(game));
		//move2 and move3 should not be valid
		assertFalse(move2.valid(game));
		assertFalse(move3.valid(game));
		assertFalse(move4.valid(game));
				
		//move1 should be able to be done
		assertTrue(move1.doMove(game));
		//check to see if foundation was changed correctly
		assertEquals(foundation.count(), 2);
		assertEquals(foundation.peek(), new Card(Card.TWO, Card.HEARTS));
				
		//move1 should be able to be undone
		assertTrue(move1.undo(game));
		//check that foundation was changed correctly
		assertEquals(foundation.count(), 1);
		assertEquals(foundation.peek(), new Card(Card.THREE, Card.HEARTS));
		//check that reserve was changed correctly
		assertEquals(waste.count(), 1);
		assertEquals(waste.peek(), new Card(Card.TWO, Card.HEARTS)); 
	} 
	
	/*
	 * Test WasteToUpFoundationMove
	 */
	public void testWasteToUpFoundationMove(){
		//create a valid move
		WasteToUpFoundationMove move1 = new WasteToUpFoundationMove(waste, foundation, new Card(Card.FOUR, Card.HEARTS));
		//create an invalid move(foundation is empty)
		WasteToUpFoundationMove move2 = new WasteToUpFoundationMove(waste, emptyFoundation, new Card(Card.ACE, Card.SPADES));
		//create an invalid move(foundation is complete
		WasteToUpFoundationMove move3 = new WasteToUpFoundationMove(waste, completeUp, new Card(Card.ACE, Card.SPADES));

		
		//move1 should be valid
		assertTrue(move1.valid(game));
		
		//move2 and move3 should be invalid
		assertFalse(move2.valid(game));
		assertFalse(move3.valid(game));
		
		//move1 should be able to be done
		assertTrue(move1.doMove(game));
		//check that foundation was correctly changed
		assertEquals(foundation.count(), 2);
		assertEquals(foundation.peek(), new Card(Card.FOUR, Card.HEARTS));
		
		//move1 should be able to be undone
		assertTrue(move1.undo(game));
		//check that foundation was correctly changed
		assertEquals(foundation.count(), 1);
		assertEquals(foundation.peek(), new Card(Card.THREE, Card.HEARTS));
		//check to see that the reserve was correctly changed
		assertEquals(waste.count(), 1);
		assertEquals(waste.peek(), new Card(Card.FOUR, Card.HEARTS));
	}
	
	/**
	 * Test the controller for the ace up foundation
	 */
	public void testAceUpFoundationController(){
		//add card to container
		game.getContainer().setActiveDraggingObject(new CardView(new Card(Card.TWO, Card.SPADES)), new MouseEvent(gw, 0, 0, 0, 0, 0, 0, false));
		game.getContainer().setDragSource(new PileView(new Pile()));
		
		//create the mouse event
		MouseEvent released = createReleased(game, game.getUpView1(), 0, 0);
		game.getUpView1().getMouseManager().handleMouseEvent(released);
		
		//up foundation correctly update
		assertEquals(game.getUp1().peek(), new Card(Card.TWO, Card.SPADES));
	}
	
	/**
	 * Test the controller for the deck
	 */
	public void testDeckController(){
		//create mouse event
		MouseEvent press = createPressed(game, game.getDeckView(), 0, 0);
		game.getDeckView().getMouseManager().handleMouseEvent(press);
		
		//check that deck is correctly changed
		assertEquals(game.getDeck().count(), 62);
		
		//check that waste pile is correctly changed
		assertEquals(game.getWaste().peek(), new Card(Card.FOUR, Card.HEARTS));
	}
	
	/**
	 * Test the controller for the king down foundation
	 */
	public void testKingDownFoundationController(){
		//add card to container
		game.getContainer().setActiveDraggingObject(new CardView(new Card(Card.QUEEN, Card.SPADES)), new MouseEvent(gw, 0, 0, 0, 0, 0, 0, false));
		game.getContainer().setDragSource(new PileView(new Pile()));
				
		//create the mouse event
		MouseEvent released = createReleased(game, game.getDownView1(), 0, 0);
		game.getDownView1().getMouseManager().handleMouseEvent(released);
			
		//up foundation correctly update
		assertEquals(game.getDown1().peek(), new Card(Card.QUEEN, Card.SPADES));
	}
	
	/**
	 * Test the controller for the reserve column
	 */
	public void testReserveColumnController(){

		
	}
	
	/**
	 * Test the controller for the waste pile
	 */
	public void testWastePileController(){
		//add card to container
		game.getContainer().setActiveDraggingObject(new CardView(new Card(Card.JACK, Card.SPADES)), new MouseEvent(gw, 0, 0, 0, 0, 0, 0, false));
		game.getContainer().setDragSource(new ColumnView(new Column()));
						
		//create the mouse event
		MouseEvent released = createReleased(game, game.getWasteView(), 0, 0);
		game.getWasteView().getMouseManager().handleMouseEvent(released);
					
		//up foundation correctly update
		assertEquals(game.getWaste().peek(), new Card(Card.JACK, Card.SPADES));
	}
}
