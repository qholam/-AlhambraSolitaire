package ks.client.gamefactory.skin;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

import ks.common.view.Container;
import ks.common.view.RectangleHierarchy;
import ks.common.view.RectangleHierarchyVisitor;


/**
 * Class for presenting a ball bouncing around, and off the various widgets,
 * using the same background color.
 * <p>
 * While an object is being dragged by the container, no bouncing will occur
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class BouncingBalls implements RectangleHierarchyVisitor, Runnable {

	class Ball {
		/** Current position. */
		Rectangle  ballPosition;

		/** New position (temporary). */
		Rectangle newPosition;

		/** Color of ball. */
		java.awt.Color ballColor = java.awt.Color.black;

		/** X Velocity. */
		int dx;

		/** Y Velocity. */
		int dy;
	}

	/** Balls in motion. */
	protected Vector<Ball> balls = new Vector<Ball>();

	/** Delay between bounces. */
	protected int delay = 25;

	/** Should there just be a single ball, or one per RectangleHierarchy child? */
	protected boolean onePerChild;

	/** Graphics context in which to draw. */
	protected Graphics myGraphics;

	/** Container over which we are operating; we need to have dragging. */	
	protected Container container;

	/** Bounds for container -- used to detect off the screen movement. */
	protected Rectangle containerBounds;

	/** size of ball radius. */
	protected int radius = 7;

	/** The thread to oversee the scrolling. */
	protected java.lang.Thread thread;

	/** Assume we are originally visible. */
	protected boolean visible = true;
	/**
	 * ImageScroller constructor with a single Bouncing Ball.
	 */
	public BouncingBalls(Container container) {
		this (container, false);
	}
	/**
	 * ImageScroller constructor.
	 */
	public BouncingBalls(Container container, boolean onePerChild) {
		super();

		if (container== null) {
			throw new IllegalArgumentException ("ImageScroller::ImageScroller() received null Container.");
		}

		this.container = container;
		this.myGraphics = container.getOffscreenGraphics();
		this.containerBounds = container.getBounds();
		this.onePerChild = onePerChild;

		// get thread going
		thread = new Thread (this);
		thread.start();
	}
	/**
	 * Return delay between image scrolling.
	 * @return int
	 */
	public int getDelay() {
		return delay;
	}
	/**
	 * Return size of Ball.
	 * Creation date: (1/5/02 12:17:41 AM)
	 * @return int
	 */
	public int getRadius() {
		return radius;
	}
	/**
	 * Determine whether image is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return visible;
	}
	/**
	 * For each ball in play, update its new position.
	 */
	public void offsetBallPosition () {
		int max = balls.size();
		if (onePerChild == false) max = 1;
		for (int i = 0; i < max; i++) {
			Ball ball = balls.elementAt (i);

			// find new ball position.
			ball.newPosition = new Rectangle (ball.ballPosition);

			// we will offset by newdx, newdy BUT FIRST, check some conditions.
			int newdx = ball.dx;
			int newdy = ball.dy;

			// move x first
			ball.newPosition.translate (ball.dx, 0);

			// if off the screen, or intersect widget, ricochet back
			if ((!containerBounds.union (ball.newPosition).equals (containerBounds)) ||
					(container.intersectsWidget (ball.newPosition) != null)) {
				ball.dx = -ball.dx;   // ricochet
				newdx = 0;
				ball.newPosition = new Rectangle (ball.ballPosition); // stay where we were...
			} 

			// now move y
			ball.newPosition.translate (0, ball.dy);		

			// if off the screen, or intersect widget, ricochet back
			if ((!containerBounds.union (ball.newPosition).equals (containerBounds)) ||
					(container.intersectsWidget (ball.newPosition) != null)) {
				ball.dy = -ball.dy;   // ricochet
				newdy = 0;  // no movement here.
			}

			// now properly move (newdx, newdy)
			ball.newPosition = new Rectangle (ball.ballPosition);
			ball.newPosition.translate (newdx, newdy);
		}
	}
	/**
	 * Returns a number from (-width/2) .. 0 .. (width/2)
	 */

	protected int randomOffset (int width) {
		double d = Math.random();
		int rn = (int) (width * d);  // 0 .. 
		return rn - (width/2);
	}
	/**
	 * Thread will operate by pausing n milliseconds and then checking if container is in 
	 * a drag. Once it is verified that we can scroll, we do so, and the entire container
	 * background is redrawn.
	 */
	public void run() {

		// ballPosition can only be calculated once container's backgroundImage is ready.
		// unfortunately for us, this is a race condition, so we simply loop back to
		// beginning if we haven't been able to set our ballposition...


		while (visible) {
			// Sleep for pre-determined delay
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ie) {
				// nothing real that we can do here...
			}

			// still not able to calculate position? Try until we can...
			if (balls.size() == 0) {
				setInitialPosition(container.getBackgroundImage());
				continue;
			}

			// update to new location
			offsetBallPosition();

			// force container to come visit us so we can redraw ball image.		
			container.repaint();

			// and get ready for next one...
			for (int i = 0; i < balls.size(); i++) {
				Ball ball = (Ball) balls.elementAt (i);
				ball.ballPosition = ball.newPosition;
			}
		}
	}
	/**
	 * Set the time delay between image scrolling.
	 * @param newDelay int
	 */
	public void setDelay(int newDelay) {
		if (newDelay < 0) {
			throw new IllegalArgumentException ("ImageScroller::setDelay() received delay time < 0.");
		}

		delay = newDelay;
	}
	/**
	 * Find a non-widget location where the ball can go.
	 */
	private void setInitialPosition(RectangleHierarchy node) {

		if (node == null) return;  // Race condition! too early			// Ready!

		// Traverse and add a Ball for each child node.
		RectangleHierarchy rh[] = node.getChildren();
		if (rh == null) {
			// At this point, child points to a rectangle which is not being used. Find Center
			int x = node.getBounds().x + (node.getBounds().width/2);
			int y = node.getBounds().y + (node.getBounds().height/2);
			Ball b = new Ball();
			b.ballPosition = new Rectangle (x - radius, y - radius, 2*radius, 2*radius);
			b.dx = randomOffset (10);
			if (b.dx == 0) b.dx = 1;
			b.dy = randomOffset (10);
			if (b.dy == 0) b.dy = 1;
			balls.addElement (b);
			return;			
		}

		// pick one of the children at random and loop again.
		for (int i = 0 ; i < rh.length; i++) {
			if (rh[i] != null) {
				setInitialPosition (rh[i]);
			}
		}
	}
	/**
	 * Set size of ball.
	 * Creation date: (1/5/02 12:17:41 AM)
	 * @param newRadius int
	 */
	public void setRadius(int newRadius) {
		radius = newRadius;
	}
	/**
	 * Setting to invisible will cause this thread to stop.
	 * @param newVisible boolean
	 */
	public void setVisible(boolean newVisible) {
		visible = newVisible;
	}
	/**
	 * Copy the appropriate image onto Graphics g at (x,y,width,height) taking into
	 * account the scrolling image.
	 * <p>
	 * @param node RectangleHierarchy root
	 */
	public void visit(Graphics g, RectangleHierarchy node) {
		// reuse regular visit with no clipping region.
		visit (g, node, null);
	}
	
	/**
	 * Update background area within widget.
	 * <p>
	 * Use existing container background.
	 * @param g           Graphics object into which to draw
	 * @param background  rectangle containing background of some widget to draw.
	 */
	public void visit(Graphics g, Rectangle background) {
		g.setColor (container.getBackground());
		g.fillRect(background.x, background.y, background.width, background.height);
	}
	
	/**
	 * Copy the appropriate image onto Graphics g at (x,y,width,height) taking into
	 * account the scrolling image.
	 * <p>
	 * Note: only Leaf nodes are drawn.
	 * <p>
	 * Clip to the given Rectangle clip area. [clip is unaffected by this invocation.]
	 * @param node    root RectangleHierarchy to start visitation
	 * @param clip    Rectangle within which drawing is clipped.
	 */
	public void visit(Graphics g, RectangleHierarchy node, java.awt.Rectangle clip) {

		// we can't do anything until we have a valid ballPosition!!
		if (balls.size() == 0) return;

		if (myGraphics == null) {
			this.myGraphics = container.getGraphics();
			if (myGraphics == null) {
				return; // nothing to do!
			}
		}

		// If there are any children, we must move on because drawing only
		// occurs on leaf nodes.
		RectangleHierarchy rh[] = node.getChildren();
		if (rh != null) {
			for (int i = 0; i < rh.length; i++) {
				if (rh[i] != null) {
					rh[i].accept (g, this);
				}
			}
			return;
		}

		// to get here, we must be a leaf node
		Rectangle bounds = node.getBounds();
		if (clip != null) {
			bounds = bounds.intersection (clip);   // a new rectangle is returned...
		}

		if (bounds.isEmpty()) return;

		// if the container's last drag is null, we update ball positions: otherwise, 
		// we copy background, including last known ball location (s).

		// in all cases, must update node's background.
		myGraphics.setColor (container.getBackground());
		myGraphics.fillRect (bounds.x, bounds.y, bounds.width, bounds.height);

		int max = balls.size();
		if (onePerChild == false) max = 1;
		for (int i = 0; i < max; i++) {
			Ball ball = (Ball) balls.elementAt(i);
			if (!ball.ballPosition.intersects (bounds)) continue;  // nothing to do here.

			if (container.getLastDrag() == null) {
				// erase old one
				myGraphics.setColor (container.getBackground());
				myGraphics.fillOval (ball.ballPosition.x, ball.ballPosition.y, ball.ballPosition.width, ball.ballPosition.height);

				myGraphics.setColor (ball.ballColor);
				if (ball.newPosition == null) {
					ball.newPosition = ball.ballPosition;
				}
				myGraphics.fillOval (ball.newPosition.x, ball.newPosition.y, ball.newPosition.width, ball.newPosition.height);	
			} else {
				// Simply draw images for all ball positions...
				myGraphics.setColor (ball.ballColor);
				if (ball.newPosition == null) {
					ball.newPosition = ball.ballPosition;
				}
				if (ball.newPosition != null)
					myGraphics.fillOval (ball.newPosition.x, ball.newPosition.y, ball.newPosition.width, ball.newPosition.height);	

			}
		}

	}
}
