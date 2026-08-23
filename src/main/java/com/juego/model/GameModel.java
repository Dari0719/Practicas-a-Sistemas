package com.juego.model;

public class GameModel {
	private boolean gameStarted;

	public void startGame() {
		gameStarted = true;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}
}
