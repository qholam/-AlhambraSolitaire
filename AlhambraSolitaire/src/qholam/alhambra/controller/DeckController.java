package qholam.alhambra.controller;

import java.awt.event.MouseEvent;

import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.games.Solitaire;
import ks.common.model.Move;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;
import qholam.alhambra.model.DealOneMove;
import qholam.alhambra.model.ReassembleDeckMove;
import qholam.alhambra.Alhambra;

public class DeckController extends SolitaireReleasedAdapter{
	public DeckController(Solitaire game) {
		super(game);
	}

	/**
	 * Handles mousePressed event for deck
	 * @param e MouseEvent
	 */
	public void mousePressed(MouseEvent e){
		//Get the deck and waste pile 
		MultiDeck d = (MultiDeck) this.theGame.getModelElement("deck");
		Pile p = (Pile) this.theGame.getModelElement("waste");
		
		//Move to be determine
		Move move = null;
		
		//Get the move that is to be made
		//Check if deck is empty
		if(d.empty()){//reassemble deck
			move = new ReassembleDeckMove(d, p);
		}
		else//deck is not empty
			move = new DealOneMove(d, p);
		
		
		//Check if move was successfully made
		if(move.doMove(theGame))
			//move success push to game
			this.theGame.pushMove(move);
		
		//refresh all affected widgets
		this.theGame.refreshWidgets();
	}
}
