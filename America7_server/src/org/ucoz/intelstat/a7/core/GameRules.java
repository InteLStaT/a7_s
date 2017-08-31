package org.ucoz.intelstat.a7.core;

import org.ucoz.intelstat.gc.GCard;

/**
 * A game of America 7 is played counterclockwise.
 * 
 * @see Game
 * @author InteLStaT
 *
 */
// TODO: add proper javadoc
// FIXME: if you're looking for a bug, it's likely here
public class GameRules {

	private GameRules() {
	}

	/**
	 * This method checks if playing the card {@code nextCard} is valid by the
	 * rules of the game, not accounting SEVEN ranked cards.
	 * 
	 * @param prevCard
	 *            the previous card (that is, the top card)
	 * @param nextCard
	 *            the card which is checked against {@code prevCard}
	 * @param isStreak
	 *            whether prevCard's rank (ACE OR UNDER) is in streak
	 */
	public static boolean isValidMove(GCard prevCard, GCard nextCard, boolean isStreak) {
		/*
		 * If the game is currently in (either an ACE or UNDER) streak, only
		 * these ranks can be placed. The streak's rank is the previous card's
		 * rank. NOTE: by the following code, and rank can streak, not just ACE
		 * or UNDER.
		 */
		if (isStreak) {
			if (nextCard.getRank() == prevCard.getRank()) {
				return true;
			}
			return false;
		}
		if (nextCard.getRank() == GCard.SEVEN) {
			return true;
		}
		if (prevCard.getRank() == nextCard.getRank() || prevCard.getSuit() == nextCard.getSuit()) {
			return true;
		}

		return false;
	}

	public static boolean isValidAskedCard(GCard card, GCard.Suit suit) {
		if (card.getSuit() == suit) {
			return true;
		}
		if (card.getRank() == GCard.Rank.SEVEN) {
			return true;
		}
		return false;
	}

}
