package qholam.alhambra.model;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Move;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;

public class DealOneMove extends Move{
	/*Deck and waste pile of the game*/
	protected MultiDeck deck;
	protected Pile wastePile;
	
	public DealOneMove(MultiDeck d, Pile p){
		super();
		
		deck = d;
		wastePile = p;
	}
	
	@Override
	/**
	* Removes top card of the deck and places it onto the top of the waste pile. 
	* Reduces the counter for the number of cards in deck by one. 
	* @param game The game being played
	* @return boolean Returns true if the move successfully executes
	*/
	public boolean doMove(Solitaire game) {
		//Ensures that move is valid to make
		if(!valid(game))
			return false;
		
		//EXECUTE MOVE
		//get top card of deck
		Card cardDealt = deck.get();
		//add top card of deck to the waste pile
		wastePile.add(cardDealt);
		
		//Update the counter for the number of cards in the deck
		game.updateNumberCardsLeft(-1);
		
		//Move successfully made
		return true;
	}

	@Override
	/**
	* Remove the dealt card from the waste pile and add it back to the deck
	* @param game The game being played
	* @return boolean Returns true if the undo was successfully made
	*/
	public boolean undo(Solitaire game) {
		//Ensures the waste pile is not empty
		if(wastePile.empty())
			return false;
		
		//Get the card that was dealt
		Card cardDealt = wastePile. get();
		//Add it back to the deck
		deck.add(cardDealt);
		
		//Update number of cards in deck
		game.updateNumberCardsLeft(+1);
		
		//undo was successful
		return true; 
	}

	@Override
	/**
	* Checks if the deck is not empty, making the move valid. 
	* @param game The game being played
	* @return boolean Returns true if the move is valid to make
	*/
	public boolean valid(Solitaire game) {
		boolean notEmpty = false; 
		
		if(!deck.empty())
			notEmpty = true;
		
		return notEmpty;
	}

}
