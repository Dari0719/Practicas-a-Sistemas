package co.edu.poli.allten.controller;

import java.io.IOException;

import co.edu.poli.allten.model.Jugador;
import co.edu.poli.allten.model.ModoJuego;
import co.edu.poli.allten.model.SesionJuego;
import co.edu.poli.allten.view.App;
import co.edu.poli.allten.view.GestorTema;
import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaInicioController {

    @FXML
    private TextField campoApodo;

    @FXML
    private Button normalButton;

    @FXML
    private Button hardcoreButton;

    @FXML
    private Button startButton;

    @FXML
    private Button rankingButton;

    private ScaleTransition normalTransition;
    private ScaleTransition hardcoreTransition;
    private ScaleTransition startTransition;
    private ModoJuego modoSeleccionado = ModoJuego.NORMAL;

    @FXML
    private void alternarTema() {
        GestorTema.alternar();
    }

    @FXML
    private void initialize() {
        normalTransition = crearTransicionEscala(normalButton);
        hardcoreTransition = crearTransicionEscala(hardcoreButton);
        startTransition = crearTransicionEscala(startButton);

        startButton.setOnMouseEntered(event -> animarEscala(startTransition, 1.04));
        startButton.setOnMouseExited(event -> animarEscala(startTransition, 1.0));
    }

    @FXML
    private void seleccionarNormal() {
        modoSeleccionado = ModoJuego.NORMAL;
        establecerModoSeleccionado(normalButton, hardcoreButton);
    }

    @FXML
    private void seleccionarHardcore() {
        modoSeleccionado = ModoJuego.HARDCORE;
        establecerModoSeleccionado(hardcoreButton, normalButton);
    }

    @FXML
    private void iniciarPartida() {
        animarEscala(startTransition, 0.96);
        startTransition.setOnFinished(evento -> {
            startTransition.setOnFinished(null);
            animarEscala(startTransition, 1.0);
        });

        String apodo = campoApodo.getText();
        if (apodo == null || apodo.isBlank()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Falta tu apodo",
                    "Escribe tu apodo antes de iniciar la partida.");
            campoApodo.requestFocus();
            return;
        }

        try {
            SesionJuego sesion = new SesionJuego(new Jugador(apodo), modoSeleccionado);
            sesion.iniciarSesion();

            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-juego.fxml"));
            Parent raiz = cargador.load();
            PantallaJuegoController controlador = cargador.getController();
            controlador.configurarSesion(sesion);

            Stage escenario = (Stage) startButton.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo abrir la partida",
                    "Ocurrió un error al cargar la pantalla de juego: " + excepcion.getMessage());
        } catch (IllegalStateException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo iniciar la partida",
                    excepcion.getMessage());
        }
    }

    @FXML
    private void consultarRanking() {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-ranking.fxml"));
            Parent raiz = cargador.load();
            PantallaRankingController controlador = cargador.getController();

            Stage escenario = (Stage) rankingButton.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
            controlador.configurarModo(modoSeleccionado);
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo abrir el ranking",
                    "Ocurrió un error al cargar la pantalla de ranking: " + excepcion.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void establecerModoSeleccionado(Button botonSeleccionado, Button otroBoton) {
        botonSeleccionado.getStyleClass().remove("game-mode-button");
        botonSeleccionado.getStyleClass().remove("game-mode-button-active");
        botonSeleccionado.getStyleClass().add("game-mode-button-active");

        otroBoton.getStyleClass().remove("game-mode-button");
        otroBoton.getStyleClass().remove("game-mode-button-active");
        otroBoton.getStyleClass().add("game-mode-button");

        animarEscala(obtenerTransicion(botonSeleccionado), 1.05);
        animarEscala(obtenerTransicion(otroBoton), 1.0);
    }

    private ScaleTransition obtenerTransicion(Button boton) {
        return boton == normalButton ? normalTransition : hardcoreTransition;
    }

    private ScaleTransition crearTransicionEscala(Button boton) {
        ScaleTransition transicion = new ScaleTransition(Duration.millis(160), boton);
        transicion.setInterpolator(Interpolator.EASE_BOTH);
        return transicion;
    }

    private void animarEscala(ScaleTransition transicion, double escala) {
        transicion.stop();
        transicion.setToX(escala);
        transicion.setToY(escala);
        transicion.playFromStart();
    }
}
