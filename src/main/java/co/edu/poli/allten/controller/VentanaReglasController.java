package co.edu.poli.allten.controller;

import java.io.IOException;

import co.edu.poli.allten.model.ModoJuego;
import co.edu.poli.allten.view.App;
import co.edu.poli.allten.view.GestorTema;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/** Ventana con las reglas del juego, organizada por secciones. */
public class VentanaReglasController {

    @FXML
    private Label modoLabel;

    @FXML
    private VBox operadoresAvanzadosBox;

    @FXML
    private VBox reglasHardcoreBox;

    @FXML
    private Button cerrarButton;

    /** Abre la ventana de reglas y espera a que el jugador la cierre. */
    public static void mostrar(Window propietario, ModoJuego modo) {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/ventana-reglas.fxml"));
            Parent raiz = cargador.load();
            VentanaReglasController controlador = cargador.getController();
            controlador.configurar(modo);

            Scene escena = new Scene(raiz);
            GestorTema.aplicar(escena); // la ventana usa el mismo tema (claro u oscuro)
            escena.setOnKeyPressed(evento -> {
                if (evento.getCode() == KeyCode.ESCAPE) {
                    ((Stage) escena.getWindow()).close();
                }
            });

            Stage ventana = new Stage();
            ventana.initOwner(propietario);
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setTitle("Reglas del juego");
            ventana.setResizable(false);
            ventana.setScene(escena);
            ventana.showAndWait();
        } catch (IOException excepcion) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("No se pudo abrir las reglas");
            alerta.setHeaderText(null);
            alerta.setContentText("Ocurrió un error al cargar la ventana de reglas: "
                    + excepcion.getMessage());
            alerta.showAndWait();
        }
    }

    private void configurar(ModoJuego modo) {
        boolean esHardcore = modo == ModoJuego.HARDCORE;
        modoLabel.setText(modo.name());
        modoLabel.getStyleClass().remove("modo-chip-hardcore");
        if (esHardcore) {
            modoLabel.getStyleClass().add("modo-chip-hardcore");
        }
        mostrarSi(operadoresAvanzadosBox, esHardcore);
        mostrarSi(reglasHardcoreBox, esHardcore);
    }

    @FXML
    private void cerrar() {
        ((Stage) cerrarButton.getScene().getWindow()).close();
    }

    // visible + managed juntos: el elemento aparece o desaparece sin dejar espacio vacío
    private static void mostrarSi(Node nodo, boolean mostrar) {
        nodo.setVisible(mostrar);
        nodo.setManaged(mostrar);
    }
}
