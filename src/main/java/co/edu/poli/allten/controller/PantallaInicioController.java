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

/**
 * Controlador de la pantalla de inicio (pantalla-inicio.fxml), la primera que
 * ve el jugador.
 *
 * Desde aqui el jugador escribe su apodo, elige el modo de juego (Normal o
 * Hardcore) y puede hacer tres cosas: iniciar una partida, consultar el
 * ranking o alternar entre el tema oscuro y el claro.
 *
 * Al iniciar una partida crea la SesionJuego con el jugador y el modo
 * elegidos, la inicia y se la entrega a PantallaJuegoController. Esta
 * pantalla no usa la base de datos: el jugador solo se guarda cuando termina
 * la partida.
 *
 * Los botones tienen animaciones de escala (se agrandan o se encogen un poco)
 * para dar respuesta visual al elegir el modo o al pasar el mouse.
 *
 * @see PantallaJuegoController
 * @see PantallaRankingController
 * @see SesionJuego
 */
public class PantallaInicioController {

    // Campo de texto donde el jugador escribe su apodo.
    @FXML
    private TextField campoApodo;

    // Boton para elegir el modo Normal.
    @FXML
    private Button normalButton;

    // Boton para elegir el modo Hardcore.
    @FXML
    private Button hardcoreButton;

    // Boton que inicia la partida.
    @FXML
    private Button startButton;

    // Boton que abre la pantalla de ranking.
    @FXML
    private Button rankingButton;

    // Animacion de escala del boton del modo Normal.
    private ScaleTransition normalTransition;

    // Animacion de escala del boton del modo Hardcore.
    private ScaleTransition hardcoreTransition;

    // Animacion de escala del boton de iniciar partida.
    private ScaleTransition startTransition;

    // Modo que el jugador tiene elegido. Empieza en NORMAL, que es el modo que
    // aparece activo al abrir la pantalla.
    private ModoJuego modoSeleccionado = ModoJuego.NORMAL;

    /**
     * Cambia entre el tema oscuro y el claro. Se ejecuta cuando el jugador
     * pulsa el boton del tema; el cambio lo hace GestorTema.
     */
    @FXML
    private void alternarTema() {
        GestorTema.alternar();
    }

    /**
     * Prepara la pantalla justo despues de que JavaFX carga el FXML y asigna
     * los controles. Crea las animaciones de escala de los tres botones y hace
     * que el boton de iniciar se agrande un poco cuando el mouse esta encima y
     * vuelva a su tamano normal cuando sale.
     */
    @FXML
    private void initialize() {
        normalTransition = crearTransicionEscala(normalButton);
        hardcoreTransition = crearTransicionEscala(hardcoreButton);
        startTransition = crearTransicionEscala(startButton);

        startButton.setOnMouseEntered(event -> animarEscala(startTransition, 1.04));
        startButton.setOnMouseExited(event -> animarEscala(startTransition, 1.0));
    }

    /**
     * Elige el modo Normal: lo guarda como modo seleccionado y marca visualmente
     * su boton como activo.
     */
    @FXML
    private void seleccionarNormal() {
        modoSeleccionado = ModoJuego.NORMAL;
        establecerModoSeleccionado(normalButton, hardcoreButton);
    }

    /**
     * Elige el modo Hardcore: lo guarda como modo seleccionado y marca
     * visualmente su boton como activo.
     */
    @FXML
    private void seleccionarHardcore() {
        modoSeleccionado = ModoJuego.HARDCORE;
        establecerModoSeleccionado(hardcoreButton, normalButton);
    }

    /**
     * Inicia una partida nueva. Primero anima el boton con un efecto de
     * pulsacion. Despues revisa que el apodo no este vacio; si lo esta, avisa
     * al jugador y deja el cursor en el campo de texto.
     *
     * Si el apodo es correcto, crea el Jugador y la SesionJuego con el modo
     * elegido, inicia la sesion (se generan los numeros y arranca el
     * cronometro), carga la pantalla de juego, le entrega la sesion a su
     * controlador y reemplaza la escena de la ventana.
     *
     * Si no se puede cargar la pantalla de juego (IOException) o la sesion no
     * puede iniciarse (IllegalStateException), muestra una alerta de error.
     */
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

    /**
     * Abre la pantalla de ranking mostrando la tabla del modo que el jugador
     * tiene seleccionado en este momento. Carga el FXML, reemplaza la escena de
     * la ventana y le indica al controlador del ranking cual modo mostrar.
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

    /**
     * Actualiza el aspecto de los dos botones de modo. Al boton elegido le pone
     * el estilo de activo y al otro el de inactivo, y anima la escala: el
     * elegido se agranda un poco y el otro vuelve a su tamano normal.
     *
     * Antes de agregar cada estilo se quitan ambos, para que un boton nunca
     * quede con los dos estilos a la vez.
     *
     * @param botonSeleccionado boton del modo que el jugador acaba de elegir
     * @param otroBoton         boton del otro modo
     */
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

    /**
     * Devuelve la animacion que corresponde a un boton de modo.
     *
     * @param boton boton de modo Normal o de modo Hardcore
     * @return la animacion del boton Normal si el boton recibido es ese; en
     *         cualquier otro caso, la del boton Hardcore
     */
    private ScaleTransition obtenerTransicion(Button boton) {
        return boton == normalButton ? normalTransition : hardcoreTransition;
    }

    /**
     * Crea una animacion de escala para un boton, de 160 milisegundos y con
     * arranque y final suaves. Todavia no la ejecuta ni define a que tamano
     * llegar: eso lo hace animarEscala.
     *
     * @param boton boton que se va a animar
     * @return la animacion lista para usarse
     */
    private ScaleTransition crearTransicionEscala(Button boton) {
        ScaleTransition transicion = new ScaleTransition(Duration.millis(160), boton);
        transicion.setInterpolator(Interpolator.EASE_BOTH);
        return transicion;
    }

    /**
     * Ejecuta una animacion de escala hasta el tamano indicado. Si la animacion
     * estaba en marcha, la detiene y la reinicia desde el tamano actual, para
     * que responda bien aunque el jugador mueva el mouse rapidamente.
     *
     * @param transicion animacion que se va a ejecutar
     * @param escala     tamano final en ancho y alto (1.0 es el tamano normal,
     *                   mayor se agranda y menor se encoge)
     */
    private void animarEscala(ScaleTransition transicion, double escala) {
        transicion.stop();
        transicion.setToX(escala);
        transicion.setToY(escala);
        transicion.playFromStart();
    }
}
