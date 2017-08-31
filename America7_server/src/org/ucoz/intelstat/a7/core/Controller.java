package org.ucoz.intelstat.a7.core;

import java.util.List;

import org.ucoz.intelstat.a7.core.Game.Player;
import org.ucoz.intelstat.gc.GCard;

public interface Controller {

	/**
	 * Callback method, called when it's the associated player's 
	 * turn. This method either has to return a card, or return 
	 * null if the player wants to draw a card from the deck.
	 * 
	 * @param topCard the top card in the pile, on top of which 
	 * the next card will be placed
	 * @param handView the list of cards in the controlled player's hand
	 * @param askedFor the suit the a previous player has asked for, {@code null} if not asked
	 * @param player the controlled player
	 */
	GCard proposeCard(GCard topCard, List<GCard> handView, Game game, Player player);
}
