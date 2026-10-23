package co.edu.poli.allten.controller;

import java.io.IOException;

import co.edu.poli.allten.model.Jugador;
import co.edu.poli.allten.model.SesionJuego;
import co.edu.poli.allten.view.App;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class PantallaResumenController {

    @FXML
    private Label apodoLabel;

    @FXML
    private Label modoLabel;

    @FXML
    private Label tiempoLabel;

    @FXML
    private Label tiempoDescripcionLabel;

    private SesionJuego sesion;

    public void configurarSesion(SesionJuego sesion) {
        this.sesion = sesion;
        apodoLabel.setText(sesion.getJugador().getApodo());
        modoLabel.setText(sesion.getModo().name());

        long segundosTotales = sesion.getTiempoTotal() / 1000;
        long minutos = segundosTotales / 60;
        long segundos = segundosTotales % 60;
        tiempoLabel.setText(String.format("%02d:%02d", minutos, segundos));
        tiempoDescripcionLabel.setText(
                minutos + (minutos == 1 ? " minuto" : " minutos")
                        + " y " + segundos
                        + (segundos == 1 ? " segundo" : " segundos"));
    }

    @FXML
    private void consultarRanking() {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-ranking.fxml"));
            Parent raiz = cargador.load();
            PantallaRankingController controlador = cargador.getController();

            Stage escenario = (Stage) apodoLabel.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
            controlador.configurarSesion(sesion);
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo abrir el ranking",
                    "Ocurrió un error al cargar la pantalla de ranking: " + excepcion.getMessage());
        }
    }

    @FXML
    private void volverAlInicio() {
        try {
            Parent raiz = FXMLLoader.load(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-inicio.fxml"));
            Stage escenario = (Stage) apodoLabel.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo volver al inicio",
                    "Ocurrió un error al cargar la pantalla de inicio: " + excepcion.getMessage());
        }
    }

    @FXML
    private void nuevaPartida() {
        try {
            SesionJuego nuevaSesion = new SesionJuego(
                    new Jugador(sesion.getJugador().getId(), sesion.getJugador().getApodo()),
                    sesion.getModo());
            nuevaSesion.iniciarSesion();

            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-juego.fxml"));
            Parent raiz = cargador.load();
            PantallaJuegoController controlador = cargador.getController();
            controlador.configurarSesion(nuevaSesion);

            Stage escenario = (Stage) apodoLabel.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
        } catch (IOException | IllegalStateException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo iniciar una nueva partida",
                    "Ocurrió un error al iniciar la partida: " + excepcion.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
