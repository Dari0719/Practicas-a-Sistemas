package co.edu.poli.allten.controller;

import java.io.IOException;
import co.edu.poli.allten.model.Jugador;
import co.edu.poli.allten.model.SesionJuego;
import co.edu.poli.allten.view.App;
import co.edu.poli.allten.view.ExportadorPNG;
import co.edu.poli.allten.view.GestorTema;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controlador de la pantalla de resumen (pantalla-resumen.fxml), que se muestra
 * cuando el jugador termina una partida.
 *
 * Muestra el apodo, el modo de juego y el tiempo empleado. Desde aqui el
 * jugador puede consultar el ranking, volver a la pantalla de inicio o
 * empezar una nueva partida con el mismo apodo y el mismo modo.
 *
 * Esta pantalla solo muestra datos: cuando se abre, PantallaJuegoController ya
 * guardo la partida en la base de datos. Recibe la sesion por medio de
 * configurarSesion, que debe llamarse apenas se carga la pantalla.
 *
 * @see PantallaJuegoController
 * @see PantallaRankingController
 * @see SesionJuego
 */
public class PantallaResumenController {

    // Etiqueta con el apodo del jugador.
    @FXML
    private VBox resumenRoot;

    @FXML
    private Label apodoLabel;

    // Etiqueta con el nombre del modo de juego (NORMAL o HARDCORE).
    @FXML
    private Label modoLabel;

    // Etiqueta grande con el tiempo total en formato mm:ss.
    @FXML
    private Label tiempoLabel;

    // Etiqueta con el tiempo escrito en palabras, por ejemplo "2 minutos y 1
    // segundo".
    @FXML
    private Label tiempoDescripcionLabel;

    @FXML
    private Label intentosLabel;

    @FXML
    private Label posicionLabel;

    @FXML
    private Label posicionDescripcionLabel;

    @FXML
    private Button temaButton;

    // Partida terminada que se esta resumiendo. Se usa para mostrar los datos y
    // para saber con que jugador y modo se crea una nueva partida.
    private SesionJuego sesion;
    private Integer posicionRanking;
    private String errorRanking;

    @FXML
    private void initialize() {
        actualizarTextoTema();
    }

    @FXML
    private void alternarTema() {
        GestorTema.alternar();
        actualizarTextoTema();
    }

    private void actualizarTextoTema() {
        temaButton.setText(GestorTema.esClaro() ? "☾  Tema oscuro" : "☀  Tema claro");
    }

    /**
     * Recibe la partida terminada y la posición consultada en el ranking para
     * mostrar sus datos en pantalla.
     *
     * @param sesion partida que acaba de terminar; no debe ser nula
     * @param posicionRanking posición de la partida, o {@code null} si no aparece
     * @param errorRanking detalle del error al consultar la posición, si ocurrió
     */
    public void configurarSesion(SesionJuego sesion, Integer posicionRanking, String errorRanking) {
        this.sesion = sesion;
        this.posicionRanking = posicionRanking;
        this.errorRanking = errorRanking;
        apodoLabel.setText(sesion.getJugador().getApodo());
        modoLabel.setText(sesion.getModo().name());
        intentosLabel.setText(
                sesion.getIntentos() + (sesion.getIntentos() == 1 ? " intento" : " intentos"));

        long segundosTotales = sesion.getTiempoTotal() / 1000;
        long minutos = segundosTotales / 60;
        long segundos = segundosTotales % 60;
        tiempoLabel.setText(String.format("%02d:%02d", minutos, segundos));
        tiempoDescripcionLabel.setText(
                minutos + (minutos == 1 ? " minuto" : " minutos")
                        + " y " + segundos
                        + (segundos == 1 ? " segundo" : " segundos"));
        if (posicionRanking == null) {
            posicionLabel.setText("—");
            posicionDescripcionLabel.setText(errorRanking == null
                    ? "Esta partida no aparece en el ranking."
                    : "No fue posible consultar tu posición.");
        } else {
            posicionLabel.setText("#" + posicionRanking);
            posicionDescripcionLabel.setText(posicionRanking <= 10
                    ? "¡Estás entre las mejores partidas!"
                    : "Tu partida está fuera del top 10.");
        }
    }

    /**
     * Recibe la partida terminada y muestra sus datos en pantalla: el apodo, el
     * modo y el tiempo total. El tiempo se muestra de dos maneras: en formato
     * mm:ss y escrito en palabras, usando singular o plural segun corresponda
     * ("1 minuto", "2 minutos"). Los milisegundos sobrantes se descartan.
     *
     * @param sesion partida que acaba de terminar; no debe ser nula
     */
    public void configurarSesion(SesionJuego sesion) {
        configurarSesion(sesion, null, null);
    }

    @FXML
    private void descargarResumen() {
        Stage escenario = (Stage) resumenRoot.getScene().getWindow();
        try {
            if (ExportadorPNG.guardar(resumenRoot, escenario, "resumen-allten.png")) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Resumen descargado",
                        "La imagen del resumen se guardó correctamente.");
            }
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo descargar el resumen",
                    "Ocurrió un error al guardar la imagen: " + excepcion.getMessage());
        }
    }

    /**
     * Abre la pantalla de ranking. Carga el FXML, reemplaza la escena de la
     * ventana y le entrega la sesion al controlador del ranking, que muestra la
     * tabla del modo de esta partida y permite volver a este resumen.
     *
     * Si no se puede cargar la pantalla, muestra una alerta de error.
     */
    @FXML
    private void consultarRanking() {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-ranking.fxml"));
            Parent raiz = cargador.load();
            PantallaRankingController controlador = cargador.getController();

            Stage escenario = (Stage) apodoLabel.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
            controlador.configurarSesion(sesion, posicionRanking, errorRanking);
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo abrir el ranking",
                    "Ocurrió un error al cargar la pantalla de ranking: " + excepcion.getMessage());
        }
    }

    /**
     * Regresa a la pantalla de inicio, cargandola desde su archivo FXML y
     * reemplazando la escena de la ventana.
     *
     * Si no se puede cargar la pantalla, muestra una alerta de error.
     */
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

    /**
     * Empieza otra partida con el mismo jugador y el mismo modo, sin pasar por
     * la pantalla de inicio. Crea un nuevo Jugador con el identificador y el
     * apodo del jugador actual (por eso ya se reconoce como existente en la
     * base de datos), crea la nueva SesionJuego, la inicia, carga la pantalla
     * de juego, le entrega la sesion a su controlador y reemplaza la escena.
     *
     * Si no se puede cargar la pantalla (IOException) o la sesion no puede
     * iniciarse (IllegalStateException), muestra una alerta de error.
     */
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

    /**
     * Muestra una ventana de alerta y espera a que el jugador la cierre.
     *
     * @param tipo    tipo de alerta (advertencia, error, etc.), que define el
     *                icono
     * @param titulo  titulo de la ventana
     * @param mensaje texto que se muestra al jugador
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

