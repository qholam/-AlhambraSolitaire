package ks.common.model;

/**
 * Root of hierarchy of all Model objects. 
 * <p>
 * Each Model object has a name, and may have a single <code>ElementListener</code>
 * that is contacted whenever the Model element changes state.
 * <p>
 * Each Element is associated with one view only to simplify the processing
 * of model state changes.
 * <p>
 * Creation date: (10/2/01 10:24:08 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public class Element {

	/** Name of the Model element. */
	protected String name = null;

	/** The Entity (if any exists) interested in model changes. */
	protected ElementListener listener = null;
	
	/**
	 * Element constructor.
	 */
	public Element() {
		super();
	}
	/**
	 * Returns the name of this model element.
	 * <p>
	 * Creation date: (10/2/01 10:24:30 PM)
	 * @return String
	 */
	public String getName() {
		return name;
	}
	/**
	 * Each subclass of Element knows when it has changed state, and this method
	 * is invoked to alert a possible listener.
	 * <p>
	 * Creation date: (10/6/01 11:39:47 PM)
	 */
	protected void hasChanged() {
		if (listener != null)
			listener.modelChanged (this);
	}
	/**
	 * Sets the listener for model changing events.
	 * <p>
	 * Creation date: (10/6/01 11:38:41 PM)
	 * @param newListener ks.common.model.ElementListener
	 */
	public void setListener(ElementListener newListener) {
		listener = newListener;
	}

	/**
	 * Returns listener for the model.
	 * @return ElementListener object
	 * @since V2.2
	 */
	public ElementListener getListener () {
		return listener;
	}

	/**
	 * Sets the name of this model element.
	 * <p>
	 * Creation date: (10/2/01 10:24:30 PM)
	 * @param newName java.lang.String
	 */
	public void setName(java.lang.String newName) {
		if (newName	== null) throw new IllegalArgumentException ("Element::setName() received null name parameter.");

		name = newName;
	}
	
	/**
	 * Return string representation of this model element.
	 * <p>
	 * String is of the form: 
	 * <p>
	 * [className]:name
	 * <p>
	 * Creation date: (10/28/01 9:57:17 PM)
	 * @return java.lang.String
	 */
	public String toString() {
		return this.getClass().getName() + ":" + getName();
	}
}
