package co.edu.poli.allten.view;

import java.io.IOException;
import java.io.UncheckedIOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

public class GameView {

	@FXML private TextField txtNickname;
	@FXML private RadioButton rbHardcore;
	@FXML private Button btnIniciar;

	private final Scene scene;

	public GameView() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/inicio.fxml"));
			loader.setController(this);
			scene = new Scene(loader.load());
		} catch (IOException e) {
			throw new UncheckedIOException("No se pudo cargar inicio.fxml", e);
		}
	}

	@FXML
	private void initialize() {
		// El botón solo se habilita cuando hay nickname
		btnIniciar.disableProperty().bind(txtNickname.textProperty().isEmpty());
	}

	public Scene createScene() {
		return scene;
	}

	public void setOnStartGame(Runnable action) {
		btnIniciar.setOnAction(event -> action.run());
	}

	public String getNickname() {
		return txtNickname.getText().trim();
	}

	public boolean isHardcore() {
		return rbHardcore.isSelected();
	}

	public void showGameStarted() {
		// Por ahora solo cambia el texto; aquí irá el cambio a la pantalla del juego
		btnIniciar.setText("Partida iniciada");
	}
}