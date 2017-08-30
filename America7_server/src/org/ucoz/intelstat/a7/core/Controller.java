package org.ucoz.intelstat.a7.core;

import org.ucoz.intelstat.a7.core.Game.Player;
import org.ucoz.intelstat.gc.GCard;
import org.ucoz.intelstat.gc.GHand;

public interface Controller {

	/**
	 * Callback method, called when it's the associated player's 
	 * turn. This method either has to return a card, or return 
	 * null if the player wants to draw a card from the deck.
	 * If the returned card 
	 * @param topCard the top card in the pile, on top of which 
	 * the next card will be placed
	 */
	GCard proposeCard(GCard topCard, GHand hand, Game game, Player player);
	default void init(Game.Player player) {}
}
