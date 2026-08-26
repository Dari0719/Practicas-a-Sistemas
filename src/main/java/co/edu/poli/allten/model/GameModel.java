package co.edu.poli.allten.model;

public class GameModel {
	private boolean gameStarted;

	public void startGame() {
		gameStarted = true;
	}

	public boolean isGameStarted() {
		return gameStarted;
	}
}
