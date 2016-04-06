package ks.client.gamefactory;


/**
 * Manages the visual layout showing comparable scores for all players in the table.
 * Creation date: (9/25/01 9:29:13 PM)
 * @author: George T. Heineman (heineman@cs.wpi.edu)
 */

import java.awt.*;

import javax.swing.JPanel;

public class PlayerJPanel extends JPanel {
	/**
	 * Keep Eclipse happy
	 */
	private static final long serialVersionUID = 1L;

	/** User info (no more than 4 players per table) */
	protected String[] players = new String[10];

	/** points info (no more than 4 players per table) */
	protected int[] points = new int[10];

	/** num Games Won (no more than 4 players per table) */
	protected int[] gamesWon = new int[10]; 		// more than enough

	/** Whether player is disabled */
	protected boolean[] disabled = new boolean[10];     // more than enough

	/** The number of players in the game. */
	public int numPlayers = 0;

	/** Default visual parameters */
	protected int nameWidth = 180;
	protected int nameOffset = 5;
	protected int barHeight = 7;
	protected int padding = 3;
	/**
	 * PlayerRanking constructor comment.
	 */
	public PlayerJPanel() {
		this(300,100);
	}
	
	/**
	 * PlayerRanking constructor. Pass in width/height and setCanvas size accordingly.
	 */
	public PlayerJPanel(int width, int height) {

		super();
		setSize (width, height);
	}
	/**
	 * Add to this display the player with specifc name.
	 *
	 * Creation date: (9/25/01 9:29:43 PM)
	 * @param name java.lang.String
	 */
	public void addUser(String name) {

		// verify not already here. If tried to add twice, return
		for (int i = 0; i< numPlayers; i++) {
			if (name.equals (players[i])) {
				return;
			}
		}

		players[numPlayers] = new String (name);
		points[numPlayers] = 0;
		gamesWon[numPlayers] = 0;
		disabled[numPlayers] = false;
		
		/** increase count */
		numPlayers++;

		sortPlayers();

		// repaint if visible
		if (isVisible())
			repaint();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:34:37 PM)
	 * @return int
	 */
	public int getBarHeight() {
		return barHeight;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:33:26 PM)
	 * @return int
	 */
	public int getNameOffset() {
		return nameOffset;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:29:35 PM)
	 * @return int
	 */
	public int getNameWidth() {
		return nameWidth;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 9:36:22 PM)
	 * @return int
	 */
	public int getNumPlayers() {
		return numPlayers;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:54:54 PM)
	 * @return int
	 */
	public int getPadding() {
		return padding;
	}
	/**
	 * Paint this object.
	 *
	 * A list of rows, each of the form User (pts) followed by a box proportional to 1.5 times
	 * the largest point count so far.
	 *
	 * The rows are ordered by games won, followed next by points.
	 * Creation date: (9/25/01 9:32:14 PM)
	 * @param g java.awt.Graphics
	 */
	@Override
	public void paintComponent(java.awt.Graphics g) {
		super.paintComponent(g); 

		// calculate maxPoints: always ensure there is at least some size.
		int maxPoints = 0;
		for (int i = 0; i< numPlayers; i++) { if (maxPoints < points[i]) maxPoints = points[i]; }
		if (maxPoints == 0) maxPoints = 30;

		int realHeight = padding + getFont().getSize();
		for (int i = 0; i< numPlayers; i++) {
			String st = players[i];
			if (st.length() > 15) st = st.substring (0, 12) + "...";
			
			if (disabled[i]) {
				g.drawString (players[i], 
						nameOffset, (i+1)*realHeight);
			} else {
				Color old = g.getColor();
				g.setColor(Color.lightGray);
				g.drawString (players[i], nameOffset, (i+1)*realHeight);
				g.setColor(old);
			}

			// add box in here...
			int off = (realHeight - barHeight) / 2;
			g.fillRect (nameOffset + nameWidth, off + realHeight*i,
					(int) ((100 * points[i]) / maxPoints), barHeight);

			// points after the lines.
			g.drawString ("(" + points[i] + ")", 
					nameOffset + nameWidth + 105, (i+1)*realHeight);
			
			// also, the games won as yellow boxes within rectangle (red counts 10)
			for (int j = 0, max = gamesWon[i]; max > 0; j++) {		
				//for (int j = 0; j < gamesWon[i]; j++) {
				if (max > 10) {
					g.setColor (Color.red);
					max -= 10;
				} else {
					g.setColor (Color.yellow);
					max -= 1;
				}

				g.fillRect (nameWidth + nameOffset + 2 + barHeight*j, 1 + off + realHeight*i, 4, 4);
			}
			g.setColor (Color.black);
		}

	}
	/**
	 * Remove user from display
	 *
	 * Creation date: (9/25/01 9:29:43 PM)
	 * @param name java.lang.String
	 */
	public void removeUser(String name) {

		// verify that this user is in list.
		int found = -1;
		for (int i = 0; i< numPlayers; i++) {
			if (name.equals (players[i])) {
				found = i;
				break;
			}
		}

		if (found == -1) return;

		players[found] = players[numPlayers-1];
		points[found] = points[numPlayers-1];
		gamesWon[found] = gamesWon[numPlayers-1];

		/** decrease count */
		numPlayers--;

		sortPlayers();

		// repaint if visible
		if (isVisible())
			repaint();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:34:37 PM)
	 * @param newBarHeight int
	 */
	public void setBarHeight(int newBarHeight) {
		barHeight = newBarHeight;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:33:26 PM)
	 * @param newNameOffset int
	 */
	public void setNameOffset(int newNameOffset) {
		nameOffset = newNameOffset;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:29:35 PM)
	 * @param newNameWidth int
	 */
	public void setNameWidth(int newNameWidth) {
		nameWidth = newNameWidth;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (9/25/01 10:54:54 PM)
	 * @param newPadding int
	 */
	public void setPadding(int newPadding) {
		padding = newPadding;
	}
	/**
	 * Sort all players in reverse order. First by num games won (descending),
	 * then by total points (descending), then by name (ascending)
	 * Creation date: (9/25/01 9:35:30 PM)
	 */
	protected void sortPlayers() {
		for (int i = 0; i < numPlayers - 1; i++) {
			for (int j = i+1; j < numPlayers; j++) {
				if (gamesWon[i] > gamesWon[j]) continue;
				if (gamesWon[i] == gamesWon[j]) {
					if (points[i] > points[j]) continue;
					if (points[i] == points[j]) {
						if (players[i].compareTo (players[j]) < 0) continue;
					}
				}

				/** swap ith and jth entries */
				int t = points[i]; points[i] = points[j]; points[j] = t;
				t = gamesWon[i]; gamesWon[i] = gamesWon[j]; gamesWon[j] = t;
				String ts = players[i]; players[i] = players[j]; players[j] = ts;
			}
		}
	}
	/**
	 * Update the points for this player, including points (accumulated), number of hands won.
	 *
	 * After each update, sort (may be inefficient, but number of entries will be <= 6
	 *
	 * Creation date: (9/25/01 9:30:44 PM)
	 * @param name java.lang.String
	 * @param numHandsWon int
	 * @param points int
	 */
	public void updatePlayerInfo(String name, int numHandsWon, int points) {
		if (name == null) return;

		for (int i = 0; i< numPlayers; i++) {
			if (name.equals (players[i])) {
				/** Update entry */
				this.points[i] = points;
				gamesWon[i] = numHandsWon;
				sortPlayers();

				// repaint if visible
				if (isVisible()) {
					repaint();			
				}
				
				return;
			}
		}
	}
	
	/** Helper methods for testing. */
	public int getPlayerPoints (String name) {
		if (name == null) return -1;

		for (int i = 0; i< numPlayers; i++) {
			if (name.equals (players[i])) {
				return points[i];
			}
		}
		
		return -1;
	}
	
	/** Helper methods for testing. */
	public int getPlayerHandsWon (String name) {
		if (name == null) return -1;

		for (int i = 0; i< numPlayers; i++) {
			if (name.equals (players[i])) {
				return gamesWon[i];
			}
		}
		
		return -1;
	}

	/**
	 * Testing method to validate disabled status for given player.
	 * <p>
	 * Returns false if the player is not a member of the table.
	 * 
	 * @param name
	 * @return whether player is disabled.
	 */
	public boolean isDisabled (String name) {
		if (name == null) { return false; }

		for (int i = 0; i< numPlayers; i++) {
			if (name.equals (players[i])) {
				return disabled[i];
			}
		}
		
		// not in the game.
		return false;
	}
	
	/**
	 * When player leaves table, points remain on the board but
	 * we want to visually show that the player has left.
	 * 
	 * @param playerID  exiting player
	 * @return  <code>true</code> if the player was a member of the table;
	 * <code>false</code> otherwise.
	 */
	public boolean disablePlayer(String name) {
		if (name == null) { return false; }

		for (int i = 0; i< numPlayers; i++) {
			if (name.equals (players[i])) {
				disabled[i] = true;
				return true;
			}
		}
		
		// not in the game.
		return false;
	}
}
