package org.ucoz.intelstat.a7.core;

import java.util.List;

import org.ucoz.intelstat.a7.core.Game.Player;
import org.ucoz.intelstat.gc.GCard;

/**
 * {@code Controller}s are through which game players are controlled. A
 * controller has to propose a card when asked for one through the abstract
 * methods.
 * 
 * @author InteLStaT
 *
 */
public interface Controller {

	/**
	 * Called when it's the associated player's turn, with no special
	 * restrictions. This method either has to return a card, or return null if
	 * the player wants to draw a card from the deck.
	 * 
	 * @param handView
	 *            the list of cards in the controlled player's hand
	 * @param player
	 *            the controlled player
	 * @param askedFor
	 *            the suit the a previous player has asked for, {@code null} if
	 *            not asked
	 */
	// TODO: change doc and functionality if this method can be and will be used for ACE and UNDER placement.
	GCard proposeCard(List<GCard> handView, Game game, Player player);

	/**
	 * Called when a previous player asked for a suit. This method has to return
	 * a card with the specified suit or draw by returning null.
	 * 
	 * @param handView
	 *            the list of cards in the controlled player's hand
	 * @param player
	 *            the controlled player
	 * @param askedFor
	 *            the suit the a previous player has asked for, {@code null} if
	 *            not asked
	 * @param suit
	 *            the suit being asked for
	 */
	GCard askedForSuit(List<GCard> handView, Game game, Player player, GCard.Suit suit);
}
