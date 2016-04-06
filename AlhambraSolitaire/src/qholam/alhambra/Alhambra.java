package qholam.alhambra;

import java.awt.Dimension;

import ks.client.gamefactory.GameWindow;
import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.games.Solitaire;
import ks.common.model.Column;
import ks.common.model.Deck;
import ks.common.model.MultiDeck;
import ks.common.model.Pile;
import ks.common.view.CardImages;
import ks.common.view.ColumnView;
import ks.common.view.DeckView;
import ks.common.view.IntegerView;
import ks.common.view.PileView;
import ks.launcher.Main;
import qholam.alhambra.controller.AceUpFoundationController;
import qholam.alhambra.controller.DeckController;
import qholam.alhambra.controller.KingDownFoundationController;
import qholam.alhambra.controller.ReserveColumnController;
import qholam.alhambra.controller.WastePileController;

public class Alhambra extends Solitaire{
	/** Deck **/
	protected MultiDeck deck;
	
	/** Waste Pile **/
	protected Pile  waste;
	
	/** 8 Reserve Columns **/
	protected Column reserve1, reserve2, reserve3, reserve4, reserve5, reserve6, reserve7, reserve8;
	
	/** 4 up foundations **/
	protected Pile up1, up2, up3, up4;
	
	/** 4 down foundations **/
	protected Pile down1, down2, down3, down4;
	
	/** Number of redeals **/
	protected int numRedeals;
	
	/** View for Deck **/
	protected DeckView deckView;
	
	/** View for Waste pile **/
	protected PileView wasteView; 
	
	/** Views for the 8 Reserve Columns **/
	protected ColumnView reserveView1, reserveView2, reserveView3, reserveView4, reserveView5, reserveView6, reserveView7, reserveView8;
	
	/** Views for the 4 up foundations **/
	protected PileView upView1, upView2, upView3, upView4;
	
	/** Views for the 4 down foundations**/
	protected PileView downView1, downView2, downView3, downView4;
	
	/** View for the score **/
	protected IntegerView scoreView;
	
	/** View for the number of cards in deck **/ 
	protected IntegerView numLeftView;
	
	/** Code to launch solitaire variation. */
	public static void main (String []args) {
		// Seed is to ensure we get the same initial cards every time.
		GameWindow gw = Main.generateWindow(new Alhambra(), Deck.OrderByRank);
		gw.setVisible(true);
	}
	
	@Override
	/**
	 * @returns String returns the name of the Solitaire variant
	 */
	public String getName() {
		return "Alhambra";
	}

	@Override
	/**
	 * Game is won when all foundation piles are complete
	 * @return boolean Returns true if win conditions for game is met.
	 */
	public boolean hasWon() {
		return up1.peek().isKing() && up2.peek().isKing() && up3.peek().isKing() && up4.peek().isKing() &&
			   down1.peek().isAce() && down2.peek().isAce() && down3.peek().isAce() && down4.peek().isAce();
	}

	@Override
	public void initialize() {
		// Initialize model, view, and controllers.
		initializeModels(getSeed());
		initializeViews();
		initializeControllers();

		/*PREPARE GAME*/
		waste.add(deck.get());
		up1.add(deck.get());
		up2.add(deck.get());
		up3.add(deck.get());
		up4.add(deck.get());
		down1.add(deck.get());
		down2.add(deck.get());
		down3.add(deck.get());
		down4.add(deck.get());
		// we have dealt nine cards
		updateNumberCardsLeft (-9);
		for(int i = 0; i < 4; i++){
			reserve1.add(deck.get());
			reserve2.add(deck.get());
			reserve3.add(deck.get());
			reserve4.add(deck.get());
			reserve5.add(deck.get());
			reserve6.add(deck.get());
			reserve7.add(deck.get());
			reserve8.add(deck.get());
			//update the number of cards left
			updateNumberCardsLeft(-8);
		}
	}

	/**
	 * initialize the models
	 * @param seed Seed used to shuffle deck
	 */
	public void initializeModels(int seed){
		//Number of cards in deck is initially 104
		numLeft = getNumLeft();
		numLeft.setValue(104);
		//Score is 0 at first
		score = getScore();
		score.setValue(0);
		//Number of redeals is 0 at first
		numRedeals = 0;
		
		//Create the deck
		deck = new MultiDeck("deck", 2);
		deck.create(seed);
		this.addModelElement(deck);
		
		//Create the waste Pile
		waste = new Pile("waste");
		this.addModelElement(waste);
		
		//Create the 8 reserves
		reserve1 = new Column("reserve1");
		reserve2 = new Column("reserve2");
		reserve3 = new Column("reserve3");
		reserve4 = new Column("reserve4");
		reserve5 = new Column("reserve5");
		reserve6 = new Column("reserve6");
		reserve7 = new Column("reserve7");
		reserve8 = new Column("reserve8");
		this.addModelElement(reserve1);
		this.addModelElement(reserve2);
		this.addModelElement(reserve3);
		this.addModelElement(reserve4);
		this.addModelElement(reserve5);
		this.addModelElement(reserve6);
		this.addModelElement(reserve7);
		this.addModelElement(reserve8);
	
		//Create the 4 up foundations
		up1 = new Pile("up1");
		up2 = new Pile("up2");
		up3 = new Pile("up3");
		up4 = new Pile("up4");
		this.addModelElement(up1);
		this.addModelElement(up2);
		this.addModelElement(up3);
		this.addModelElement(up4);
		
		//Create the 4 down foundations
		down1 = new Pile("down1");
		down2 = new Pile("down2");
		down3 = new Pile("down3");
		down4 = new Pile("down4");
		this.addModelElement(down1);
		this.addModelElement(down2);
		this.addModelElement(down3);
		this.addModelElement(down4);
		
	}
	
//	@Override
	/*
	public String getDeckType() {
		return "tiny";
	}
	*/
	
	/**
	 * Initialize the views
	 */
	public void initializeViews(){
		//Get card image to see dimensions
		CardImages c = this.getCardImages();
		
		//set height and width of card
		int height = c.getHeight();
		int width = c.getWidth();
		
		//Set up view for deck
		deckView = new DeckView(deck);
		deckView.setBounds(150 + 4 * width , 80 + 3 * height, width, height);
		this.addViewWidget(deckView);
		
		//Set up view for up foundations
		upView1 = new PileView(up1);
		upView1.setBounds(40 + width, 20, width, height);
		this.addViewWidget(upView1);
		
		upView2 = new PileView(up2);
		upView2.setBounds(60 + 2 * width, 20, width, height);
		this.addViewWidget(upView2);
		
		upView3 = new PileView(up3);
		upView3.setBounds(80 + 3 * width, 20, width, height);
		this.addViewWidget(upView3);
		
		upView4 = new PileView(up4);
		upView4.setBounds(100 + 4 * width, 20, width, height);
		this.addViewWidget(upView4);
		
		//Set up view for the down foundations
		downView1 = new PileView(down1);
		downView1.setBounds(140 + 6 * width, 20, width, height);
		this.addViewWidget(downView1);

		downView2 = new PileView(down2);
		downView2.setBounds(160 + 7 * width, 20, width, height);
		this.addViewWidget(downView2);
		
		downView3 = new PileView(down3);
		downView3.setBounds(180 + 8 * width, 20, width, height);
		this.addViewWidget(downView3);
		
		downView4 = new PileView(down4);
		downView4.setBounds(200 + 9 * width, 20, width, height);
		this.addViewWidget(downView4);
		
		//Set up reserves
		reserveView1 = new ColumnView(reserve1);
		reserveView1.setBounds(80 + width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView1);

		reserveView2 = new ColumnView(reserve2);
		reserveView2.setBounds(100 + 2 * width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView2);
		
		reserveView3 = new ColumnView(reserve3);
		reserveView3.setBounds(120 + 3 * width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView3);
		
		reserveView4 = new ColumnView(reserve4);
		reserveView4.setBounds(140 + 4 * width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView4);
		
		reserveView5 = new ColumnView(reserve5);
		reserveView5.setBounds(160 + 5 * width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView5);
		
		reserveView6 = new ColumnView(reserve6);
		reserveView6.setBounds(180 + 6 * width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView6);
		
		reserveView7 = new ColumnView(reserve7);
		reserveView7.setBounds(200 + 7 * width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView7);
		
		reserveView8 = new ColumnView(reserve8);
		reserveView8.setBounds(220 + 8 * width, 60 + height, width, 2*height);
		this.addViewWidget(reserveView8);
		
		//Set up view for waste pile
		wasteView = new PileView(waste);
		wasteView.setBounds(160 + 5 * width, 80 + 3 * height, width, height);
		this.addViewWidget(wasteView);
		
		//Set up view for score
		scoreView = new IntegerView(score);
		scoreView.setBounds(width, 70 + 4 * height, 100, 100);
		this.addViewWidget(scoreView);
		
		//Set up view for numCards
		numLeftView = new IntegerView(numLeft);
		numLeftView.setBounds(100 + width, 70 + 4 * height, 100, 100);
		this.addViewWidget(numLeftView);
	}
	
//	@Override
	/**
	 * Change dimensions of the board
	 * @return Dimension Return the new preferred dismensions
	 */
	public Dimension getPreferredSize(){
		return new Dimension(1000,700);
	}
	
	/**
	 * Return the number of redeals
	 */
	public int getNumRedeals(){
		return numRedeals;
	}
	
	/**
	 * Initialize the controllers
	 */
	public void initializeControllers(){
		//Controllers for ace up foundations
		upView1.setMouseAdapter(new AceUpFoundationController(this, upView1));
		upView2.setMouseAdapter(new AceUpFoundationController(this, upView2));
		upView3.setMouseAdapter(new AceUpFoundationController(this, upView3));
		upView4.setMouseAdapter(new AceUpFoundationController(this, upView4));
		
		//Controllers for king down foundations
		downView1.setMouseAdapter(new KingDownFoundationController(this, downView1));
		downView2.setMouseAdapter(new KingDownFoundationController(this, downView2));
		downView3.setMouseAdapter(new KingDownFoundationController(this, downView3));
		downView4.setMouseAdapter(new KingDownFoundationController(this, downView4));
		
		//Controllers for Reserve Columns
		reserveView1.setMouseAdapter(new ReserveColumnController(this, reserveView1));
		reserveView2.setMouseAdapter(new ReserveColumnController(this, reserveView2));
		reserveView3.setMouseAdapter(new ReserveColumnController(this, reserveView3));
		reserveView4.setMouseAdapter(new ReserveColumnController(this, reserveView4));
		reserveView5.setMouseAdapter(new ReserveColumnController(this, reserveView5));
		reserveView6.setMouseAdapter(new ReserveColumnController(this, reserveView6));
		reserveView7.setMouseAdapter(new ReserveColumnController(this, reserveView7));
		reserveView8.setMouseAdapter(new ReserveColumnController(this, reserveView8));
		
		//Controller for Deck
		deckView.setMouseAdapter(new DeckController(this));
		
		//Controller for Waste Pile
		wasteView.setMouseAdapter(new WastePileController(this, wasteView));
		
		//Controller for view of score
		scoreView.setMouseAdapter(new SolitaireReleasedAdapter(this));
		
		//Controller for view of number cards in deck
		numLeftView.setMouseAdapter(new SolitaireReleasedAdapter(this));
	}
}
