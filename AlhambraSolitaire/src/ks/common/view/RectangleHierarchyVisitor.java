package ks.common.view;

import java.awt.Graphics;

/**
 * Employ Visitor Pattern to enable specific invocations at each
 * Leaf in the RectangleHierarchy. 
 * <p>
 * Note: Internal nodes (with children) are not involved in any drawing
 * <p>
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public interface RectangleHierarchyVisitor {

	/**
	 * If a RectangleHierarchyVisitor performs actions of an asynchronous nature,
	 * it needs to be told to stop. The simplest way to do this, is to assume
	 * someone may want to setVisible (false) once done.<p>
	 * <p>
	 * This is called by external entity who knows when the visitor will
	 * no longer be needed.
	 */
	void setVisible(boolean visible);
	
	/**
	 * Visit the given rectangle in the background.
	 * <p>
	 * Background is redrawn using container's "skin".
	 * 
	 * @param g     graphics object into which to draw.
	 * @param rect  rectangle in g to be updated
	 */
	void visit(Graphics g, java.awt.Rectangle rect);
	
	/**
	 * Visit a node in the hierarchy.
	 * 
	 * @param g     graphics object into which to draw.
	 * @param node  node within RectangleHierarchy.
	 */
	void visit(Graphics g, RectangleHierarchy node);
	
	/**
	 * Visit a node in the hierarchy.
	 * <p>
	 * All drawing must be clipped to given clip rectangle. [clip is unaffected 
	 * by this invocation.]
	 * <p>
	 * @param g     graphics object into which to draw.
	 * @param node   node within RectangleHierarchy.
	 * @param clip   Rectangle to which all drawing is to be clipped.
	 */
	void visit(Graphics g, RectangleHierarchy node, java.awt.Rectangle clip);
}
