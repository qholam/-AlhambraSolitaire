package ks.common.view;

import java.awt.*;

import ks.common.controller.MouseManager;
import ks.common.controller.UndoAdapter;
import ks.common.model.Element;
import ks.common.model.ElementListener;

/**
 * For all view.* entities, this is the superclass that defines important concepts.
 * <p>
 * The widget is drawn at location (x,y) extending width pixels to the right and 
 * height pixels down.
 * <p>
 * Every widget has an associated CardImages object to retrieve cards and the card 
 * reverse image. When a widget is initially constructed, it is marked as 'dirty'.
 * <p>
 * The widget class provides an offscreenImage to each subclass should it be desired.
 * {@link #offscreenImage} is stored by this class to enable rapid cleanup of image
 * resources once no longer necessary. Each subclass can allocate and release resources
 * in its own {@link #dispose()} method, but be sure to invoke super.dispose() to properly
 * clean up.
 * <p>
 * Because the card images for widgets can be disposed independently of the drawing 
 * AWT thread, there may be times when a {@link NullPointerException} is thrown
 * within the redraw methods. There is no easy way to eliminate this situation
 * except for synchronizing on the cards within {@link #redraw()}, but this
 * extra burden would only impair performance. As it stands, the only 
 * time this should happen is during testing when numerous windows are
 * created and disposed of during the JUnit testing.
 * 
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public abstract class Widget implements ElementListener {

	/** X location of this widget in the graphical object. */
	protected int x;

	/** Y location of this widget in the graphical object. */
	protected int y;

	/** Width of this widget. */
	protected int width;

	/** Height of this widget. */
	protected int height;

	/** The image that is displayed in the paint(Graphics g) method. */	
	protected java.awt.Image image = null;

	/** The container. Also the peer to ask when requesting an Image. */
	protected Container container;

	/** Card images to use for this widget (set during setContainer). */
	// TODO: make private
	protected CardImages cards;

	/** 
	 * Image maintained by this widget to perform any drawings. Accessed by subclasses ONLY.
	 * Placed here so it can be finalized. 
	 */
	protected java.awt.Image offscreenImage = null;
	
	/** Will be set by subclasses. */
	protected String name;

	/** Every widget can have a mouseManager to coordinate reactions (set once added to a container). */
	protected MouseManager mouseManager = null;

	/** The model element for this widget. */
	protected Element modelElement;

	/** 
	 * Records whether the model for this widget has changed since last time it was drawn.
	 * @since v1.5.1 Widgets are created already 'dirty'
	 */
	protected boolean dirty = true;

	/**
	 * Widget constructor for subclasses only.
	 */
	protected Widget() {

	}

	/**
	 * Default constructor for Widgets. Used as super() within subclasses' constructors.
	 * Since V1.6.8 check for null argument.
	 * <p>
	 * @param me ks.common.model.Element
	 */
	public Widget(Element me) {
		if (me == null) throw new IllegalArgumentException ("Widget::Constructor received null Element object.");

		/** Store model element. */
		modelElement = me;

		/** Set name of widget to the name of the model element. */
		setName (me.getName());

		/** Set ourselves up as the default listener for state changes. */
		me.setListener (this);
	}

	/**
	 * Return the bounds of the Widget as a newly instantiated rectangle.
	 * @return java.awt.Rectangle
	 */
	public java.awt.Rectangle getBounds() {
		return new java.awt.Rectangle (x, y, width, height);
	}

	/**
	 * Return the container for this Widget.
	 * @return java.awt.Component
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Return the height of the widget.
	 * @return int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Return the image of this Widget (used for drawing the Widget on screen).
	 * @return java.awt.Image
	 */
	public java.awt.Image getImage() {
		return image;
	}

	/**
	 * Return the Model Element associated with this Widget.
	 * @return ks.common.model.Element
	 */
	public Element getModelElement() {
		return modelElement;
	}
	/**
	 * Return the MouseManager associated with this Widget.
	 * <p>
	 * At some point in the future, this manager will return boolean to signal whether
	 * it processed the event or not. This will allow us to produce a layered approach
	 * to controllers. Develop MouseMotionController, MouseController in controller
	 * package just for this purpose.
	 * <p>
	 * @return ks.common.view.MouseManager
	 */
	public MouseManager getMouseManager() {
		return mouseManager;
	}

	/**
	 * Return the name of this widget.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the width of the widget.
	 * @return int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Return the x coordinate for the Widget's location.
	 * @return int
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Return the y coordinate for the Widget's location.
	 * @return int
	 */
	public int getY() {
		return y;
	}

	/**
	 * If (x,y) point is within this widget, return true, else false.
	 * <p>
	 * @return boolean 
	 * @param p java.awt.Point
	 */
	public boolean inBounds(java.awt.Point p) {
		if (p.x < x) return false;
		if (p.x > x + width) return false;
		if (p.y < y) return false;
		if (p.y > y + height) return false;

		return true;
	}

	/**
	 * Determine whether this widget needs to be recalculated.
	 * @return boolean
	 */
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Respond to underlying changes in this widget's model.
	 * <p>
	 * See ElementListener.
	 * <p>
	 * @param e ks.common.model.Element
	 */
	public void modelChanged(Element e) {
		// we are now dirty.
		setDirty (true);
	}


	/**
	 * Each widget must contain the logic to visually represent itself into the given 
	 * <code>Graphics</code> context. Paint is an imperative command that forces the 
	 * widget to draw itself.
	 * <p>
	 * If the widget is dirty, or if it has never been drawn before, then it is redrawn
	 * before being painted to the screen, and is marked as clean.
	 * <p>
	 * @param g java.awt.Graphics
	 */
	public void paint(java.awt.Graphics g) {
		// If image is null, we haven't drawn it for the first time. Do so now by
		// asking subclasses to redraw.
		if (getImage() == null) {
			redraw();
		}

		// if we are dirty, must redraw and clean.
		if (isDirty()) {
			redraw();
			setDirty (false);
		}

		java.awt.Image img = getImage();

		// Because of race observed race conditions, the image may yet be null! We don't want
		// to exit in error. We mask the problem, and hope it doesn't occur that often.
		if (img != null) {
			// If each Widget's (w,h) parameters are set as they should be, then
			// we can use the full method to transfer only what is needed.
			// Use container as the ImageObserver.
			if (g != null) {
				g.drawImage (img, x, y, x+width,y+height, 0, 0, width, height, java.awt.Color.white, container);
			}
		}
	}

	/**
	 * Redraws the view from the model. Each subclass must contain the specific logic.
	 */
	public abstract void redraw();

	/**
	 * Refresh is an optimized command that only redraws the widget if it is dirty.
	 */
	public void refresh() {
		if (isDirty()) {
			// paint handles clearing the dirty bit and redrawing as necessary.
			Graphics ng = container.getGraphics();
			paint(ng);
			ng.dispose();
		}
	}

	/**
	 * Each Widget presents the graphical representation of an entity element. It also
	 * provides basic mechanisms for extracting Cards or other structures of Cards from
	 * this underlying element. There is no standard means for doing this extraction,
	 * but this method can be used to undo such an extraction. For example, if on
	 * mousePress, a widget were to extract the topmost card from its column and make it
	 * available for dragging as a <code>CardView</code>, then this
	 * <code>returnWidget()</code> method would return the card back onto the underlying
	 * Column.
	 * <p>
	 * This method as provided returns <code>true</code> in all cases; simply override it
	 * in your Widget subclass as you deem appropriate.
	 * <p>
	 * This method will only work if the extraction methods, shown above, actually call
	 * the setDragSource() on the container when the initial extraction takes place.
	 * @return boolean
	 * @param w ks.common.view.Widget
	 * @since V1.6
	 */
	public boolean returnWidget(Widget w) {
		return false;
	}

	/**
	 * Set the graphical bounds for this widget within the Container.
	 * @param x int
	 * @param y int
	 * @param width int
	 * @param height int
	 */
	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Set the graphical bounds for this widget within the Container using a Rectangle.
	 * @param r java.awt.Rectangle
	 */
	public void setBounds(java.awt.Rectangle r) {
		this.x = r.x;
		this.y = r.y;
		this.width = r.width;
		this.height = r.height;
	}

	/**
	 * All Widget objects need to know the Container within which they will be made visible. 
	 * This enables the widget to create an offscreen image whenever it needs to, by
	 * asking the container to do it for itself!
	 * <p>
	 * This method sets the <code>cards</code> field so that the widget knows the cards
	 * to be used for drawing.
	 * <p>
	 * @since V2.1, the Container has determined its card images before widgets are added; here
	 *    we just get the card images from the Container.
	 * <p>
	 * @param container Container
	 */
	public void setContainer (Container container) {
		this.container = container;

		// Now that we know the container, get the Card Images to be used
		// default type is found in Configuration
		this.cards = container.getCardImages();
	}

	/**
	 * Determines whether this widget's screen representation needs to be recalculated.
	 * @param newDirty boolean
	 */
	public void setDirty(boolean newDirty) {
		dirty = newDirty;
	}

	/**
	 * Set the height of this widget.
	 * @param newHeight int
	 */
	public void setHeight(int newHeight) {
		height = newHeight;
	}

	/**
	 * To update the image as shown by this widget, the subclass will create an off-screen
	 * image and call this method.  But, someone will ultimately have to call refresh once done.
	 * <p>
	 * This sets the dirty state of the Widget.
	 * @param newImage java.awt.Image
	 */
	public void setImage(java.awt.Image newImage) {
		image = newImage;

		dirty = true;
	}

	/**
	 * Alter the way in which this Widget reacts to MouseEvents.
	 * <p>
	 * Add our own MouseController so we can return boolean values.
	 * @param ma java.awt.event.MouseAdapter
	 */
	public void setMouseAdapter(java.awt.event.MouseAdapter ma) {
		if (mouseManager == null) {
			throw new IllegalArgumentException ("Widget::setMouseAdapter() unable to invoke until Widget is added to a container.");
		}

		mouseManager.setMouseAdapter (ma);
	}

	/**
	 * Set the aggregate entity that consolidates MouseAdapter and MouseMotionAdapter. 
	 * <p>
	 * Add our own Controllers so we can return boolean values.
	 * @param mm MouseManager
	 */
	public void setMouseManager (MouseManager mm) {
		if (mm == null) {
			throw new IllegalArgumentException ("Widget::setMouseManager() invoked with null object.");
		}

		// keep track of this.
		mouseManager = mm;
	}

	/**
	 * Alter the way in which this Widget's MouseManager reacts to MouseMotion events.
	 * <p>
	 * Add our own MouseMotionController
	 * @param ma java.awt.event.MouseMotionAdapter
	 */
	public void setMouseMotionAdapter(java.awt.event.MouseMotionAdapter ma) {
		if (mouseManager == null) {
			throw new IllegalArgumentException ("Widget::setMouseMotionAdapter() unable to invoke until Widget is added to a container.");
		}

		mouseManager.setMouseMotionAdapter (ma);
	}

	/**
	 * Set the name for this Widget.
	 * @param newName java.lang.String
	 */
	public void setName(String newName) {
		if (newName == null) throw new IllegalArgumentException ("Widget::setName(String) received null argument.");

		name = newName;
	}

	/**
	 * Alter the way in which this Widget reacts to UndoRequested events.
	 * <p>
	 * Change this to be UndoController to be consistent with everything else in the universe!
	 * @param ua java.awt.event.UndoAdapter
	 */
	public void setUndoAdapter(UndoAdapter ua) {
		if (mouseManager == null) {
			throw new IllegalArgumentException ("Widget::setUndoAdapter() unable to invoke until Widget is added to a container.");
		}

		mouseManager.setUndoAdapter (ua);
	}

	/**
	 * Set the X position of the widget.
	 * @param newX int
	 */
	public void setX(int newX) {
		x = newX;
	}

	/**
	 * Set the (x,y) position of the widget.
	 * @param x int
	 * @param y int
	 */
	public void setXY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the Y position of the widget.
	 * @param newY int
	 */
	public void setY(int newY) {
		y = newY;
	}

	/**
	 * Return String representation of the given View Widget.
	 * <p>
	 * If this method is called on the EmptyWidget, then "[EMPTYWIDGET]" is returned.
	 * @return String
	 */
	public String toString() {
		return "[" + name + " viewing " + modelElement.toString() + "]";	 
	}

	/**
	 * Clear away offscreen images.
	 * <p>
	 * Note that CardImages is referenced but we do not take responsibility for clearing it. 
	 */
	public void dispose(){
		if (offscreenImage != null) { offscreenImage.flush(); }
		if (image != null) { image.flush(); }
		offscreenImage = null;
		image = null;
		
		// Releases separately. 
		cards = null;
	}

}
