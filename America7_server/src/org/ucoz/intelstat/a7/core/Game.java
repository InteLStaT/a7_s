package org.ucoz.intelstat.a7.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.ucoz.intelstat.gc.GCard;
import org.ucoz.intelstat.gc.GDeck;
import org.ucoz.intelstat.gc.GHand;

/**
 * Represents an America 7 card game. Players can join the game by creating an
 * instance of the nested class {@code Player}. A game can have anywhere from 2
 * to 6 players, although 5- or 6-player games are considered non-standard.
 * <h1>Constitution of a game</h1>
 * <ul>
 * <li>a deck of German playing cards as defined {@link GCard here}
 * <li>2 to 6 players arranged in a circle
 * <li>the top card with a pile of previous top cards
 * </ul>
 * For the rules of the game, see {@link GameRules}.
 * 
 * @author InteLStaT
 *
 */
// TODO: THROW EXCEPTIONS IN APPROPRIATE GETTERS IF THE GAME HASN'T STARTED
// FIXME: Now what's wrong with this class? Too many exceptions.
// FIXME: inconsistent usage of public/private getters and private fields
public class Game {

	private static final int AWAIT_GED_TIME = 100;

	public static final int INITIAL_HAND_SIZE = 5;
	public static final int MIN_PLAYER_COUNT = 2;
	public static final int MAX_PLAYER_COUNT = 6;
	private static final IllegalGameStateException PREGAME_EXCEPTION = new IllegalGameStateException(
			"The game hasn't started yet", GameState.PREGAME);
	private static final IllegalGameStateException INGAME_EXCEPTION = new IllegalGameStateException(
			"The game has already started", GameState.INGAME);
	private static final IllegalGameStateException POSTGAME_EXCEPTION = new IllegalGameStateException(
			"The game has already ended", GameState.POSTGAME);

	private Player[] players;
	private int playerCount;

	/**
	 * The face-down deck of cards from which cards are dealt to players and are
	 * drawn from by them.
	 */
	private GDeck stock;
	/**
	 * The pile in which cards are put into by players. The top card is the one
	 * which was put the last into the pile.
	 * 
	 * @see #topCard
	 */
	private List<GCard> pile;
	/**
	 * The top card in the pile.
	 * 
	 * @see #pile
	 */
	private GCard topCard;

	/**
	 * Current state of the game.
	 * 
	 * @see GameState
	 */
	private GameState gameState = GameState.PREGAME;
	/**
	 * A round consists of all players' turns. Therefore it is incremented
	 * whenever all players have made a move.
	 */
	private int round;
	/**
	 * The current player being asked to make a move.
	 */
	private Player curPlayer;
	/**
	 * Index of the current player in the getPlayers() array.
	 * 
	 * @see Game#curPlayer
	 */
	private int curPlayerIdx;
	/**
	 * Winner of the game.
	 */
	private Player winner;
	/**
	 * Listeners for GED.
	 */
	private List<GameListener> listeners = new ArrayList<>(2);
	/**
	 * Dictates the flow of the game. Wow what a description.
	 */
	private GameLoop gameLoop;
	private Thread gameLoopThread;
	/**
	 * Dispatches events generated by the game.
	 */
	private static GameEventDispatcher ged;

	// TODO: let the server manage GEDs. Too many games will likely cause
	// delays.
	static {
		ged = new GameEventDispatcher();
		ged.start();
	}

	public Game() {
		players = new Player[6];
		stock = GDeck.shuffledDeck();
		pile = new ArrayList<>(32);
		gameLoopThread = new Thread(gameLoop = new GameLoop());
	}

	public synchronized void addGameListener(GameListener l) {
		listeners.add(l);
	}

	public synchronized void removeGameListener(GameListener l) {
		listeners.remove(l);
	}

	public GameState getGameState() {
		return gameState;
	}

	public Player getStartingPlayer() throws IllegalGameStateException {
		_checkPregameException();
		return getPlayers()[0];
	}

	public Player getCurrentPlayer() throws IllegalGameStateException {
		_checkPregameException();
		return curPlayer;
	}

	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Returns the deck's size from which cards are drawn by players. A value of
	 * 32 indicates the game hasn't started yet.
	 */
	// TODO: check pregame exception or not? if yes, remove doc
	public int getDrawDeckSize() {
		return stock.getSize();
	}

	public GCard getTopCard() throws IllegalGameStateException {
		_checkPregameException();
		return topCard;
	}

	public int getRound() throws IllegalGameStateException {
		_checkPregameException();
		return round;
	}

	public Player getWinner() throws IllegalGameStateException {
		if (getGameState() != GameState.POSTGAME) {
			throw getGameState() == GameState.PREGAME ? PREGAME_EXCEPTION : POSTGAME_EXCEPTION;
		}
		return winner;
	}

	public void start() throws IllegalGameStateException {
		// make sure game hasn't started, enough players are there and they are
		// all ready
		if (getGameState() == GameState.PREGAME) {
			if (playerCount >= MIN_PLAYER_COUNT && playerCount <= MAX_PLAYER_COUNT) {
				for (Player player : getPlayers()) {
					if (!player.isReady()) {
						throw new IllegalGameStateException("At least one of the players is not ready", gameState);
					}
				}
				// simply start the loop?
				gameLoopThread.start();
			} else {
				throw new IllegalGameStateException("Player count isn't between allowed bounds");
			}
		} else {
			throw getGameState() == GameState.INGAME ? INGAME_EXCEPTION : POSTGAME_EXCEPTION;
		}
	}

	private void setGameState(GameState state) {
		gameState = state;
	}

	private void _sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			System.err.println("SLEEP METHOD INTERRUPTED");
		}
	}

	private void _awaitGED() {
		_sleep(AWAIT_GED_TIME);
	}

	private void _checkPregameException() throws IllegalGameStateException {
		if (getGameState() == GameState.PREGAME) {
			throw PREGAME_EXCEPTION;
		}
	}

	public class Player {

		private final String name;
		private boolean ready = false;

		private int index;

		private Controller ctrl;

		private GHand hand;

		public Player(String name, Controller ctrl) {
			if (gameState == GameState.PREGAME) {
				if (playerCount <= MAX_PLAYER_COUNT) {
					players[playerCount] = this;
					playerCount++;
				} else {
					throw new IllegalStateException("Player count exceeded " + MAX_PLAYER_COUNT + ". Can't add more");
				}
			} else {
				throw getGameState() == GameState.INGAME ? INGAME_EXCEPTION : POSTGAME_EXCEPTION;
			}
			hand = new GHand();
			this.name = name;
			this.ctrl = ctrl;
			/* EVENT */gameLoop.playerJoined(new GamePassiveEvent(Game.this, this, round, gameState, gameState));
		}

		// TODO: do something with the ready up system
		public void readyUp() {
			ready = true;
		}

		public boolean isReady() {
			return ready;
		}

		public Game getGame() {
			return Game.this;
		}

		public String getName() {
			return name;
		}

		public int getCardCount() {
			return hand.getSize();
		}

		/**
		 * Returns this player's index in the Game.getPlayers() array. The index
		 * of players in the PREGAME state is unknown.
		 * 
		 * @throws IllegalGameStateException
		 *             if the game hasn't started yet
		 */
		public int getIndex() {
			if (getGameState() != GameState.PREGAME) {
				return index;
			} else {
				throw PREGAME_EXCEPTION;
			}
		}

		private void setIndex(int idx) {
			index = idx;
		}

		private GCard requestCard() {
			return ctrl.proposeCard(hand.readonlyView(), getGame(), this);
		}

		private GCard requestCardWithSuit(GCard.Suit suit) {
			return ctrl.proposeCardWithSuit(hand.readonlyView(), getGame(), this, suit);
		}

		/**
		 * Asks the controller for a suit because it put a SEVEN card before. In
		 * case it returns null, a default value is used.
		 * 
		 * @return the suit asked for by the controller
		 */
		private GCard.Suit requestSuit() {
			GCard.Suit suit = ctrl.askForSuit(hand.readonlyView(), getGame(), this);
			suit = suit != null ? suit : GCard.Suit.values()[0];
			return suit;
		}

		private GHand getHand() {
			return hand;
		}

	}

	// I very much like public interface methods. Not like anybody's gonna get
	// access to it.
	// I will disable reflection anyways.
	// TODO: disable reflection.
	private class GameLoop implements Runnable, GameListener {

		private int underStreak = 0;
		private boolean isUnderStreak = false;
		private boolean isAceStreak = false;
		private boolean isAskingSuit;
		private boolean isValidMove = false;
		private GCard proposedCard = null;
		private GCard.Suit askedSuit = null;

		/***************
		 * GAME LOOP *
		 ***************/
		// TODO: still mess, but more mess. please make less mess.
		public void run() {

			// RANDOMIZE PLAYERS
			Random rgen = new Random();
			for (int i = 0; i < playerCount; i++) {
				int rand = rgen.nextInt(playerCount);
				Player temp = players[i];
				players[i] = players[rand];
				players[rand] = temp;
			}

			// PLAYER SETUP
			for (int i = 0; i < playerCount; i++) {
				players[i].setIndex(i);
				stock.dealTo(players[i].getHand(), INITIAL_HAND_SIZE);
			}

			// GAME SETUP
			topCard = stock.dealCard();
			putInPile(topCard);

			setGameState(GameState.INGAME);
			/* EVENT */gameLoop
					.gameStateChanged(new GamePassiveEvent(Game.this, null, round, GameState.PREGAME, gameState));

			// Pause for a second... no way the game is already starting!
			_sleep(1000);

			// Set up first player
			curPlayer = getPlayers()[0];
			curPlayerIdx = curPlayer.getIndex();
			/* EVENT */currentPlayerChanged(new GamePassiveEvent(Game.this, curPlayer, round, gameState, gameState));

			// NOW THE REAL LOOP
			/*
			 * REALLY IMPORTANT TODO MUST DO TIMEOUT CHECK NOT JUST VALID MOVE
			 * CHECK BECAUSE NON-ENDING GAME
			 */
			// TODO: refactor with truth table
			// Null signifies a draw because I can't be bothered
			// with an other way of drawing honestly
			while (true) {
				// ask card until the player makes a valid move
				while (!isValidMove) {
					
					proposedCard = isAskingSuit ? curPlayer.requestCardWithSuit(askedSuit) : curPlayer.requestCard();
					
					if (isAskingSuit) {													// condA

						if (proposedCard == null) { 									// condB
							validate();													// actionA
							draw(1);
						}

						else if (GameRules.isValidAskedCard(proposedCard, askedSuit)) { // condC
							validCardAction();
						}
					} // end asking suit
					else if (isAceStreak) {												// condD

						if (proposedCard == null) { 									// condB
							validate();
						}

						else if (GameRules.isValidMove(topCard, proposedCard, true)) {	// condE
							validCardAction();
						}
					} // end ace streak
					else if (isUnderStreak) {											// condF

						if (proposedCard == null) {										// actionD
							validate();
							draw(GameRules.getUnderDrawAmount(underStreak));
						}

						else if (GameRules.isValidMove(topCard, proposedCard, true)) {	// condE
							validCardAction();
						}
					} // end under streak
					// General
					else {

						if (proposedCard == null) { 									// condB
							validate();													// actionA
							draw(1);
						}

						else if (GameRules.isValidMove(topCard, proposedCard, false)) {	// condG
							validCardAction();
						}
					} // end general
				} // end loop valid move
				isValidMove = false;
				/*
				 * This condition is checked once more outside the loop because
				 * the current player might have put a SEVEN
				 */
				if(curPlayer.getCardCount() == 0) {
					winner = curPlayer;
					break;
				}
				// TODO: bringback conditions can be implemented here, if needed. If so, modify the above code.
				if (isAskingSuit) {
					askedSuit = curPlayer.requestSuit();
				}

				curPlayerIdx = (curPlayerIdx + 1) % playerCount;
				curPlayer = players[curPlayerIdx];
				if(curPlayerIdx == 0) {
					round++;
				}
				_awaitGED();
			} // end game loop

		} // end run

		/**
		 * Deals the specified number of cards to the current player.
		 */
		private void draw(int number) {
			stock.dealTo(curPlayer.getHand(), number);
		}
		
		private void putInPile(GCard card) {
			pile.add(card);
			/* EVENT */pileSizeIncreased(new GameQuantitativeEvent(Game.this, pile.size()-1, pile.size()));
		}

		private void validCardAction() {
			putInPile(proposedCard);									// actionB
			validate();
		}
		
		private void validate() {
			setFlags(proposedCard);
			isValidMove = true;
		}
		
		private void setFlags(GCard card) {
			switch (card.getRank()) {
			case UNDER:
				isUnderStreak = true;
				isAceStreak = false;
				isAskingSuit = false;
				break;
			case ACE:
				isAceStreak = true;
				isUnderStreak = false;
				isAskingSuit = false;
				break;
			case SEVEN:
				isAskingSuit = true;
				isUnderStreak = false;
				isAceStreak = false;
				break;
			default:
				isUnderStreak = false;
				isAceStreak = false;
				isAskingSuit = false;
				break;
			}
		}

		/************************
		 * EVENT FIRING METHODS *
		 ************************/
		@Override
		public synchronized void playerJoined(GamePassiveEvent e) {
			System.err.println("  \033[31m+Player joined\033[0m");
			System.err.println("  \033[33m└─Event dispatched to " + listeners.size() + "\033[0m");
			ged.postEvent(listeners, (l) -> l.playerJoined(e));
		}

		@Override
		public synchronized void playerLeft(GamePassiveEvent e) {
			ged.postEvent(listeners, (l) -> l.playerLeft(e));
		}

		@Override
		public synchronized void currentPlayerChanged(GamePassiveEvent e) {
			ged.postEvent(listeners, (l) -> l.currentPlayerChanged(e));
		}

		@Override
		public synchronized void gameStateChanged(GamePassiveEvent e) {
			ged.postEvent(listeners, (l) -> l.gameStateChanged(e));
		}

		@Override
		public synchronized void playerCardCountChanged(GameQuantitativeEvent e, Player player) {
			System.err.println("  \033[31m+Card count changed\033[0m");
			System.err.println("  \033[33m└─Event dispatched to " + listeners.size() + "\033[0m");
			ged.postEvent(listeners, (l) -> l.playerCardCountChanged(e, player));
		}

		@Override
		public synchronized void roundChanged(GameQuantitativeEvent e) {
			System.err.println("  \033[31m+Round changed\033[0m");
			System.err.println("  \033[33m└─Event dispatched to " + listeners.size() + "\033[0m");
			ged.postEvent(listeners, (l) -> l.roundChanged(e));
		}

		@Override
		public synchronized void deckRefilled(Game game) {
			ged.postEvent(listeners, (l) -> l.deckRefilled(game));
		}

		@Override
		public synchronized void stockSizeDecreased(GameQuantitativeEvent e) {
			ged.postEvent(listeners, (l) -> l.stockSizeDecreased(e));
		}

		@Override
		public synchronized void pileSizeIncreased(GameQuantitativeEvent e) {
			ged.postEvent(listeners, (l) -> l.pileSizeIncreased(e));
		}

	}

}
