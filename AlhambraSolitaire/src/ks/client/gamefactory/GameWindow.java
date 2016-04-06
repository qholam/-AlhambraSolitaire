package ks.client.gamefactory;

import ks.client.gamefactory.skin.*;
import ks.common.games.Solitaire;
import ks.common.games.SolitaireSolver;
import ks.common.view.BackgroundCopier;
import ks.common.view.Container;

import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Button;

/**
 * Window used to locally test a solitaire variation prior to integrating with 
 * the base system.
 *
 * Creation date: (9/25/01 9:19:21 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class GameWindow extends java.awt.Frame implements IUpdateStatus {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3544957666427353398L;
	private java.awt.Panel ivjContentsPane = null;
	private java.awt.Panel ivjTopPane = null;
	
	private Container ivjContainer = null;

	/** Name of the current user. Needed to include this value within the Container. */
	protected String currentUser = "test";

	/** Initial width for the window. Must never shrink less than this. */
	protected int initialWidth = 769;

	/** Initial height for the window. Must never shrink less than this. */
	protected int initialHeight = 635;

	/** Last height for the window. Start with initial height. */
	protected int lastHeight = 635;

	/** Last width for the window. Start with initial width. */
	protected int lastWidth = 769;

	/** Since V2.2: Must hold active game until timer expires. */
	Solitaire activeGame = null;  //  @jve:decl-index=0:

	/** Since V1.6.2: The solver. */
	protected SolitaireSolver solver = null;
	
	/** Enable tester to try out new games. */
	private Button newGameButton = null;
	
	private Button solveButton = null;

	boolean includeSolver = false;
	
	/**
	 * Event handler for mouse events 
	 * 
	 * @author George Heineman
	 *
	 */
	class IvjEventHandler extends java.awt.event.MouseAdapter {
	
		public void mouseClicked(java.awt.event.MouseEvent e) {
			getSolitaireContainer().processMouseEvent(e);
		}
		
		public void mouseDragged(java.awt.event.MouseEvent e) {
			getSolitaireContainer().processMouseEvent(e);
		}
		
		public void mouseMoved(java.awt.event.MouseEvent e) {
			getSolitaireContainer().processMouseEvent(e);
		}
		
		public void mousePressed(java.awt.event.MouseEvent e) {
			getSolitaireContainer().processMouseEvent(e);
		}
		
		public void mouseReleased(java.awt.event.MouseEvent e) {
			getSolitaireContainer().processMouseEvent(e);
		}
	}
	
	/**
	 * GameWindow constructor comment.
	 */
	public GameWindow() {
		super();
		includeSolver = false;
		initialize();
	}
	
	/**
	 * GameWindow constructor comment.
	 * @param title java.lang.String
	 */
	public GameWindow(String title) {
		super(title);
		includeSolver = false;
		initialize();
	}
	
	public GameWindow(boolean includeSolver) {
		this.includeSolver = includeSolver;
		initialize();
	}

	/**
	 * Construct an image given the specified URL
	 * Creation date: (10/9/01 8:26:51 PM)
	 * @return java.awt.Image
	 * @param u java.net.URL
	 */
	protected java.awt.Image createBackgroundImage(java.net.URL u) {
		java.awt.Image img = java.awt.Toolkit.getDefaultToolkit().getImage(u);
		java.awt.MediaTracker tracker = new java.awt.MediaTracker (this);
		tracker.addImage(img, 0);

		try {
			tracker.waitForAll();
		} catch (InterruptedException ie) {
			// we have been interrupted, but this is not overly problematic.
			// we will be slowed down on demand.
			System.err.println ("GameWindow::createBackgroundImage() interrupted:" + ie.toString());
		}

		return img;
	}

	/**
	 * Return the solitaire container.
	 * @return ks.common.view.Container
	 */
	private Container getSolitaireContainer() {
		if (ivjContainer == null) {
			ivjContainer = new Container();
			ivjContainer.setUpdateStatus(this);
			ivjContainer.setName("Container");
			ivjContainer.setBackground(new java.awt.Color(0,138,0));
			ivjContainer.setBounds(0, 0, 764, 512);  // was 14,52
		}
		
		return ivjContainer;
	}
	
	Panel getControlPane() {
		if (ivjTopPane == null) {
			try {
				ivjTopPane = new java.awt.Panel();
				ivjTopPane.setName("TopPane");
				ivjTopPane.setSize(769, 40);
				ivjTopPane.setLayout(null);
				ivjTopPane.setBackground(new java.awt.Color(0,138,0));
				ivjTopPane.add(getNewGameButton(), null);
				if (includeSolver) {
					ivjTopPane.add(getSolveButton(), null);
				}
				
			} catch (Exception e) {

			}
		}
		return ivjTopPane;
	}
	
	/**
	 * Return the ContentsPane property value.
	 * @return java.awt.Panel
	 */
	Panel getContentsPane() {
		if (ivjContentsPane == null) {
			try {
				ivjContentsPane = new java.awt.Panel();
				ivjContentsPane.setName("ContentsPane");
				ivjContentsPane.setLayout(null);
				ivjContentsPane.setBackground(new java.awt.Color(0,138,0));
				ivjContentsPane.add(getSolitaireContainer(), getSolitaireContainer().getName());
			} catch (Exception e) {

			}
		}
		return ivjContentsPane;
	}
	
	/**
	 * Returns the Container in which solitaire games are being executed.
	 * Creation date: (10/3/01 1:12:29 AM)
	 * @return ks.common.view.Container
	 */
	public Container getGameContainer() {
		return getSolitaireContainer();
	}

	/**
	 * Initializes connections
	 */
	private void initConnections() {
		getSolitaireContainer().addMouseListener(new IvjEventHandler());
		getSolitaireContainer().addMouseMotionListener(new IvjEventHandler());
	}
	
	/**
	 * Initialize the class.
	 */
	private void initialize() {
		setName("GameWindow");
		setLayout(new java.awt.BorderLayout());
		setSize(769, 635);
		setTitle("Sample Game");
		add(getControlPane(), "North");
		add(getContentsPane(), "Center");
		initConnections();
	}
	
	/**
	 * Initialize game canvas using specific solitaire game. 
	 * 
	 * Note that nothing will actually become visible until activate(boolean) is called.
	 * 
	 * Called by the entity that creates the GameWindow. Must be called before setting GameWindow 
	 * to Visible.
	 * <p>
	 * since v1.6.2 this enables a button "Solve" if the solitaire game is an
	 * instance of SolvableSolitaire.
	 * <p>
	 * Creation date: (10/1/01 9:10:22 PM)
	 * @param game Solitaire
	 */
	public boolean initialize(Solitaire game) {
		activeGame = game;

		// resize GameWindow to fit the size of the Solitaire preferred size.
		java.awt.Dimension d = game.getPreferredSize();
		setSize (d);

		// Get Game Container and initialize game within it. Make sure
		// that container is as large as preferred size of solitaire...
		Container cont = getGameContainer();
		cont.setBounds(0, 0, Math.max(764, d.width), Math.max(512, d.height));

		if (!cont.initialize (game)) {
			return false;
		}

		// Default Visitor (if one not already set)
		if (cont.getVisitor() == null) {
			cont.setVisitor (new BackgroundCopier(cont.getBackground()));
		}

		cont.setCurrentUser(currentUser);
		return true;
	}

	/**
	 * Set the skin for this GameWindow...
	 */
	public void setSkin (String skinChoice) {
		Container cont = getGameContainer();

		// Default Visitor (if one not already set)
		if (skinChoice.equals (SkinCatalog.STANDARD_SKIN)) {
			cont.setVisitor (new BackgroundCopier(cont.getBackground()));
		} else if (skinChoice.equals (SkinCatalog.SCROLLING_BALLOONS)) {
			java.net.URL u = this.getClass().getResource ("/backgrounds/hot-air-balloons.jpg");
			java.awt.Image image = createBackgroundImage (u);
			if (image != null) {
				cont.setVisitor (new ImageScroller(image, cont));
			} else {
				System.err.println ("Unable to create ImageScroller.");
			}
		} else if (skinChoice.equals (SkinCatalog.FIXED_IMAGE)) {
			java.net.URL u = this.getClass().getResource ("/backgrounds/hot-air-balloons.jpg");
			java.awt.Image image = createBackgroundImage (u);
			if (image != null) {
				cont.setVisitor (new ImageCopier(image, cont));
			} else {
				System.err.println ("Unable to create ImageCopier.");
			}
		} else if (skinChoice.equals (SkinCatalog.BOUNCING_BALLS)) {
			cont.setVisitor (new BouncingBalls (cont));
		} else if (skinChoice.equals (SkinCatalog.PSYCHADELIC)) {
			cont.setVisitor (new Psychedelic (cont, cont.getBackground()));
		} else if (skinChoice.equals (SkinCatalog.MULTIPLE_BOUNCING_BALLS)) {
			cont.setVisitor (new BouncingBalls (cont, true));
		}
		
		// now that skin has changed, force repaint of everything.
		cont.repaintAll();
	}

	/**
	 * This method initializes ivjnewGame	
	 * 	
	 * @return java.awt.Button	
	 */
	private Button getSolveButton() {
		if (solveButton == null) {
			solveButton = new Button();
			solveButton.setBounds(new Rectangle(92, 12, 72, 23));
			solveButton.setActionCommand("");
			solveButton.setLabel("Solve");
			solveButton.setName("solveGame");
			solveButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if (solver == null) {
						solver = new SolitaireSolver(activeGame);
						solver.start();
						solveButton.setLabel("Stop");
					} else {
						solver.stop();
						solver = null;
						solveButton.setLabel("Solve");
					}
				}
			});
		}
		return solveButton;
	}

	/**
	 * This method initializes ivjnewGame	
	 * 	
	 * @return java.awt.Button	
	 */
	private Button getNewGameButton() {
		if (newGameButton == null) {
			newGameButton = new Button();
			newGameButton.setBounds(new Rectangle(12, 12, 72, 23));
			newGameButton.setActionCommand("");
			newGameButton.setLabel("New Hand");
			newGameButton.setName("newGame");
			newGameButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Container cont = getGameContainer();
					cont.forceNextHand();
				}
			});
		}
		return newGameButton;
	}

	/** Validate methods for outgoing game updates */
	public void incrementNumNewHands() {
		System.out.println ("GamePanel: numNewHands++");
	}

	/** Validate methods for outgoing game updates */
	public void incrementNumWins() {
		System.out.println ("GamePanel: numWins++");
	}

	/** Validate methods for outgoing game updates */
	public void updateScore(int score) {
		System.out.println ("updateScore:" + score);
	}

	/**
	 * Override to clean up everything from the Container and down to release
	 * resources.
	 */
	@Override
	public void dispose() {
		ivjContainer.dispose();
		ivjContainer = null;
		super.dispose();
		if (solver != null) {
			solver.stop();
			solver = null;
		}
	}
}
