package org.ucoz.intelstat.a7.core;

import org.ucoz.intelstat.a7.core.Game.Player;

public interface GameListener {

	// TODO ADD CALLBACKS AS NEEDED, APPROPRIATELY
	void playerJoined(GamePassiveEvent e);
	void playerLeft(GamePassiveEvent e);
	void currentPlayerChanged(GamePassiveEvent e);
	void gameStateChanged(GamePassiveEvent e);
	void playerCardCountChanged(GameQuantitativeEvent e, Player player);
	void roundChanged(GameQuantitativeEvent e);
	void deckRefilled(Game game);
	void deckSizeDecreased(GameQuantitativeEvent e);
	void pileSizeIncreased(GameQuantitativeEvent e);
}
