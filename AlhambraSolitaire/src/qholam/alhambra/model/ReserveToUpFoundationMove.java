package qholam.alhambra.model;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.Pile;

public class ReserveToUpFoundationMove extends Move{
	protected Column reserve;
	protected Pile upFoundation;
	protected Card cardBeingDragged;
	
	public ReserveToUpFoundationMove(Column from, Pile to, Card c){
		super();
		
		reserve = from;
		upFoundation = to;
		cardBeingDragged = c;
	}

	@Override
	/**
	* If move is valid, moves cardBeingDragged into the foundation pile
	* @param game The game being played.
	* @return boolean Returns true if move was successful
	*/
	public boolean doMove(Solitaire game) {
		//Make sure move is valid
		if(!valid(game))
			return false;
		
		//add card being dragged into the foundation pile
		upFoundation.add(cardBeingDragged);
		
		//update the score
		game.updateScore(1);
		
		//move was successful
		return true;
	}

	@Override
	/**
	* Adds top card of foundation pile back into the reserve
	* @param game The game being played.
	* @return boolean Returns true if undo was successful 
	*/
	public boolean undo(Solitaire game) {
		//Ensure that the foundation pile is not empty
		if(upFoundation.empty())
			return false;
		
		//Get top card of foundation
		Card c = upFoundation.get();
		//Add it into the reserve
		reserve.add(c);
		
		//update score
		game.updateScore(-1);
		
		//move was successful
		return true;
	}

	@Override
	/**
	* This move is valid to make if: 
	* (1) Foundation pile is not empty
	* (2) The top card of the foundation pile is not a king
	* (3) Top card of foundation pile is of the same suite and one point lower
	* than the card being dragged
	* @param game The game being played.
	* @return boolean Returns false since this move cannot be undone
	*/
	public boolean valid(Solitaire game) {
		boolean validMove = true;
		Card topCard = upFoundation.peek();
		
		//checks if foundation is empty
		if(upFoundation.empty())
			validMove = false;
		
		//checks if the foundation pile is a king
		if(topCard.isKing())
			validMove = false;
		
		//makes sure the top card of the foundation pile is of the same suite and one pointer
		//lower than the card being dragged
		if(!topCard.sameSuit(cardBeingDragged) || topCard.compareTo(cardBeingDragged) != -1)
			validMove = false;
		
		return validMove;
	}

}
