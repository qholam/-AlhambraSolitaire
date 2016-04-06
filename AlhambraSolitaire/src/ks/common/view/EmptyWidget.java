package ks.common.view;

/**
 * To simplify some algorithms, an EmptyWidget object is a sentinel
 * that can be used to identify no Widget at all (rather than use null).
 * <p>
 * An empty widget has no model, has no name, and is no listener.
 * Creation date: (10/4/01 5:32:23 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class EmptyWidget extends Widget {
	
	/**
	 * EmptyWidget constructor comment.
	 */
	public EmptyWidget() {
		super();

		setName ("EmptyWidget");
	}
	
	/**
	 * Empty Widget has nothing to draw!
	 */
	public void redraw() {}
}
