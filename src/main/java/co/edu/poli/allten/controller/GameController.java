package co.edu.poli.allten.controller;

import co.edu.poli.allten.model.GameModel;
import co.edu.poli.allten.view.GameView;
import co.edu.poli.allten.view.JuegoView;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameController {
	private final GameModel model;
	private final GameView view;
	private final JuegoView juegoView;
	private final Stage stage;
	private final Timeline timer;

	public GameController(GameModel model, GameView view, JuegoView juegoView, Stage stage) {
		this.model = model;
		this.view = view;
		this.juegoView = juegoView;
		this.stage = stage;

		// Actualiza el tiempo transcurrido cada segundo
		this.timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> refreshStatus()));
		this.timer.setCycleCount(Animation.INDEFINITE);

		view.setOnStartGame(this::startGame);
		juegoView.setOnExit(this::backToStart);
	}

	public Scene getScene() {
		return view.createScene();
	}

	private void startGame() {
		GameModel.Mode mode = view.isHardcore()
				? GameModel.Mode.HARDCORE
				: GameModel.Mode.NORMAL;
		model.startGame(view.getNickname(), mode);

		juegoView.showPlayer(model.getNickname(), model.getMode());
		juegoView.showMessage("Arma tu operación");
		refreshStatus();
		timer.playFromStart();
		showScene(juegoView.createScene());

		// PRUEBA TEMPORAL (bórrala después): quita los // para ver objetivos logrados
		// completeObjective(1);
		// completeObjective(4);
		// completeObjective(7);
	}

	/**
	 * Punto de entrada para la lógica del juego: llámalo cuando el jugador
	 * logre un objetivo (1 a 10) con su operación.
	 */
	public void completeObjective(int objective) {
		model.completeObjective(objective);
		refreshStatus();
		if (model.isFinished()) {
			timer.stop();
			juegoView.showFinished(model.getElapsedSeconds());
		}
	}

	private void refreshStatus() {
		juegoView.showObjectives(model.getCompletedObjectives());
		juegoView.showProgress(model.getCompletedCount(), GameModel.TOTAL_OBJECTIVES);
		juegoView.showElapsed(model.getElapsedSeconds());
	}

	private void backToStart() {
		timer.stop();
		showScene(view.createScene());
	}

	private void showScene(Scene scene) {
		stage.setScene(scene);
		stage.centerOnScreen();
	}

	public GameModel getModel() {
		return model;
	}

	public GameView getView() {
		return view;
	}
}