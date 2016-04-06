package ks.common.model;

/**
 * Each Model has a View that is dependent upon that model. When
 * the model changes, the view is alerted.
 * <p>
 * Creation date: (10/6/01 11:34:23 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public interface ElementListener {

	/** Alert listener that specific model element has changed. */
	void modelChanged (Element elt);
}
