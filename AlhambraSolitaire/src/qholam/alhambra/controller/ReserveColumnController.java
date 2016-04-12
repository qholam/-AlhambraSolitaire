package qholam.alhambra.controller;

import java.awt.event.MouseEvent;

import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.games.Solitaire;
import ks.common.view.CardView;
import ks.common.view.ColumnView;
import ks.common.view.Container;

public class ReserveColumnController extends SolitaireReleasedAdapter{
	protected Solitaire game;
	protected ColumnView reserve;

	public ReserveColumnController(Solitaire theGame, ColumnView c) {
		super(theGame);
		
		game = theGame;
		reserve = c;
	}
	
	/**
	 * Handles mousePressed event for reserve column
	 * @param e MouseEvent
	 */
	public void mousePressed(MouseEvent e){
		//Get CardView for the top card of the reserve
		CardView cardView = reserve.getCardViewForTopCard(e);
		//check if card is null
		if(cardView == null)
			return;
		
		//Pass the CardView to the Container and let it handle it
		Container c = game.getContainer();
		c.setActiveDraggingObject(cardView, e);
		//let the Container know of the source
		c.setDragSource(reserve);
		
		//repaint the reserve
		reserve.redraw();
	}
}
