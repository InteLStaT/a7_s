package org.ucoz.intelstat.a7.core;

public class IllegalGameStateException extends IllegalStateException {

	private GameState state;
	
	public IllegalGameStateException(String message) {
		this(message, GameState.PREGAME);
	}
	
	public IllegalGameStateException(String message, GameState state) {
		super(message);
		this.state = state;
	}
	
	public GameState getGameState() {
		return state;
	}
	
}
