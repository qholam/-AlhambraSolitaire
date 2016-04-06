package ks.common.view;

/**
 * Notify status of CardImagesLoader.
 * Creation date: (1/3/02 11:15:54 PM)
 * @author George T. Heineman (heineman@cs.wpi.edu)
 */
public interface ICardImagesStatus {
	
/**
 * End the loadingProcess
 */
void end();
/**
 * Load image of given name.
 */
void load (String name);
}
