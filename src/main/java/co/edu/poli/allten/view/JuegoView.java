package co.edu.poli.allten.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Set;

import co.edu.poli.allten.model.GameModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

public class JuegoView {

	@FXML private Label lblJugador;
	@FXML private Label lblModo;
	@FXML private Button btnSalir;
	@FXML private Button btnAyuda;
	@FXML private Label lblProgreso;
	@FXML private ProgressBar barProgreso;
	@FXML private Label lblTiempo;
	@FXML private GridPane gridObjetivos;
	@FXML private Label lblExpresion;
	@FXML private Button btnFicha1;
	@FXML private Button btnFicha2;
	@FXML private Button btnFicha3;
	@FXML private Button btnFicha4;

	private List<Label> objetivos;
	private Button[] fichas;
	private final Scene scene;

	public JuegoView() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/juego.fxml"));
			loader.setController(this);
			scene = new Scene(loader.load());
		} catch (IOException e) {
			throw new UncheckedIOException("No se pudo cargar juego.fxml", e);
		}
	}

	@FXML
	private void initialize() {
		// Los labels están en el FXML en orden: el primero es el objetivo 1, etc.
		objetivos = gridObjetivos.getChildren().stream()
				.filter(Label.class::isInstance)
				.map(Label.class::cast)
				.toList();
		fichas = new Button[] { btnFicha1, btnFicha2, btnFicha3, btnFicha4 };
	}

	public Scene createScene() {
		return scene;
	}

	public void showPlayer(String nickname, GameModel.Mode mode) {
		lblJugador.setText(nickname);
		lblModo.setText(mode.name());
		lblModo.getStyleClass().remove("chip-hardcore");
		if (mode == GameModel.Mode.HARDCORE) {
			lblModo.getStyleClass().add("chip-hardcore");
		}
	}

	/** Muestra en las cuatro fichas los números del reto. */
	public void showNumbers(int[] numeros) {
		for (int i = 0; i < fichas.length; i++) {
			fichas[i].setText(String.valueOf(numeros[i]));
		}
	}

	/** Pinta los objetivos logrados con una marca y color; el resto queda igual. */
	public void showObjectives(Set<Integer> completados) {
		for (int i = 0; i < objetivos.size(); i++) {
			int numero = i + 1;
			Label label = objetivos.get(i);
			boolean logrado = completados.contains(numero);
			label.setText(logrado ? "✓ " + numero : String.valueOf(numero));
			label.getStyleClass().remove("objetivo-logrado");
			if (logrado) {
				label.getStyleClass().add("objetivo-logrado");
			}
		}
	}

	public void showProgress(int logrados, int total) {
		lblProgreso.setText(logrados + " / " + total);
		barProgreso.setProgress((double) logrados / total);
	}

	public void showElapsed(long segundos) {
		lblTiempo.setText(formatTime(segundos));
	}

	public void showMessage(String text) {
		lblExpresion.setText(text);
		lblExpresion.getStyleClass().remove("expresion-exito");
	}

	public void showFinished(long segundos) {
		lblExpresion.setText("¡Completaste los 10 objetivos en " + formatTime(segundos) + "!");
		if (!lblExpresion.getStyleClass().contains("expresion-exito")) {
			lblExpresion.getStyleClass().add("expresion-exito");
		}
	}

	public void setOnHelp(Runnable action) {
		btnAyuda.setOnAction(event -> action.run());
	}

	public void setOnExit(Runnable action) {
		btnSalir.setOnAction(event -> action.run());
	}

	private static String formatTime(long segundos) {
		long horas = segundos / 3600;
		long minutos = (segundos % 3600) / 60;
		long seg = segundos % 60;
		return horas > 0
				? String.format("%d:%02d:%02d", horas, minutos, seg)
				: String.format("%02d:%02d", minutos, seg);
	}
}