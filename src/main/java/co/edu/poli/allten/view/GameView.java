package co.edu.poli.allten.view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameView {
	private final Label statusLabel = new Label("Listo para jugar");
	private final Button startButton = new Button("Iniciar partida");
	private final Scene scene;

	public GameView() {
		Label titleLabel = new Label("All Ten Game");
		VBox layout = new VBox(16, titleLabel, statusLabel, startButton);
		layout.setStyle("-fx-alignment: center; -fx-padding: 32;");
		scene = new Scene(layout, 600, 400);
	}

	public Scene createScene() {
		return scene;
	}

	public void setOnStartGame(Runnable action) {
		startButton.setOnAction(event -> action.run());
	}

	public void showGameStarted() {
		statusLabel.setText("Partida iniciada");
	}
}
