package qholam.alhambra;

import java.awt.Dimension;

import ks.client.gamefactory.GameWindow;
import ks.common.controller.SolitaireReleasedAdapter;
import ks.common.games.Solitaire;
import ks.common.model.Card;
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
		return up1.peek().getRank() == Card.KING && up2.peek().getRank() == Card.KING && up3.peek().getRank() == Card.KING && up4.peek().getRank() == Card.KING &&
			   down1.peek().isAce() && down2.peek().isAce() && down3.peek().isAce() && down4.peek().isAce();
	}

	@Override
	public void initialize() {
		// Initialize model, view, and controllers.
		initializeModels(getSeed());
		initializeViews();
		initializeControllers();

		/*PREPARE GAME*/
		//set up the foundations
		addCardFromDeck(new Card(Card.ACE, Card.SPADES), up1);
		addCardFromDeck(new Card(Card.ACE, Card.CLUBS), up2);
		addCardFromDeck(new Card(Card.ACE, Card.HEARTS), up3);
		addCardFromDeck(new Card(Card.ACE, Card.DIAMONDS), up4);
		addCardFromDeck(new Card(Card.KING, Card.SPADES), down1);
		addCardFromDeck(new Card(Card.KING, Card.CLUBS), down2);
		addCardFromDeck(new Card(Card.KING, Card.HEARTS), down3);
		addCardFromDeck(new Card(Card.KING, Card.DIAMONDS), down4);

		waste.add(deck.get());
		// we have dealt one card to the waste pile
		updateNumberCardsLeft (-1);
						
		//add cards to the reserve column, each reserve column should have 4 cards
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
	 * Find the given card within the deck and adds it to the pile
	 * @param card Card in deck to be added
	 * @param pile Pile in which card will be added too
	 */
	public void addCardFromDeck(Card card, Pile pile){
		//determines whether card was found
		boolean cardFound = false;
		//get the size of the deck
		int size = deck.count();
		//temp deck to stored the popped cards
		MultiDeck tempDeck = new MultiDeck(2);
		
		//go through deck to find the card
		for(int i = 0; i < size; i++){
			//get top card
			Card c = deck.get();
			
			//Check if correct card is found add it to the pile
			if(c.equals(card)){
				pile.add(c);
				
				//card was found
				cardFound = true;
				
				//break out of loop
				break;
			}
			else{
				tempDeck.add(c);
			}
		}
		
		//move the cards from the temp deck back into the main deck
		int tempSize = tempDeck.count();
		for(int i = 0; i < tempSize; i++){
			deck.add(tempDeck.get());
		}
		
		//if the card was found update the number of cards in the deck
		if(cardFound){
			this.updateNumberCardsLeft(-1);
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
	 * update number of redeals
	 * @param i To be added to current number of redeals
	 */
	public void updateNumRedeals(int i){
		numRedeals += i;
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

	/**
	 * @return the deck
	 */
	public MultiDeck getDeck() {
		return deck;
	}

	/**
	 * @param deck the deck to set
	 */
	public void setDeck(MultiDeck deck) {
		this.deck = deck;
	}

	/**
	 * @return the waste
	 */
	public Pile getWaste() {
		return waste;
	}

	/**
	 * @param waste the waste to set
	 */
	public void setWaste(Pile waste) {
		this.waste = waste;
	}

	/**
	 * @return the reserve1
	 */
	public Column getReserve1() {
		return reserve1;
	}

	/**
	 * @param reserve1 the reserve1 to set
	 */
	public void setReserve1(Column reserve1) {
		this.reserve1 = reserve1;
	}

	/**
	 * @return the reserve2
	 */
	public Column getReserve2() {
		return reserve2;
	}

	/**
	 * @param reserve2 the reserve2 to set
	 */
	public void setReserve2(Column reserve2) {
		this.reserve2 = reserve2;
	}

	/**
	 * @return the reserve3
	 */
	public Column getReserve3() {
		return reserve3;
	}

	/**
	 * @param reserve3 the reserve3 to set
	 */
	public void setReserve3(Column reserve3) {
		this.reserve3 = reserve3;
	}

	/**
	 * @return the reserve4
	 */
	public Column getReserve4() {
		return reserve4;
	}

	/**
	 * @param reserve4 the reserve4 to set
	 */
	public void setReserve4(Column reserve4) {
		this.reserve4 = reserve4;
	}

	/**
	 * @return the reserve5
	 */
	public Column getReserve5() {
		return reserve5;
	}

	/**
	 * @param reserve5 the reserve5 to set
	 */
	public void setReserve5(Column reserve5) {
		this.reserve5 = reserve5;
	}

	/**
	 * @return the reserve6
	 */
	public Column getReserve6() {
		return reserve6;
	}

	/**
	 * @param reserve6 the reserve6 to set
	 */
	public void setReserve6(Column reserve6) {
		this.reserve6 = reserve6;
	}

	/**
	 * @return the reserve7
	 */
	public Column getReserve7() {
		return reserve7;
	}

	/**
	 * @param reserve7 the reserve7 to set
	 */
	public void setReserve7(Column reserve7) {
		this.reserve7 = reserve7;
	}

	/**
	 * @return the reserve8
	 */
	public Column getReserve8() {
		return reserve8;
	}

	/**
	 * @param reserve8 the reserve8 to set
	 */
	public void setReserve8(Column reserve8) {
		this.reserve8 = reserve8;
	}

	/**
	 * @return the up1
	 */
	public Pile getUp1() {
		return up1;
	}

	/**
	 * @param up1 the up1 to set
	 */
	public void setUp1(Pile up1) {
		this.up1 = up1;
	}

	/**
	 * @return the up2
	 */
	public Pile getUp2() {
		return up2;
	}

	/**
	 * @param up2 the up2 to set
	 */
	public void setUp2(Pile up2) {
		this.up2 = up2;
	}

	/**
	 * @return the up3
	 */
	public Pile getUp3() {
		return up3;
	}

	/**
	 * @param up3 the up3 to set
	 */
	public void setUp3(Pile up3) {
		this.up3 = up3;
	}

	/**
	 * @return the up4
	 */
	public Pile getUp4() {
		return up4;
	}

	/**
	 * @param up4 the up4 to set
	 */
	public void setUp4(Pile up4) {
		this.up4 = up4;
	}

	/**
	 * @return the down1
	 */
	public Pile getDown1() {
		return down1;
	}

	/**
	 * @param down1 the down1 to set
	 */
	public void setDown1(Pile down1) {
		this.down1 = down1;
	}

	/**
	 * @return the down2
	 */
	public Pile getDown2() {
		return down2;
	}

	/**
	 * @param down2 the down2 to set
	 */
	public void setDown2(Pile down2) {
		this.down2 = down2;
	}

	/**
	 * @return the down3
	 */
	public Pile getDown3() {
		return down3;
	}

	/**
	 * @param down3 the down3 to set
	 */
	public void setDown3(Pile down3) {
		this.down3 = down3;
	}

	/**
	 * @return the down4
	 */
	public Pile getDown4() {
		return down4;
	}

	/**
	 * @param down4 the down4 to set
	 */
	public void setDown4(Pile down4) {
		this.down4 = down4;
	}

	/**
	 * @return the deckView
	 */
	public DeckView getDeckView() {
		return deckView;
	}

	/**
	 * @param deckView the deckView to set
	 */
	public void setDeckView(DeckView deckView) {
		this.deckView = deckView;
	}

	/**
	 * @return the wasteView
	 */
	public PileView getWasteView() {
		return wasteView;
	}

	/**
	 * @param wasteView the wasteView to set
	 */
	public void setWasteView(PileView wasteView) {
		this.wasteView = wasteView;
	}

	/**
	 * @return the reserveView1
	 */
	public ColumnView getReserveView1() {
		return reserveView1;
	}

	/**
	 * @param reserveView1 the reserveView1 to set
	 */
	public void setReserveView1(ColumnView reserveView1) {
		this.reserveView1 = reserveView1;
	}

	/**
	 * @return the reserveView2
	 */
	public ColumnView getReserveView2() {
		return reserveView2;
	}

	/**
	 * @param reserveView2 the reserveView2 to set
	 */
	public void setReserveView2(ColumnView reserveView2) {
		this.reserveView2 = reserveView2;
	}

	/**
	 * @return the reserveView3
	 */
	public ColumnView getReserveView3() {
		return reserveView3;
	}

	/**
	 * @param reserveView3 the reserveView3 to set
	 */
	public void setReserveView3(ColumnView reserveView3) {
		this.reserveView3 = reserveView3;
	}

	/**
	 * @return the reserveView4
	 */
	public ColumnView getReserveView4() {
		return reserveView4;
	}

	/**
	 * @param reserveView4 the reserveView4 to set
	 */
	public void setReserveView4(ColumnView reserveView4) {
		this.reserveView4 = reserveView4;
	}

	/**
	 * @return the reserveView5
	 */
	public ColumnView getReserveView5() {
		return reserveView5;
	}

	/**
	 * @param reserveView5 the reserveView5 to set
	 */
	public void setReserveView5(ColumnView reserveView5) {
		this.reserveView5 = reserveView5;
	}

	/**
	 * @return the reserveView6
	 */
	public ColumnView getReserveView6() {
		return reserveView6;
	}

	/**
	 * @param reserveView6 the reserveView6 to set
	 */
	public void setReserveView6(ColumnView reserveView6) {
		this.reserveView6 = reserveView6;
	}

	/**
	 * @return the reserveView7
	 */
	public ColumnView getReserveView7() {
		return reserveView7;
	}

	/**
	 * @param reserveView7 the reserveView7 to set
	 */
	public void setReserveView7(ColumnView reserveView7) {
		this.reserveView7 = reserveView7;
	}

	/**
	 * @return the reserveView8
	 */
	public ColumnView getReserveView8() {
		return reserveView8;
	}

	/**
	 * @param reserveView8 the reserveView8 to set
	 */
	public void setReserveView8(ColumnView reserveView8) {
		this.reserveView8 = reserveView8;
	}

	/**
	 * @return the upView1
	 */
	public PileView getUpView1() {
		return upView1;
	}

	/**
	 * @param upView1 the upView1 to set
	 */
	public void setUpView1(PileView upView1) {
		this.upView1 = upView1;
	}

	/**
	 * @return the upView2
	 */
	public PileView getUpView2() {
		return upView2;
	}

	/**
	 * @param upView2 the upView2 to set
	 */
	public void setUpView2(PileView upView2) {
		this.upView2 = upView2;
	}

	/**
	 * @return the upView3
	 */
	public PileView getUpView3() {
		return upView3;
	}

	/**
	 * @param upView3 the upView3 to set
	 */
	public void setUpView3(PileView upView3) {
		this.upView3 = upView3;
	}

	/**
	 * @return the upView4
	 */
	public PileView getUpView4() {
		return upView4;
	}

	/**
	 * @param upView4 the upView4 to set
	 */
	public void setUpView4(PileView upView4) {
		this.upView4 = upView4;
	}

	/**
	 * @return the downView1
	 */
	public PileView getDownView1() {
		return downView1;
	}

	/**
	 * @param downView1 the downView1 to set
	 */
	public void setDownView1(PileView downView1) {
		this.downView1 = downView1;
	}

	/**
	 * @return the downView2
	 */
	public PileView getDownView2() {
		return downView2;
	}

	/**
	 * @param downView2 the downView2 to set
	 */
	public void setDownView2(PileView downView2) {
		this.downView2 = downView2;
	}

	/**
	 * @return the downView3
	 */
	public PileView getDownView3() {
		return downView3;
	}

	/**
	 * @param downView3 the downView3 to set
	 */
	public void setDownView3(PileView downView3) {
		this.downView3 = downView3;
	}

	/**
	 * @return the downView4
	 */
	public PileView getDownView4() {
		return downView4;
	}

	/**
	 * @param downView4 the downView4 to set
	 */
	public void setDownView4(PileView downView4) {
		this.downView4 = downView4;
	}

	/**
	 * @return the scoreView
	 */
	public IntegerView getScoreView() {
		return scoreView;
	}

	/**
	 * @param scoreView the scoreView to set
	 */
	public void setScoreView(IntegerView scoreView) {
		this.scoreView = scoreView;
	}

	/**
	 * @return the numLeftView
	 */
	public IntegerView getNumLeftView() {
		return numLeftView;
	}

	/**
	 * @param numLeftView the numLeftView to set
	 */
	public void setNumLeftView(IntegerView numLeftView) {
		this.numLeftView = numLeftView;
	}

	/**
	 * @param numRedeals the numRedeals to set
	 */
	public void setNumRedeals(int numRedeals) {
		this.numRedeals = numRedeals;
	}
}
