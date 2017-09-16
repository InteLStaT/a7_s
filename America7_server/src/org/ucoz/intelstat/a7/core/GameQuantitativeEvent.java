package org.ucoz.intelstat.a7.core;

public class GameQuantitativeEvent {

	private Game game;
	private int oldValue;
	private int newValue;
	
	public GameQuantitativeEvent(Game game, int oldValue, int newValue) {
		this.game = game;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public Game getGame() {
		return game;
	}
	
	public int getOldValue() {
		return oldValue;
	}
	
	public int getNewValue() {
		return newValue;
	}
}
