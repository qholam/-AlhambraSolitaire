package qholam.alhambra.model;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Move;
import ks.common.model.Pile;

public class ReserveToWastePileMove extends Move{
	protected Column reserve;
	protected Pile waste;
	protected Card cardBeingDragged;

	public ReserveToWastePileMove(Column col, Pile p, Card c){
		super();
		
		reserve = col;
		waste = p;
		cardBeingDragged = c;
	}
	
	@Override
	/**
	 * Places the card being dragged into the Waste pile if move
	 * is valid
	 * @param game The game being played.
	 * @return boolean return true if mode was successful
	 */
	public boolean doMove(Solitaire game) {
		//check validity of move
		if(!valid(game))
			return false;
		
		//add the card being dragged to Waste pile
		waste.add(cardBeingDragged);
		
		//move was successful
		return true;
	}

	@Override
	/**
	 * Undo move by taking top card of waste pile and placing it back
	 * to the Reserve from which it came
	 * @param game The game being played.
	 * @return boolean returns true if undo was successful
	 */
	public boolean undo(Solitaire game) {
		//check if Waste pile is empty
		if(waste.empty())
			return false;
		
		//get the top card of the Waste pile
		Card top = waste.get();
		//add it back to the Reserve
		reserve.add(top);
		
		//undo successful
		return true;
	}

	@Override
	/**
	 * Method to check validity of move
	 * This move is valid if:
	 * (1) Waste pile is not empty
	 * (2) Top card of Waste pile and card being dragged are 
	 * both of the same suite and one point apart(King wraps to Ace)
	 * @param game The game being played.
	 * @return boolean returns true if move is valid
	 */
	public boolean valid(Solitaire game) {
		boolean valid = true;
		
		//checks if the waste pile is empty
		if(waste.empty())
			return false;
		
		/*Ensure the top card of waste pile and card being dragged
		are same suite and one point apart*/
		Card top = waste.peek();
		//check if they are different suites
		if(!top.sameSuit(cardBeingDragged)){
			//false if cards are not same suite
			valid = false;
		}
		//ensures cards are one point apart(King wraps to Ace)
		if(Math.abs(top.compareTo(cardBeingDragged)) != 1){
			//compareTo method does not account for the wrapping of king to ace, need to check
			if(top.getRank() == Card.KING && cardBeingDragged.isAce()){}//do nothing
			else{//Cards are not one point apart
				valid = false;
			}
			  
		}
		
		return valid;
			
	}

}
