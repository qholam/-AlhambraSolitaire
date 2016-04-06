package ks.client.gamefactory.skin;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.awt.Image;

import ks.common.view.RectangleHierarchy;
import ks.common.view.RectangleHierarchyVisitor;

/**
 * Visitor class to fills rectangles with parts of an image.
 * Creation date: (10/9/01 7:22:11 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class ImageCopier implements RectangleHierarchyVisitor {

	/** The image from which to extract the rectangles. */
	protected Image myImage;

	/** The image observer visible on the screen. */
	protected java.awt.image.ImageObserver myObserver = null;

	/** The graphics context in which to draw. */
	//protected java.awt.Graphics myGraphics;

	/**
	 * ImageCopier by default uses this color as the fillRect color when transfering a filled Rectangle.
	 * 
	 * @param i              image being copied
	 * @param observer       observer receiving notices 
	 * @param g              Graphics device
	 */
	public ImageCopier(Image i, ImageObserver observer) {
		super();

		myImage = i;
		myObserver = observer;
	}
	/**
	 * Visitor's chance to free up resources at end of being displayed
	 * @param visible boolean
	 */
	public void setVisible(boolean visible) {
		// nothing to do.
	}
	
	/**
	 * Copy the appropriate image onto Graphics g at (x,y,width,height).
	 * @param  node
	 */
	public void visit(Graphics g, RectangleHierarchy node) {
		// reuse regular visit with no clipping region.
		visit (g, node, null);
	}
	/**
	 * Copy the appropriate image onto Graphics g at (x,y,width,height).
	 * <p>
	 * Note: only Leaf nodes are drawn.
	 * <p>
	 * In 2.1 Fixed defect where CHILDREN where being passed NULL clip region.
	 * <p>
	 * Clip to the given Rectangle clip area. [clip is unaffected by this invocation.]
	 * @param node
	 * @param clip
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

		g.drawImage(myImage, bounds.x, bounds.y, 
				bounds.x+bounds.width, bounds.y+bounds.height,
				bounds.x, bounds.y, 
				bounds.x+bounds.width, bounds.y+bounds.height,myObserver);
	}
	
	/**
	 * Update background area within widget.
	 * <p>
	 * Note that widget is drawn relative to (0,0) so we have to do some computations
	 * to properly line things up.
	 * 
	 * @param g           Graphics object into which to draw
	 * @param background  rectangle containing background of some widget to draw.
	 */
	public void visit(Graphics g, Rectangle bounds) {
		g.drawImage(myImage, 0, 0, 
				bounds.width, bounds.height,
				bounds.x, bounds.y, 
				bounds.x+bounds.width, bounds.y+bounds.height,myObserver);
	}
}
