package qholam.alhambra.controller;

import java.awt.event.MouseEvent;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Element;
import ks.common.model.Move;
import ks.common.model.Pile;
import ks.common.view.CardView;
import ks.common.view.Container;
import ks.common.view.PileView;
import ks.common.view.Widget;
import qholam.alhambra.model.ReserveToUpFoundationMove;
import qholam.alhambra.model.WasteToUpFoundationMove;

public class AceUpFoundationController extends java.awt.event.MouseAdapter{
	protected Solitaire game;
	protected PileView aceUpFoundation;
	
	public AceUpFoundationController(Solitaire g, PileView p){
		super();
		
		game = g;
		aceUpFoundation = p;
		
	}
	
	/**
	 * Handles mouseReleased event for ace up foundation piles
	 * @param e MouseEvent
	 */
	public void mouseReleased(MouseEvent e){
		//Get the container for this game
		Container c = this.game.getContainer();
				
		//get the CardView of the card being dragged
		Widget obj = c.getActiveDraggingObject();
		//checks if anything is actually being dragged
		if(obj == c.getNothingBeingDragged())
			return;
		CardView draggedObject = (CardView) obj;
		//Get the Card object reference by the CardView
		Card draggedCard = (Card) draggedObject.getModelElement();
				
		//Get the model element of the widget of the source that initiated
		//the drag, found from the Container
		Widget sourceView = c.getDragSource();
		Element source = sourceView.getModelElement();
		
		//Get the model element from the ace up foundation pile
		Pile target = (Pile) aceUpFoundation.getModelElement();
		
		//Determine what move to make based on the source of the drag card
		//checks if source is a Column
		Move move = null;
		if(source instanceof Column){
			Column col = (Column) source;
			move = new ReserveToUpFoundationMove(col, target, draggedCard);
		}
		if(source instanceof Pile){
			Pile p = (Pile) source;
			move = new WasteToUpFoundationMove(p, target, draggedCard);
		}
		
		//If move was successful push it onto the game
		if(move.doMove(game))
			this.game.pushMove(move);
		//else return the dragged card to its source
		else
			sourceView.returnWidget(draggedObject);
		
		//Redraw all affected widgets
		this.game.refreshWidgets();
		
		//Have the container release the dragged object 
		c.releaseDraggingObject();
		c.repaint();
	}
	
}
