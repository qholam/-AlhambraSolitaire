package ks.common.view;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Color;

/**
 * Manages the responsibility of filling rectangles that need refreshing
 * into a specific graphics context object using a particular color.
 * <p>
 * Default Skin for a container; simply copies a known color everywhere.
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class BackgroundCopier implements RectangleHierarchyVisitor {

	/** My color to show. */
	protected java.awt.Color myColor;
	
	/** Old color prior to drawing. */
	Color oldColor; 

	/**
	 * BackgroundCopier constructor comment.
	 * 
	 * @param rectangle    bounds for this graphics so we can normalize clips
	 *  
	 * @param g Graphics
	 * @param c java.awt.Color
	 */
	public BackgroundCopier(java.awt.Color c) {
		if (c == null) throw new IllegalArgumentException ("BackgroundCopier::BackgroundCopier(Grahpics, Color) received null Color parameter.");
		myColor = c;
	}
	
	/**
	 * Visitor's chance to release resources once done.
	 * <p>
	 * @param visible boolean
	 */
	public void setVisible(boolean visible) {
		// nothing to do.
	}
	
	/**
	 * Copy the appropriate image as based by this node's value. 
	 * <p>
	 * @param g	       graphics object into which to draw.
	 * @param node    node within RectangleHierarchy being visited.
	 */
	public void visit (Graphics g, RectangleHierarchy node) {
		if (g == null) { 
			return; 
		}
		
		oldColor = g.getColor();
		g.setColor(myColor);
		
		// reuse more general method.
		visit (g, node, null);
		
		g.setColor(oldColor);
	}
	
	/**
	 * Copy the appropriate image as based by this node's value. Use clip
	 * as a truncating clipping region.
	 * <p>
	 * clip is unaffected by this method.
	 * <p>
	 * In V2.1 fixed defect with child nodes not being pass clip region.
	 * 
	 * @param g	       graphics object into which to draw.
	 * @param node     node within RectangleHierarchy being visited.
	 * @param clip     Rectangle to which all drawing is to be clipped.
	 */
	public void visit (Graphics g, RectangleHierarchy node, Rectangle clip) {
		if (g == null) { 
			return; 
		}
		
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

		// to get here, we must be a leaf node. Calculate bounds
		Rectangle bounds = node.getBounds();
		if (clip != null) {
			bounds = bounds.intersection (clip);
		}

		// return if no overlap
		if (bounds.isEmpty()) {
			return;
		}

		// perform operation (fillRect)
		g.fillRect (bounds.x, bounds.y, bounds.width, bounds.height);
	}

	@Override
	public void visit(Graphics g, Rectangle bounds) {
		g.setColor (myColor);
		// was bounds.x, bounds.y instead of 0,0
		g.fillRect (0, 0, bounds.width, bounds.height);
	}
	
}
