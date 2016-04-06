package ks.launcher;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import ks.client.gamefactory.GameWindow;
import ks.common.games.Solitaire;
import ks.common.games.SolvableSolitaire;

public class Main {
	
	/** Temporary function to be able to launch Solitaire window. */
	public static GameWindow generateWindow(Solitaire theGame, int seed) {
		boolean trySolve = false;
		if (theGame instanceof SolvableSolitaire) {
			trySolve = true;
		}
		final GameWindow itg = new GameWindow(trySolve);

		itg.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	itg.setVisible(false);
		    	itg.dispose();
		    }
		});
		
		// make visible (to enable downloading of images?)
		itg.setVisible(true);
		itg.setTitle(theGame.getName());

		// initialize our game within this game window.
		theGame.setSeed(seed);

		// prepare game (note: initially the game is inactive waiting for all
		// users to join). Note that the game is not going to be really active
		// until all users have joined the table.
		if (!itg.initialize(theGame)) {
			// not sure what to do
		}
		
		// ONLY INITIALIZE ONCE GAME IS REALLY READY TO PROCEED!
		return itg;
	}
	
	/** Temporary function to be able to launch Solitaire window. */
	public static GameWindow generateWindow(Solitaire theGame, int seed, String skin) {
		
		// make sure skin is set BEFORE it becomes visible.
		boolean solver = false;
		if (theGame instanceof SolvableSolitaire) {
			solver = true;
		}
		final GameWindow itg = new GameWindow(solver);
		itg.setSkin(skin);
		
		itg.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	itg.setVisible(false);
		    	itg.dispose();
		    }
		});
		
		// make visible (to enable downloading of images?)
		itg.setVisible(true);
		itg.setTitle(theGame.getName());

		// initialize our game within this game window.
		theGame.setSeed(seed);

		// prepare game (note: initially the game is inactive waiting for all
		// users to join). Note that the game is not going to be really active
		// until all users have joined the table.
		if (!itg.initialize(theGame)) {
			// not sure what to do
		}
		
		// ONLY INITIALIZE ONCE GAME IS REALLY READY TO PROCEED!
		return itg;
	}

}
