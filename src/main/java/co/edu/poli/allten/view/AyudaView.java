package co.edu.poli.allten.view;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

import co.edu.poli.allten.model.ExpressionSolver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/** Ventana de ayuda: elige un objetivo y muestra expresiones que lo resuelven. */
public class AyudaView {

	@FXML private Label lblNumeros;
	@FXML private Label lblResumen;
	@FXML private GridPane gridObjetivos;
	@FXML private ToggleGroup grupoObjetivos;
	@FXML private ListView<String> lstExpresiones;
	@FXML private Button btnCerrar;

	private final Scene scene;
	private final Label lblVacio = new Label();
	private List<ToggleButton> botones;
	private Stage dialog;
	private IntConsumer onObjectiveSelected;
	private boolean suppress;

	public AyudaView() {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/ayuda.fxml"));
			loader.setController(this);
			scene = new Scene(loader.load());
		} catch (IOException e) {
			throw new UncheckedIOException("No se pudo cargar ayuda.fxml", e);
		}
		scene.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE) {
				close();
			}
		});
	}

	@FXML
	private void initialize() {
		// Los botones están en el FXML en orden: el primero es el objetivo 1, etc.
		botones = gridObjetivos.getChildren().stream()
				.filter(ToggleButton.class::isInstance)
				.map(ToggleButton.class::cast)
				.toList();
		for (int i = 0; i < botones.size(); i++) {
			botones.get(i).setUserData(i + 1);
		}

		// Siempre debe haber un objetivo elegido
		grupoObjetivos.selectedToggleProperty().addListener((obs, anterior, actual) -> {
			if (actual == null) {
				if (anterior != null) {
					anterior.setSelected(true);
				}
				return;
			}
			if (!suppress && onObjectiveSelected != null) {
				onObjectiveSelected.accept((Integer) actual.getUserData());
			}
		});

		lstExpresiones.setPlaceholder(lblVacio);
		btnCerrar.setOnAction(event -> close());
	}

	public void setOnObjectiveSelected(IntConsumer action) {
		this.onObjectiveSelected = action;
	}

	public void open(Stage owner, int[] numbers, Set<Integer> completados, int inicial) {
		if (dialog == null) {
			dialog = new Stage();
			dialog.initOwner(owner);
			dialog.initModality(Modality.APPLICATION_MODAL);
			dialog.setTitle("Ayuda");
			dialog.setResizable(false);
			dialog.setScene(scene);
		}

		lblNumeros.setText("Tus números:  " + Arrays.stream(numbers)
				.mapToObj(String::valueOf)
				.collect(Collectors.joining("  ·  ")));

		for (int i = 0; i < botones.size(); i++) {
			int numero = i + 1;
			botones.get(i).setText(completados.contains(numero) ? "✓ " + numero : String.valueOf(numero));
		}

		suppress = true;
		botones.get(inicial - 1).setSelected(true);
		suppress = false;
		if (onObjectiveSelected != null) {
			onObjectiveSelected.accept(inicial);
		}

		dialog.show();
		dialog.toFront();
	}

	public void showHints(int objetivo, List<String> expresiones) {
		lstExpresiones.getItems().setAll(expresiones.stream()
				.map(expresion -> expresion + " = " + objetivo)
				.toList());

		if (expresiones.isEmpty()) {
			lblVacio.setText("Con estos números no se puede lograr el " + objetivo);
			lblResumen.setText("SIN SOLUCIÓN");
		} else {
			lblResumen.setText("SUGERENCIAS PARA EL " + objetivo
					+ (ExpressionSolver.USE_ALL_NUMBERS ? " · USANDO LOS 4 NÚMEROS" : ""));
		}
	}

	private void close() {
		if (dialog != null) {
			dialog.close();
		}
	}
}