package ks.common.view;

import java.awt.Graphics;
import java.awt.Rectangle;

/**
 * Manages a Tree of rectangles that represent the non-widget elements
 * on the container.
 * <p>
 * Assuming an outer boundary rectangle (x,y,width,height), this class keeps track
 * of 'negative space' assuming that each inserted rectangle represents 'positive 
 * space'.
 * <p>
 * For example, given the root (x,y,width,height) and the rectangle (u,v,l,m) is 
 * inserted, this node has four children R0, R1, R2, R3.
 * <p>
 *   R0 = (x,y,width,v-y)                ;; top horizontal slice<br>
 *   R1 = (x,v,u-1,m)                    ;; rectangle slice to the left of the<br>
 *                                       ;; inserted rectangle<br>
 *   R2 = (u+m,v,width-m-(u-x))          ;; rectangle slice to the right of the <br>
 *                                       ;; inserted rectangle<br>
 *   R3 = (x,v+m,width,height-m-(v-y))   ;; bottom horizontal slice.
 * <p>
 * insert (Rectangle r) is a recursive method, enabling partialling intersecting 
 * rectangles to be modeled.
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */

public class RectangleHierarchy {
	/** The bounds for this node */
	protected java.awt.Rectangle bounds = null;

	/** Each Rectangle node can have up to four children. */
	protected RectangleHierarchy children[] = null;
/**
 * RectangleHierarchy constructor.
 */
public RectangleHierarchy(Rectangle initialBounds) {
	bounds = new Rectangle (initialBounds);
}
/**
 * Accept visitor and use double dispatching to pass ourselves in as a parameter
 * to the visitor.
 * @param visitor ks.common.view.RectangleHierarchyVisitor
 */
public void accept(Graphics g, RectangleHierarchyVisitor visitor) {
	visitor.visit (g, this);
}
/**
 * Accept visitor and use double dispatching to pass ourselves in as a parameter
 * to the visitor.
 * <p>
 * Clip all drawing to this clip Rect. [clip is unaffected by this invocation.]
 * <p>
 * @param visitor ks.common.view.RectangleHierarchyVisitor
 * @param clip Rectangle
 */
public void accept(Graphics g, RectangleHierarchyVisitor visitor, Rectangle clip) {
	visitor.visit (g, this, clip);
}
/**
 * Return the R0 child (or null if it is empty).
 * @param tree java.awt.Rectangle
 * @param r java.awt.Rectangle
 * @return RectangleHierarchy
 */
protected RectangleHierarchy calculateChildR0(Rectangle tree, Rectangle r) {
	// no child R0 if the two rectangles have the same top (same y values)
	if (r.y == tree.y) return null;
	
	Rectangle r0 = new Rectangle (tree.x, tree.y, tree.width, r.y - tree.y);
	return new RectangleHierarchy(r0);
}
/**
 * Return the R1 child (or null if it is empty).
 * @param tree java.awt.Rectangle
 * @param r java.awt.Rectangle
 * @return RectangleHierarchy
 */
protected RectangleHierarchy calculateChildR1(Rectangle tree, Rectangle r) {
	/** no child R1 if the rectangles share a left border. */
	if (tree.x == r.x) return null;
	
	Rectangle r1 = new Rectangle (tree.x, r.y, r.x - tree.x, r.height);
	return new RectangleHierarchy(r1);
}
/**
 * Return the R2 child (or null if it is empty).
 * @param tree java.awt.Rectangle
 * @param r java.awt.Rectangle
 * @return RectangleHierarchy
 */
protected RectangleHierarchy calculateChildR2(Rectangle tree, Rectangle r) {
	/** If rectangles share a right border, no R2 child */
	if (tree.x + tree.width == r.x + r.width) return null;
	
	Rectangle r2 = new Rectangle (r.x + r.width, r.y, tree.x + tree.width - r.width - r.x, r.height);
	return new RectangleHierarchy(r2);
}
/**
 * Return the R3 child (or null if it is empty).
 * @param tree java.awt.Rectangle
 * @param r java.awt.Rectangle
 * @return RectangleHierarchy
 */
protected RectangleHierarchy calculateChildR3(Rectangle tree, Rectangle r) {
	/** if rectangles share a bottom edge then no R3 child. */
	if (tree.y + tree.height == r.y + r.height) return null;
	
	Rectangle r3 = new Rectangle (tree.x, r.y + r.height, tree.width, tree.y + tree.height - r.y - r.height);
	return new RectangleHierarchy(r3);
}
/**
 * Return bounds of this node.
 * <p>
 * The rectangle returned is the actual rectangle. DO NOT MODIFY THIS OBJECT.
 * @return Rectangle
 */
public Rectangle getBounds() {
	return bounds;
}
/**
 * Return all children of the given node in array (or null if there are no children).
 * @return ks.common.view.RectangleHierarchy[]
 */
public RectangleHierarchy[] getChildren() {
	if (children == null) return null;

	RectangleHierarchy rh[] =  { children[0], children[1], children[2], children[3] };
	return rh;
}
/**
 * Insert the given rectangle into the hierarchy.
 * <p>
 * The rectangle represents the bounds of a widget.  We essentially need to model the other
 * portions that remain as rectangles.  Consider the widget as positive space; the RectangleHierarchy
 * models the negative space.
 * @return boolean
 * @param r Rectangle
 */
public boolean insert(Rectangle r) {
	// no intersection: nothing to insert
	if (! r.intersects (bounds)) return false;
	
	Rectangle rint = bounds.intersection (r);

	// Simple case: If we do not have any children yet
	if (children == null) {
		children = new RectangleHierarchy[4];
		
		children[0] = calculateChildR0 (bounds, rint);
		children[1] = calculateChildR1 (bounds, rint);
		children[2] = calculateChildR2 (bounds, rint);
		children[3] = calculateChildR3 (bounds, rint);
	} else {
		// insert into all children, recursively.
		for (int i = 0; i < 4; i++) {
			if (children[i] != null) children[i].insert (rint);
		}
	}
	
	return true;
}
/**
 * Convert to String the hierarchy root Rectangle.
 * @return java.lang.String
 */
public String toString() {
	return bounds.toString();
}
}
