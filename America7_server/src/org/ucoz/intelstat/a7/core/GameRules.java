package org.ucoz.intelstat.a7.core;

import org.ucoz.intelstat.a7.core.Game.Player;
import org.ucoz.intelstat.gc.GCard;

public class GameRules {

	private GameRules() {}
	
	public static boolean isValidMove(GCard prevCard, GCard nextCard, Game game, Player player) {
		if(nextCard.getRank() == GCard.SEVEN) {
			return true;
		}
		if(prevCard.getSuit() == nextCard.getSuit() || prevCard.getRank() == nextCard.getRank()) {
			return true;
		}
		return false;
	}
	
	
}
