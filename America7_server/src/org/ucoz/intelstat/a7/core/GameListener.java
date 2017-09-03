package org.ucoz.intelstat.a7.core;

import org.ucoz.intelstat.a7.core.Game.Player;

public interface GameListener {

	// TODO ADD CALLBACKS AS NEEDED, APPROPRIATELY
	void playerJoined(GamePassiveEvent e);

	void playerLeft(GamePassiveEvent e);

	void currentPlayerChanged(GamePassiveEvent e);

	void gameStateChanged(GamePassiveEvent e);

	/**
	 * Called when a player's card count changes (draws are plays cards).
	 */
	void playerCardCountChanged(GameQuantitativeEvent e, Player player);

	void roundChanged(GameQuantitativeEvent e);

	/**
	 * Called when the stock becomes empty, thus gets refilled.
	 * @param game
	 */
	void stockRefilled(Game game);

	/**
	 * When someone draws one or more cards, this happens. This isn't called
	 * when cards are dealt to players at the start of the game.
	 */
	void stockSizeDecreased(GameQuantitativeEvent e);

	/**
	 * When someone puts cards into the pile, this happens.
	 */
	void pileSizeIncreased(GameQuantitativeEvent e);
}
