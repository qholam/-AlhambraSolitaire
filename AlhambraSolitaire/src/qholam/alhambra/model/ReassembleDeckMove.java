package qholam.alhambra.model;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Move;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;

public class ReassembleDeckMove extends Move{
	protected MultiDeck deck;
	protected Pile waste;
	
	public ReassembleDeckMove(MultiDeck d, Pile waste){
		super();
		
		this.deck = d;
		this.waste = waste;
	}

	@Override
	/**
	* Removes the top card of the waste pile one by one and places them
	* onto the deck
	* @param game The game being played.
	* @return boolean Returns true if the move successfully executes
	*/
	public boolean doMove(Solitaire game) {
		//keeps track of number of cards added to deck
		int numCards = 0;
		
		//Ensures move is valid to be made
		if(!valid(game))
			return false;
		
		//Remove top card of waste pile one by one and place them into the deck
		while(!waste.empty()){
			//get top card of waste pile
			Card c = waste.get();
			//add it to the deck
			deck.add(c);
			
			//one card has been added, update tracker
			numCards++;
		}
		
		//update the counter for the number of cards in deck
		game.updateNumberCardsLeft(numCards);
		
		//move was successful
		return true;
	}

	@Override
	/**
	* This move cannot be undone 
	* @param game The game being played.
	* @return boolean Returns false since this move cannot be undone
	*/
	public boolean undo(Solitaire game) {
		return false;
	}

	@Override
	/**
	* Check if deck is empty, making the move valid to be made
	* @param game The game being played
	* @return boolean Returns true if the deck is empty 
	*/
	public boolean valid(Solitaire game) {
		boolean deckEmpty = false;
		
		if(deck.empty())
			deckEmpty = true;
		
		return deckEmpty;
	}

}
