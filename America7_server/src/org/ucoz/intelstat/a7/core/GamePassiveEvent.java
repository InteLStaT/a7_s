package org.ucoz.intelstat.a7.core;

import org.ucoz.intelstat.a7.core.Game.Player;

public class GamePassiveEvent implements GameEvent{

	private Game game;
	private Player player;
	private int round;
	private GameState oldState;
	private GameState newState;

	public GamePassiveEvent(Game game, Player player, int round, GameState oldState, GameState newState) {
		this.game = game;
		this.player = player;
		this.round = round;
		this.oldState = oldState;
		this.newState = newState;
	}
	
	public Game getGame() {
		return game;
	}
	
	public Player getCurrentPlayer() {
		return player;
	}

	public int getCurrentRound() {
		return round;
	}
	
	public GameState getOldState() {
		return oldState;
	}
	
	public GameState getNewState() {
		return newState;
	}
}
