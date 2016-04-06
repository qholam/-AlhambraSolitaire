package qholam.alhambra.controller;

import java.awt.event.MouseEvent;

import ks.common.games.Solitaire;
import ks.common.model.Card;
import ks.common.model.Column;
import ks.common.model.Element;
import ks.common.model.Pile;
import ks.common.view.CardView;
import ks.common.view.Container;
import ks.common.view.PileView;
import ks.common.view.Widget;
import qholam.alhambra.model.ReserveToWastePileMove;

public class WastePileController extends java.awt.event.MouseAdapter{
	protected Solitaire game;
	protected PileView waste;
	
	public WastePileController(Solitaire g, PileView p){
		super();
		
		game = g;
		waste = p;
	}
	
	/**
	 * Handles mousePressed event for waste pile
	 * @param e MouseEvent
	 */
	public void mousePressed(MouseEvent e){
		//Get the CardView for the top card of the waste pile
		CardView cardView = waste.getCardViewForTopCard(e);
		//checks if card is null
		if(cardView == null)
			return;
		
		//Pass the dragged card to the Container and let it handle it
		Container c = game.getContainer();
		c.setActiveDraggingObject(cardView, e);
		//let container know the source of the dragged card
		c.setDragSource(waste);
		
		//redraw the pile
		waste.redraw();
	}
	
	/**
	 * Handles mouseReleased event for waste pile
	 * @param e MouseEvent
	 */
	public void mouseReleased(MouseEvent e){
		//get target pile
		Pile target = (Pile) waste.getModelElement();
		
		//Get the container for this game
		Container c = this.game.getContainer();
		
		//get the CardView of the card being dragged
		CardView draggedObject = (CardView) c.getActiveDraggingObject();
		//checks if anything is actually being dragged
		if(draggedObject == c.getNothingBeingDragged())
			return;
		//Get the Card object reference by the CardView
		Card draggedCard = (Card) draggedObject.getModelElement();
		
		//Get the model element of the widget of the source that initiated
		//the drag, found from the Container
		Widget sourceView = c.getDragSource();
		Element source = sourceView.getModelElement();
		
		//Check to make sure source is reserve column
		if(!(source instanceof Column)){
			sourceView.returnWidget(draggedObject); 
		}
		else{
			//else it is a Column
			Column from = (Column) source;
			
			//make the move object for this event
			ReserveToWastePileMove move = new ReserveToWastePileMove(from, target, draggedCard);
			
			//If move was successfully made then push it on to the game
			if(move.doMove(game)){
				this.game.pushMove(move);
			}
			else{//if move fails return it to the source
				sourceView.returnWidget(draggedObject);
			}
		}
		
		//redraw all affected widgets
		this.game.refreshWidgets();
		
		//Have the container released the object being dragged
		c.releaseDraggingObject();
		c.repaint();
	}
}
