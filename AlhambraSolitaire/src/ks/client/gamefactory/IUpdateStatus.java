package ks.client.gamefactory;

/**
 * When a game is in play, either:
 * 
 * (a) the game is won, in which case incrementNumWins
 * (b) player requests to reset hands
 * (c) player chooses to advance to new hand.
 * 
 * That's it.
 * 
 * During routine play, the score is updated via the 
 * updateScore method.
 * 
 * @author George Heineman
 *
 */
public interface IUpdateStatus {
	/** Increment the number of wins. */
	void incrementNumWins();
	
	/** Increment number of new hands. */
	void incrementNumNewHands();
	
	/** Update score (can go up or down). */
	void updateScore (int score);
}
