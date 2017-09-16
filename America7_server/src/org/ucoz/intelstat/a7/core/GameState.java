package org.ucoz.intelstat.a7.core;

/**
 * A {@code GameState} instance indicates the state of
 * an America 7 game.
 * @author InteLStaT
 *
 */
public enum GameState {
	/**
	 * Indicates that the game hasn't started yet.
	 */
	PREGAME,
	/**
	 * Indicates that the game has started and is
	 * being played.
	 */
	INGAME,
	BRINGBACK,
	/**
	 * Indicates that the game has ended with a winner.
	 */
	POSTGAME
	
}
