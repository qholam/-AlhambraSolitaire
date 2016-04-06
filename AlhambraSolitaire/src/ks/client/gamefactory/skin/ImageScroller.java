package ks.client.gamefactory.skin;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;

import ks.common.view.Container;
import ks.common.view.RectangleHierarchy;
import ks.common.view.RectangleHierarchyVisitor;


/**
 * Class for presenting a scrolling image on the back of a solitaire game.
 * <p>
 *  This could be made more generic (to allow scrolling up/down/left/right/random)...
 * <p>
 * Creation date: (10/11/01 5:00:49 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class ImageScroller implements RectangleHierarchyVisitor, Runnable {

	/** Image being scrolled. */
	protected Image myImage;

	/** Delay between scrolling. */
	protected int delay = 500;

	/** Container over which we are operating; we need to have dragging. */	
	protected Container container;

	/** The distance between the left edge and position of leading scrolled edge. */
	protected int edge = 0;

	/** Distance to scroll horizontally with each clock tick. */
	protected int scrollDistance = 2;

	/** The thread to oversee the scrolling. */
	protected java.lang.Thread thread;

	/** Assume we are originally visible. */
	protected boolean visible = true;
	
	/**
	 * ImageScroller constructor.
	 */
	public ImageScroller(Image img, Container container) {
		super();

		if (img == null) {
			throw new IllegalArgumentException ("ImageScroller::ImageScroller() received null Image.");
		}

		if (container== null) {
			throw new IllegalArgumentException ("ImageScroller::ImageScroller() received null Container.");
		}

		myImage = img;
		this.container = container;

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
	 * Return distance to scroll image.
	 * @return int
	 */
	public int getScrollDistance() {
		return scrollDistance;
	}
	
	/**
	 * Determine whether image is visible.
	 * @return boolean
	 */
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Thread will operate by pausing n milliseconds and then checking if container is in 
	 * a drag. Once it is verified that we can scroll, we do so, and the entire container
	 * background is redrawn.
	 */
	public void run() {

		while (visible) {
			// Sleep for pre-determined delay
			try {
				Thread.sleep(delay);
			} catch (InterruptedException ie) {
				// nothing real that we can do here...
			}

			// update everything, including backgrounds of widgets. Then force update.
			container.repaintAll();
			container.repaint();

			// prepare for next time: move range, always keeping within 0..container.width
			edge += scrollDistance;
			
			if (edge > container.getWidth() || edge < 0) {
				scrollDistance = -scrollDistance;
				edge += 2 * scrollDistance;
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
	 * Set the distance to scroll each time.
	 * @param newScrollDistance int
	 */
	public void setScrollDistance(int newScrollDistance) {
		if (newScrollDistance < 0) {
			throw new IllegalArgumentException ("ImageScroller::setScrollDistance() received scroll distance < 0.");
		}

		scrollDistance = newScrollDistance;
	}
	
	/**
	 * Setting to invisible will cause this thread to stop.
	 * 
	 * @param newVisible boolean
	 */
	@Override
	public void setVisible(boolean newVisible) {
		visible = newVisible;
	}
	
	/**
	 * Copy the appropriate image onto Graphics g at (x,y,width,height) taking into
	 * account the scrolling image.
	 * <p>
	 * @param node    root RectangleHierarchy node being visited
	 */
	public void visit(Graphics g, RectangleHierarchy node) {
		// reuse regular visit with no clipping region.
		visit (g, node, null);
	}
	
	/**
	 * Copy the appropriate image onto Graphics g at (x,y,width,height) taking into
	 * account the scrolling image.
	 * <p>
	 * Note: only Leaf nodes are drawn.
	 * <p>
	 * Note: In V2.1 fixed defect in missing clip argument to recursive calls!
	 * <p>
	 * Clip to the given Rectangle clip area. [clip is unaffected by this invocation.]
	 * @param node    root RectangleHierarchy node being visited
	 * @param clip    Rectangle within whichd drawing is clipped.
	 */
	public void visit(Graphics g, RectangleHierarchy node, java.awt.Rectangle clip) {

		// If there are any children, we must move on because drawing only
		// occurs on leaf nodes.
		RectangleHierarchy rh[] = node.getChildren();
		if (rh != null) {
			for (int i = 0; i < rh.length; i++) {
				if (rh[i] != null) {
					rh[i].accept (g, this, clip);
				}
			}
			return;
		}

		// to get here, we must be a leaf node
		Rectangle bounds = node.getBounds();
		if (clip != null) {
			bounds = bounds.intersection (clip);
		}

		if (bounds.isEmpty()) return;

		// just fill in.
		visit_ (g, bounds);
	}
	
	/**
	 * Update background area within widget.
	 * <p>
	 * @param g           Graphics object into which to draw
	 * @param background  rectangle containing background of some widget to draw.
	 */
	public void visit(Graphics g, Rectangle bounds) {
		
		/**
		 * We must determine two rectangular regions, left and right. For those
		 * parts of the image on the left, we move from the tail-end (right) of the src image.
		 * for those images on the right, we move from the leading-end (left) of the src image.
		 * Either could be empty/non-intersecting. Note: it is impossible that both left&right are empty.
		 */
		Rectangle contRect = container.getBounds();	 
		Rectangle left = bounds.intersection (new Rectangle (0, 0, edge, contRect.height));
		Rectangle right = bounds.intersection (new Rectangle (edge, 0, contRect.width - edge, contRect.height));

		// note: left & right refer to the destination and we must translate from the source into it.
		
		// Calculate Dest Left Side by translating edge units to the left.
		if (! right.isEmpty()) {
			if (g == null) {
				setVisible(false);  // shut down scrolling thread.
				return;
			}

			// images to the right of edge are drawn by horizontal offset 'edge' from src.
			// NOTE: The Source will always start flush at the left of the original image (x = 0)
			g.drawImage(myImage, 0, 0, right.width, right.height,
					bounds.x-edge-contRect.x, bounds.y, bounds.x-edge+right.width-contRect.x, bounds.y+right.height, container);
		}

		// Src Left side
		if (! left.isEmpty()) {
			if (g == null) {
				setVisible (false);  // shut down scrolling thread
				return;
			}

			// images to the left of edge are drawn by extracting 'edge' pixels from right of original image.
			// left will always be drawn from rightmost edge of original image.
			g.drawImage(myImage, 0, 0, left.width, left.height,
					contRect.width - (edge - left.x), left.y, contRect.width - (edge - left.x) + left.width, left.y + left.height, container);
		}
	}
	
	
	/**
	 * Update background area within widget.
	 * <p>
	 * @param g         Graphics object into which to draw
	 * @param bounds    rectangle containing background of some widget to draw.
	 */
	private void visit_(Graphics g, Rectangle bounds) {
		/**
		 * We must determine two rectangular regions, left and right. For those
		 * parts of the image on the left, we move from the tail-end (right) of the src image.
		 * for those images on the right, we move from the leading-end (left) of the src image.
		 * Either could be empty/non-intersecting. Note: it is impossible that both left&right are empty.
		 */
		Rectangle contRect = container.getBounds();	 
		Rectangle left = bounds.intersection (new Rectangle (contRect.x, contRect.y, edge, contRect.height));
		Rectangle right = bounds.intersection (new Rectangle (contRect.x + edge, contRect.y, contRect.width - edge, contRect.height));

		int originalWidth = contRect.width;
		int originalX = contRect.x;
		int originalY = contRect.y;

		// note: left & right refer to the destination and we must translate from the source into it.

		// Calculate Dest Left Side by translating edge units to the left.
		if (! right.isEmpty()) {
			if (g == null) {
				setVisible(false);  // shut down scrolling thread.
				return;
			}

			// images to the right of edge are drawn by horizontal offset 'edge' from src.
			// NOTE: The Source will always start flush at the left of the original image (x = 0)
			g.drawImage(myImage, right.x, right.y, right.x + right.width, right.y + right.height,
					right.x-originalX-edge, right.y - originalY, right.x-originalX-edge + right.width, right.y - originalY + right.height, container);
		}

		// Src Left side
		if (! left.isEmpty()) {
			if (g == null) {
				setVisible (false);  // shut down scrolling thread
				return;
			}

			// images to the left of edge are drawn by extracting 'edge' pixels from right of original image.
			// left will always be drawn from rightmost edge of original image.
			g.drawImage(myImage, left.x, left.y, left.x + left.width, left.y + left.height,
					originalWidth - (originalX + edge - left.x), left.y - originalY, originalWidth - (originalX + edge - left.x) + left.width, left.y - originalY + left.height, container);
		}
	}
}
