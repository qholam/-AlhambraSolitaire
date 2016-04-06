package ks.common.view;

import java.awt.*;
import java.security.Permission;

import javax.swing.JPanel;

import ks.client.gamefactory.IUpdateStatus;
import ks.common.controller.MouseManager;
import ks.common.controller.UndoAdapter;
import ks.common.games.Solitaire;
import ks.common.model.Element;
import ks.common.view.CardImages;

/**
 * The container provides the environment within which all Solitaire plug-ins
 * execute.
 * <p>
 * The container is initialized with a Solitaire Plug-in, and manages the interaction
 * between the user and the game. All mouse events pass through the container, and
 * are delivered to the appropriate widget based upon the (x,y) point of the mouse
 * event.
 * <p>
 * All Undo requests (right mouse clicks) are first identified by the container, and
 * are passed along to the solitaire game.
 * <p>
 * The container manages any Widgets that are being dragged on the screen. Typically,
 * these are <code>CardView</code> widgets, but there is no limitation on this, and
 * conceivably any widget could be dragged. The container also manages the Widget that
 * initiated the drag event, called the <code>dragSource</code>.
 * <p>
 * Note: Container must be a java.awt.Component because many of the low-level
 *       image routines expect an ImageObserver, satisfied by java.awt.Canvas.
 *       In addition, this class implements key methods enabling events to be
 *       interpreted and delivered to the Solitaire subclass being viewed.
 * <p>
 * Note: Since V1.6, the container also is capable of remembering the source widget 
 *       from which a drag originates, if the Controller invokes <code>setDragSource 
 *       (Widget w)</code> accordingly. This source widget can then be asked to 
 *       <code>returnWidget(Widget w)</code> a widget extracted from the source as 
 *       part of the dragging procedure.
 * <p>
 * Note: Since V2.2, there is additional logic to enable 'shared state'
 *       model elements to exist. That is, there is some server-side
 *       state that stores information about shared state that is only
 *       accessed
 * 
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class Container extends JPanel {

	// If true, then EclEmma can't function...
	// make true on deployment!
	private boolean applySecurity = false; 

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 3977859566358640438L;

	/** Widgets stored by this container. */
	protected Widget[] widgets;

	/** number of widgets. */
	protected int numWidgets;

	/** Growth/initial size of the array */
	protected int potentialSize = 16;

	/** The game to be played. */
	protected Solitaire game = null;

	/** Entity to whom information about game is delivered. */
	IUpdateStatus status;

	/** Manage an object that is currently being dragged (if at all). */
	protected Widget activeDraggingObject = null;

	/** 
	 * The card images used in this container (as specified by the plugin).
	 * Container is responsible for disposing of memory once container is done.
	 * 
	 * @since V2.1
	 */
	protected CardImages cards = null;

	/**
	 * The source widget from which a drag originated.
	 * @since v1.6
	 */
	protected Widget dragSource = null;

	/** A special widget to be used to signal that no object is being dragged. */
	protected static final Widget nothingBeingDragged = new EmptyWidget();

	/** if an entity is being dragged, this point reflects the anchor Point within the widget (useful for ensuring smooth drags). */
	protected java.awt.Point draggingAnchor = null;

	/** Every container must have a mouseManager to coordinate reactions that don't occur in a widget. */
	protected MouseManager mouseManager = new MouseManager (this);

	/** The hierarchy that remembers the background images to be redrawn. */
	protected RectangleHierarchy backgroundImage = null;

	/** Offscreen image. */
	protected Image offscreenImage = null;
	
	/** Graphics into offscreen image. */
	protected Graphics offScreenGraphics;
	
	/** Visitor to be used for this container. */
	protected RectangleHierarchyVisitor visitor;

	/** What was the point of the last drag. */
	protected java.awt.Point lastDrag;

	/** Has a widget told us that a drag is in progress? */
	protected boolean dragging;

	/** A Container is active once the game has started. Prior to that all events are disabled. */
	protected boolean active = false;

	/** 
	 * Container knows the name of the current user on which Container is being played.
	 * @since V2.2  
	 */
	protected String currentUser;

	/** Once a game is in progress, we turn on our security, and save old one. */
	SecurityManager oldSecurity = null;

	/**
	 * Creates an empty container with no widgets.
	 * <p>
	 */
	public Container() {
		super();

		
		// reuse code to start container afresh.
		removeAllWidgets();
	}

	/**
	 * Tell Container who the current user player is
	 * 
	 * @param user
	 * @since V2.2 to enable multiple solitaire
	 */
	public void setCurrentUser (String user) {
		currentUser = user;
	}

	/**
	 * Return the current user for this container.
	 * 
	 * @since V2.2 to enable multiple solitaire
	 */
	public String getCurrentUser() {
		return currentUser;
	}

	/**
	 * Adds the widget to the Container.
	 * <p>
	 * This method can only be called after the Container has been initialized. The Widget is added
	 * to the set of widgets, and a MouseManager is assigned, if one is not already in place.
	 * <p>
	 * The Boundary of the Widget is then added to the backgroundImage to maintain the background
	 * real estate that needs its own painting routines.
	 * <p>
	 * If the proposed widget intersects with an existing widget, a message is sent to stderr.
	 * <p>
	 * @return boolean true if the widget does not overlap with existing widgets
	 * @param w ks.common.view.Widget
	 */
	public boolean addWidget(Widget w) {
		Widget ex = intersectsWidget (w.getBounds());
		if (ex != null) {
			System.err.println ("Unable to add widget " + w.getName() + ". It intersects with " + ex.getName());
			return false;
		}

		// extend widget array if necessary
		if (numWidgets >= widgets.length) {
			growWidgets();
		}

		/** Have Widget's peer be this Container and create a MouseManager for it to use (if none in place). */
		w.setContainer (this);
		if (w.getMouseManager() == null)
			w.setMouseManager (new MouseManager (this));

		/** Update hierarchy */
		backgroundImage.insert (w.getBounds());

		/** Add to our list and return with success. */
		widgets[numWidgets++] = w;
		return true;
	}

	/**
	 * Move on to play the next hand for this game. 
	 * <p>
	 * Note: This is directly invoked by the user and signifies that they have given
	 * up for the given hand. If the user wins, call wonGame() instead.
	 */
	public void forceNextHand() {
		if (game == null) {
			System.err.println ("Container::forceNextHand() unexpectedly called with empty game.");
			return;
		}

		// pass request onto the game 
		game.nextHand();

		if (status != null) { status.incrementNumNewHands(); }
	}
	/**
	 * Returns the object being dragged. If no object is being dragged,
	 * the special <code>getNothingBeingDragged()<code> object is the value
	 * of this method.
	 * <p>
	 * @return ks.common.view.Widget
	 */
	public Widget getActiveDraggingObject() {
		return activeDraggingObject;
	}
	/**
	 * Return the background RectangleHierarchy representing the non-widget areas in container.
	 * <p>
	 * @return ks.common.view.RectangleHierarchy
	 */
	public RectangleHierarchy getBackgroundImage() {
		return backgroundImage;
	}
	/**
	 * Return the offset within the dragging Widget for where the mouse was initially clicked.
	 * <p>
	 * @return Point
	 */
	public Point getDraggingAnchor() {
		return draggingAnchor;
	}

	/**
	 * Gets the source widget from which a drag originated.
	 * <p>
	 * Note: dragSource must be manually set by the controller of the widget since there is
	 * no obvious way for the container to know the object for which the drag originates.
	 * <p>
	 * @return ks.common.view.Widget
	 * @since v1.6
	 */
	public Widget getDragSource() {
		return dragSource;
	}
	/**
	 * Returns the last Drag point, or null if no drag is in process.
	 * <p>
	 * This class variable is kept up-to-date by the various MouseManagers
	 * associated with each widget, as well as the MouseManagers with the
	 * Container itself that represents the background.
	 * <p>
	 * If this ever returns null, it means nothing is being dragged.
	 * <p>
	 * @return java.awt.Point
	 */
	public Point getLastDrag() {
		return lastDrag;
	}
	/**
	 * Returns a 'sentinel' object that represents the fact that nothing
	 * is being dragged. This avoids the use of using null object references
	 * <p>
	 * @return ks.common.view.Widget
	 */
	public static Widget getNothingBeingDragged() {
		return nothingBeingDragged;
	}

	/**
	 * Return visitor being used to draw background skin.
	 * <p>
	 * @return ks.common.view.RectangleHierarchyVisitor
	 */
	public RectangleHierarchyVisitor getVisitor() {
		return visitor;
	}
	/**
	 * Return the widget (or null if none exist) that includes given mouse location.
	 * <p>
	 * @return ks.common.view.Widget
	 * @param me java.awt.event.MouseEvent
	 */
	public Widget getWidget(java.awt.event.MouseEvent me) {
		if (me == null) return null;

		Point p = me.getPoint();

		for (int i = 0; i < numWidgets; i++) {
			if (widgets[i].inBounds(p)) {
				return widgets[i];
			}
		}

		return null;
	}
	/**
	 * Return the View Widgets in the container as an Enumeration.
	 * <p>
	 * @return java.util.Enumeration
	 */
	public java.util.Enumeration<Widget> getWidgets() {
		java.util.Vector<Widget> v = new java.util.Vector<Widget>();
		for (int i = 0 ; i < numWidgets; i++) {
			v.addElement (widgets[i]);
		}

		return v.elements();
	}
	/**
	 * Grow widgets array accordingly.
	 */
	protected void growWidgets() {
		Widget newWidgets[] = new Widget [widgets.length + potentialSize];
		System.arraycopy (widgets, 0, newWidgets, 0, widgets.length);
		widgets = newWidgets;
	}
	/**
	 * Check with plugin to see if the game has been won.
	 * @return boolean    whether the solitaire game has been won, according to the
	 *                    game's method <code>hasWon</code>.
	 */
	public boolean hasWon() {
		if (game == null) return false;

		boolean rc = game.hasWon();
		if (rc) {
			if (status != null) { status.incrementNumWins(); }
		}

		return rc;
	}
	/**
	 * Initialize using a Solitaire game that was already constructed elsewhere.
	 * <p>
	 * @since V2.1 this method is responsible for setting the card images in the container
	 * as determined by the given Solitaire plugin
	 * <p>
	 * After this method, the Container is active.
	 * 
	 * Since V2.2, we include a security Manager to enforce the
	 * proper behavior of these Solitaire plugins, just to be safe.
	 * 
	 * @param myGame   the plugin to be instantiated and activated.
	 */
	public boolean initialize(Solitaire myGame) {
		// Set hierarchy bounds. Can finally do this because we have been instantiated. Until
		// Container is visible, getBounds() will return non-valid results.
		backgroundImage = new RectangleHierarchy (getBounds());

		// if we have no visitor, instantiate a default BackgroundCopier.
		if (visitor == null) {
			setVisitor (new BackgroundCopier (getBackground())); 
		}

		// Must remove old widgets because we may be 'rejoined' container (see rejoin table command).
		removeAllWidgets();

		// Set the Solitaire context and store the game.
		myGame.setContainer (this);
		this.game = myGame;

		// Tell container of the CardImages that are to be used.
		// @since V2.1
		cards = myGame.getCardImages();
		if (cards == null) {
			return false;
		}

		// get old security context
		oldSecurity = System.getSecurityManager();

		// install security Manager on this thread. If we can't do so, because we already don't
		// have sufficient permissions, we are ok and no need to process this
		try {
			SecurityManager ksSecurity = new SecurityManager() {
				/**
				 * Throws a <code>SecurityException</code> if the requested
				 * access, specified by the given permission, is not permitted based
				 * on the security policy currently in effect.
				 * <p>
				 * This method calls <code>AccessController.checkPermission</code> 
				 * with the given permission.
				 *
				 * @param     perm   the requested permission.
				 * @exception SecurityException if access is not permitted based on
				 *		  the current security policy.
				 * @exception NullPointerException if the permission argument is
				 *            <code>null</code>.
				 * @since     1.2
				 */
				public void checkPermission(Permission perm) {
					if (perm instanceof AWTPermission) return;

					// allow all runtime things to go.
					if (perm instanceof RuntimePermission) return;

					// deny socket transmissions
					if (perm instanceof java.net.SocketPermission) return;

					try {
						// allow property read permission to go
						if (perm instanceof java.util.PropertyPermission) {
							java.util.PropertyPermission pp = (java.util.PropertyPermission) perm;
							if (!pp.getActions().equals ("read")) {
								throw new SecurityException ("preventing update on:" + pp.getName());
							}

							// OK!
							return;
						}

						if (perm instanceof java.io.FilePermission) {
							java.io.FilePermission fp = (java.io.FilePermission) perm;
							if (!fp.getActions().equals("read")) {
								// allow if within Temp directory
								String tmpDir = System.getProperty("java.io.tmpdir");
								if (fp.getName().startsWith(tmpDir)) {
									return;
								}

								// BAD update attempts!
								throw new SecurityException ("preventing update on:" + fp.getName());
							}

							// allow any READ (because we may need .gif or .jpg files).
							return;
						}
					} catch (SecurityException e) {
						// CAN'T ALLOW THIS TO CONTINUE!
						String name = "";
						if (game != null) name = game.getName();
						System.err.println ("Bad Behavior by KS Plugin (" + name + "):" + e.getMessage());

						leaveTable();
						resetSecurity(); // doable within handler?

						throw new SecurityException (e.getMessage());
					}
				}
			};
			if (applySecurity) {
				System.setSecurityManager(ksSecurity);
			} else {
				oldSecurity = null;
			}
		} catch (java.security.AccessControlException ace) {
			// just proceed.
			return false;
		}

		// ask Game to initialize itself, then force a refresh.
		game.resetHand();

		// we are now active.
		setActive (true);

		return true;
	}
	/**
	 * Returns the first Widget that intersects the given Rectangle (or null if none exist).
	 * <p>
	 * @return ks.common.view.Widget
	 * @param r java.awt.Rectangle
	 */
	public Widget intersectsWidget(Rectangle r) {
		if (r == null) return null;

		for (int i = 0; i < numWidgets; i++) {

			// If we have an intersection, return the widget.
			Rectangle wr = widgets[i].getBounds();
			if (wr.intersects (r)) {
				return widgets[i];
			}
		}

		// none intersect.
		return null;
	}
	/**
	 * Determine whether the Container is active and ready to process events.
	 * <p>
	 * Note: When a container is active, all mouse events are processed.
	 * <p>
	 * @return boolean
	 */
	public boolean isActive() {
		return active;
	}
	/**
	 * When a player wishes to leave the entire table. Note that the GUI has
	 * already confirmed this with the user so we must now carry out the action.
	 */
	public void leaveTable() {
		// release any resources we have.
		visitor.setVisible (false);   // check out interface for reasons.


		resetSecurity();
	}
	/**
	 * Go back to original security manager.
	 */
	void resetSecurity() {
		if (oldSecurity != null) {
			System.setSecurityManager(oldSecurity);
			oldSecurity = null;
		}
	}

	/**
	 * Force all model elements to be redrawn. Since this method is used by low-level
	 * java.awt.* code, we must adhere to the semantics of paint(). Thus, all elements
	 * are redrawn (including the background)
	 * <p>
	 * Paint is an imperative command that forces the Container to draw itself.
	 * <p>
	 * The dragging of objects is managed separately.
	 * <p>
	 * @param g java.awt.Graphics
	 */
	@Override
	public void paintComponent(java.awt.Graphics g) {
		// create once needed. All drawing goes into this.
		if (offscreenImage == null) {
			offscreenImage = this.createImage(getWidth(), getHeight());
			offScreenGraphics = offscreenImage.getGraphics();
		}
		
		// swing demands.
		super.paintComponent(g);

		// visitor for background already has graphics object.
		repaintBackground();

		// update those views that need refreshing.
		for (int i = 0 ; i < numWidgets; i++) {
			widgets[i].paint(offScreenGraphics);
		}

		// anything being dragged? deal with that now.
		Widget w = getActiveDraggingObject();
		if (w != null && w != Container.getNothingBeingDragged()) {
			w.paint(offScreenGraphics);
		}
		
		// copy in the image.
		g.drawImage(offscreenImage, 0, 0, this);
	}

	/**
	 * Expose the underlying off-screen graphics object to enable
	 * sophisticated background renders.
	 * <p>
	 * Note that to prevent these renderes from mistakenly disposing
	 * of this graphics context, we make sure to return a newly 
	 * created one and let the one invoking this method be responsible
	 * for disposing of it 
	 */
	public Graphics getOffscreenGraphics () {
		// create once needed. All drawing goes into this.
		if (offscreenImage == null) {
			offscreenImage = this.createImage(getWidth(), getHeight());
			if (offscreenImage == null) { return null; }
			
			offScreenGraphics = offscreenImage.getGraphics();
		}
		return offscreenImage.getGraphics();
	}
	
	/**
	 * Container must send this mouse event to the appropriate controller
	 * for processing.
	 * <p>
	 * If the container is inactive, no action is taken. The following mouse events can
	 * be global and do not apply to a specific Widget (not even handled yet):
	 * <p>
	 *    MouseExited<br>
	 *    MouseEntered<br>
	 * <p>
	 * @param me java.awt.event.MouseEvent
	 */
	public void processMouseEvent(java.awt.event.MouseEvent me) {

		// Perhaps allow mouseEntered/mouseExited to be dealt with here (especially with regards to
		// setting the cursor to a WAIT cursor if the game hasn't started...)

		// do nothing if we are inactive.
		if (isActive() == false) {
			return;
		}

		// Find the widget who should get event (or return if none found).
		Widget w = getWidget (me);
		if (w == null) {
			// even if no widget gets the mouse event, it may still be needed (for example, if released
			// over the container, or during drag events. 
			mouseManager.handleMouseEvent (me);
		} else {
			// get the controller
			MouseManager mm = w.getMouseManager();
			if (mm == null) return;

			mm.handleMouseEvent(me);
		}

		// Will this be invoked too many times? Limit to BIG THREE.
		switch(me.getID()) {
		case java.awt.event.MouseEvent.MOUSE_PRESSED:
		case java.awt.event.MouseEvent.MOUSE_RELEASED:
		case java.awt.event.MouseEvent.MOUSE_CLICKED:
			if (hasWon()) {
				wonGame();
			}
		}
	}

	/**
	 * Refresh widgets in container. This only refreshes the widgets that have been marked 'dirty'
	 */
	public void refreshWidgets() {
		// update those views that need refreshing.
		for (int i = 0 ; i < numWidgets; i++) {
			widgets[i].refresh();
		}
	}
	
	/**
	 * Releases the object being dragged by the container.
	 * <p>
	 * Note: this will also reset the dragSource and lastDrag to null.
	 */
	public void releaseDraggingObject() {
		// release dragging Object.
		setActiveDraggingObject (getNothingBeingDragged(), null);

		dragging = false;
		lastDrag = null;

		// reset our sourceWidget (if it was ever set in the first place).
		dragSource = null;
	}
	/**
	 * Remove all widgets from this container to start fresh.
	 */
	public void removeAllWidgets() {

		/** Initialize this container to have no widgets. */
		widgets = new Widget [potentialSize];
		numWidgets = 0;

		/** At the outset, nothing is being dragged. */
		releaseDraggingObject();	
	}

	/**
	 * Refresh the background screen which is a hierarchy of rectangles to be filled using background image.
	 */
	public void repaintBackground() {

		// Traverse the tree and have visitor update everything.
		if (visitor != null && backgroundImage != null) {
			backgroundImage.accept (offScreenGraphics, visitor);
		}
	}

	/**
	 * Reset existing game to redeal cards exactly as it was started.
	 */
	public void resetHand() {
		if (game == null) {
			System.err.println ("Container::resetHand() unexpectedly called with empty game.");
			return;
		}

		// pass request onto the game 
		game.resetHand();
	}

	/**
	 * Activate the container (or deactivate it) to be able to process events.
	 * @param newActive boolean
	 */
	public void setActive(boolean newActive) {
		active = newActive;
	}
	
	/**
	 * Specifies the Widget being dragged. Note: this is called once. Thereafter, for each
	 * drag event, MouseManagers for the Widgets update the lastDrag Point in the container.
	 * <p>
	 * We also take as input the MouseEvent associated with this dragging object; this enables
	 * us to calculate the anchor.
	 * <p>
	 * Take some care if MouseEvent is null.
	 * @param newActiveDraggingObject ks.common.view.Widget
	 * @param me MouseEvent
	 */
	public void setActiveDraggingObject(Widget newActiveDraggingObject, java.awt.event.MouseEvent me) {
		activeDraggingObject = newActiveDraggingObject;

		if (me == null) {
			setDraggingAnchor (null);
			return;
		}

		Point p = new Point (me.getX() - newActiveDraggingObject.getX(), me.getY() - newActiveDraggingObject.getY());
		setDraggingAnchor (p);
	}
	/**
	 * Record DraggingAnchor, the offset within the dragged widget of where the 
	 * mouse was first clicked. Using this point will ensure that dragging redraws
	 * are smooth.
	 * <p>
	 * @param newDraggingAnchor Point
	 */
	protected void setDraggingAnchor(Point newDraggingAnchor) {
		draggingAnchor = newDraggingAnchor;
	}
	/**
	 * Sets the source widget from which a drag originated.
	 * <p>
	 * Note: dragSource must be manually set by the controller of the widget since there is
	 * no obvious way for the container to know the object for which the drag originates.
	 * <p>
	 * Passing in null simply means that the drag is released.
	 * <p>
	 * @param newDragSource ks.common.view.Widget
	 * @since v1.6
	 */
	public void setDragSource(Widget newDragSource) {
		if (newDragSource == null)
			releaseDraggingObject();
		else		
			dragSource = newDragSource;
	}


	/**
	 * Tell container of last drag point (or null if ending a drag).
	 * @param p java.awt.Point
	 */
	public void setLastDrag(Point p) {
		lastDrag = p;
		dragging = (p != null);
	}
	/**
	 * Alters the way in which this Container reacts to MouseEvents.
	 * @param ma java.awt.event.MouseAdapter
	 */
	public void setMouseAdapter(java.awt.event.MouseAdapter ma) {
		if (mouseManager != null)
			mouseManager.setMouseAdapter (ma);
	}
	/**
	 * Alters the way in which this Container reacts to MouseMotion events.
	 * @param ma java.awt.event.MouseMotionAdapter
	 */
	public void setMouseMotionAdapter(java.awt.event.MouseMotionAdapter ma) {
		if (mouseManager != null) {
			mouseManager.setMouseMotionAdapter (ma);
		}
	}

	/** Determine who must know of events. */
	public void setUpdateStatus(IUpdateStatus ius) {
		status = ius;
	}


	/**
	 * Alters the way in which this Container reacts to UndoRequested
	 * @param ua UndoAdapter
	 */
	public void setUndoAdapter(UndoAdapter ua) {
		if (mouseManager != null) {
			mouseManager.setUndoAdapter (ua);
		}
	}
	/**
	 * Sets the entity responsible for updating background skin of the container
	 * @param v     the RectangleHierarchyVisitor to be used.
	 */
	public void setVisitor(RectangleHierarchyVisitor v) {
		if (v == null)
			throw new IllegalArgumentException ("Container::setImageDisplayer() received null argument.");

		visitor = v;
	}
	/**
	 * The game has been won. Move on to the next game.
	 */
	public void wonGame() {

		if (game != null)
			game.nextHand();

		resetSecurity();
	}

	/**
	 * Return the Model elements associated with the container's game as an Enumeration.
	 * <p>
	 * This method is not used during play. It is being provided to anticipate the need of
	 * a sophisticated Beta-testing GUI application that can inspect and manipulate the
	 * state of a solitaire plug-in.
	 * @return java.util.Enumeration
	 */
	public java.util.Enumeration<Element> getModelElements() {
		if (game == null) {
			return new java.util.Vector<Element>().elements();
		}

		return game.getModelElements();
	}

	/** Need to access internal game to be able to find score, etc... */
	public Solitaire getGame() {
		return game;
	}

	/**
	 * Return the card images associated with this container.
	 * <p>
	 * @return CardImages   the cardImages used by the plugin active within this container.
	 */
	public CardImages getCardImages () {
		return cards;
	}

	// When score is updated, worth knowing about...
	public void updateScore(int sc) {
		if (status != null) { status.updateScore(sc); }		
	}

	/**
	 * Attempts to free all resources visible "DOWN" from the container.
	 * <p>
	 * This should properly release all graphic objects down from the solitaire game
	 * to all widgets used in the game.
	 */
	public void dispose() {
		cards.dispose();
		
		// request game to release any of its specialized resources
		game.dispose();
		
		for (int i = 0; i < numWidgets; i++) {
			if (widgets[i] != null) {
				widgets[i].dispose();
				widgets[i] = null;
			}
		}
		
		numWidgets = 0;
		
		if (offScreenGraphics != null) { offScreenGraphics.dispose(); }
		if (offscreenImage != null) { offscreenImage.flush(); }
		
		offScreenGraphics = null;
		offscreenImage = null;
		cards = null;
	}

	/**
	 * Convenient method to force a repaint of all widgets.
	 * <p>
	 * Useful for times when the entire set of widgets needs to be drawn, or when the 
	 * skin changes.
	 */
	public void repaintAll() {
		for (int i = 0; i < numWidgets; i++) {
			widgets[i].setDirty(true);
		}
		
		refreshWidgets();
	}
}
