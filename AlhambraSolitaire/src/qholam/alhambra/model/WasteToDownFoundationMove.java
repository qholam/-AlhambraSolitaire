package qholam.alhambra.model;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Move;
import ks.common.model.Pile;

public class WasteToDownFoundationMove extends Move{
	protected Pile waste, downFoundation;
	protected Card cardBeingDragged;
	
	public WasteToDownFoundationMove(Pile from, Pile to, Card c){
		super();
		
		waste = from;
		downFoundation = to;
		cardBeingDragged = c;
	}

	@Override
	/**
	* If move is valid, moves cardBeingDragged into the foundation pile
	* @param game The game being played.
	* @return boolean Returns true if move was successful
	*/
	public boolean doMove(Solitaire game) {
		//checks if move is valid to be made
		if(!valid(game))
			return false;
				
		//add the card being dragged to the foundation pile
		downFoundation.add(cardBeingDragged);
				
		//update the score
		game.updateScore(1);
		
		//move was successful
		return true;
	}

	@Override
	/**
	* Add the card from the foundation pile back into the waste pile
	* @param game The game being played.
	* @return boolean Returns true if undo was successful
	*/
	public boolean undo(Solitaire game) {
		//Ensures the foundation column is not empty
		if(downFoundation.empty())
			return false;
		
		//Get top card of the foundation pile
		Card c = downFoundation.get();
		//Add it back into the waste pile
		waste.add(c);
		
		//update score
		game.updateScore(-1);
		
		//move was successful
		return true;
	}

	@Override
	/**
	* This move is valid to make if: 
	* (1) Foundation pile is not empty
	* (2) The top card of the foundation pile is not an ace
	* (3) Top card of foundation pile is of the same suite and one point higher
	* than the card being dragged
	* @param game The game being played.
	* @return boolean Returns true if move is valid
	*/
	public boolean valid(Solitaire game) {
		boolean validMove = true;
		Card topCard = downFoundation.peek();
		
		//checks if foundation is empty
		if(downFoundation.empty())
			return false;
		
		//checks if the foundation pile is an ace
		if(topCard.isAce())
			validMove = false;
		
		//makes sure the top card of the foundation pile is of the same suite and one pointer
		//higher than the card being dragged
		if(!topCard.sameSuit(cardBeingDragged) || topCard.compareTo(cardBeingDragged) != 1)
			validMove = false;
		
		return validMove;
	}

}
