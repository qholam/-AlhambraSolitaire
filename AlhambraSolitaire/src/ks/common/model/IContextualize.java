/*
 * Part of processing for remote moves.
 */
package ks.common.model;

import ks.common.games.Solitaire;

/**
 * When a Move is generated on one client and shipped to another
 * client for processing, it must be 'recontextualized' so as
 * to properly hook up with the ModelElements as found on the 
 * remote client.
 */
public interface IContextualize {
	/**
	 * It is the responsibility of the Move object to locate
	 * its context given the game argument.
	 * 
	 * @param theGame
	 * @exception if unable to properly determine new context
	 */
	void recontextualize (Solitaire theGame) throws IllegalArgumentException;
}
