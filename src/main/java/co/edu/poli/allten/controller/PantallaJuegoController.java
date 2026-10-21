package co.edu.poli.allten.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import co.edu.poli.allten.database.ConexionBaseDatos;
import co.edu.poli.allten.database.JugadorDAO;
import co.edu.poli.allten.database.PartidaDAO;
import co.edu.poli.allten.model.BuscadorSoluciones;
import co.edu.poli.allten.model.Ronda;
import co.edu.poli.allten.model.ResultadoValidacion;
import co.edu.poli.allten.model.SesionJuego;
import co.edu.poli.allten.model.ValidadorExpresion;
import co.edu.poli.allten.view.App;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaJuegoController {

    @FXML
    private Button reglasButton;

    @FXML
    private Button pistaButton;

    @FXML
    private Button modeBadge;

    @FXML
    private Label cronometroLabel;

    @FXML
    private GridPane objetivosGrid;

    @FXML
    private Button btnDyn1;

    @FXML
    private Button btnDyn2;

    @FXML
    private Button btnDyn3;

    @FXML
    private Button btnDyn4;

    @FXML
    private Button btnLimpiar;

    @FXML
    private Button btnBorrar;

    @FXML
    private Button btnIgual;

    @FXML
    private TextField campoExpresion;

    private SesionJuego sesion;
    private int objetivoSeleccionado = 1;
    private final Map<Button, ParallelTransition> animacionesDisponibilidad = new IdentityHashMap<>();
    private ScaleTransition animacionEntrada;
    private PauseTransition pausaResaltadoEntrada;
    private Timeline cronometro;
    private boolean animacionEntradaPendiente;

    @FXML
    private void initialize() {
        animacionEntrada = new ScaleTransition(Duration.millis(90), campoExpresion);
        animacionEntrada.setFromX(1.0);
        animacionEntrada.setFromY(1.0);
        animacionEntrada.setToX(1.015);
        animacionEntrada.setToY(1.015);
        animacionEntrada.setAutoReverse(true);
        animacionEntrada.setCycleCount(2);
        animacionEntrada.setInterpolator(Interpolator.EASE_BOTH);

        pausaResaltadoEntrada = new PauseTransition(Duration.millis(220));
        pausaResaltadoEntrada.setOnFinished(evento ->
                campoExpresion.getStyleClass().remove("expression-input-flash"));

        campoExpresion.setTextFormatter(new TextFormatter<String>(cambio -> {
            String texto = cambio.getText()
                    .replace('*', '×')
                    .replace('/', '÷');
            if (!texto.matches("[0-9()+\\-×÷\\s]*")) {
                return null;
            }
            cambio.setText(texto);
            if (sesion != null && !numerosDentroDelLimite(cambio.getControlNewText())) {
                return null;
            }
            if (!texto.isEmpty()
                    && !cambio.getControlText().equals(cambio.getControlNewText())) {
                animacionEntradaPendiente = true;
            }
            return cambio;
        }));
        campoExpresion.textProperty().addListener((observable, textoAnterior, textoNuevo) -> {
            actualizarDisponibilidadNumeros();
            if (animacionEntradaPendiente && !textoAnterior.equals(textoNuevo)) {
                animacionEntradaPendiente = false;
                animarEntradaExpresion();
            }
        });
    }

    public void configurarSesion(SesionJuego sesion) {
        this.sesion = sesion;
        List<Integer> numeros = sesion.getNumerosDisponibles();
        List<Button> botonesNumeros = List.of(btnDyn1, btnDyn2, btnDyn3, btnDyn4);

        for (int indice = 0; indice < botonesNumeros.size(); indice++) {
            botonesNumeros.get(indice).setText(String.valueOf(numeros.get(indice)));
        }
        modeBadge.setText(sesion.getModo().name());
        actualizarCronometro();
        cronometro = new Timeline(new KeyFrame(Duration.seconds(1), evento -> actualizarCronometro()));
        cronometro.setCycleCount(Timeline.INDEFINITE);
        cronometro.play();
        actualizarDisponibilidadNumeros();
        actualizarObjetivoSeleccionado();
    }

    @FXML
    private void consultarReglas() {
        animarSeleccion(reglasButton);
        mostrarAlerta(
                Alert.AlertType.INFORMATION,
                "Reglas del juego",
                "Resuelve los objetivos del 1 al 10 en cualquier orden.\n\n"
                        + "Cada expresión debe usar los cuatro números generados exactamente "
                        + "una vez. Puedes reutilizar el conjunto en cada objetivo.\n\n"
                        + "Operaciones permitidas: suma (+), resta (-), multiplicación (×), "
                        + "división (÷) y paréntesis. No se permite dividir entre cero.\n\n"
                        + "El resultado debe ser un objetivo pendiente.");
    }

    @FXML
    private void consultarPista() {
        animarSeleccion(pistaButton);
        Ronda ronda = sesion.getRondas().stream()
                .filter(item -> item.getNumeroObjetivo() == objetivoSeleccionado)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el objetivo " + objetivoSeleccionado + "."));

        if (ronda.isResuelta()) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Objetivo resuelto",
                    "El objetivo " + objetivoSeleccionado + " ya está resuelto.");
            return;
        }

        try {
            List<String> soluciones = new BuscadorSoluciones().buscarSoluciones(
                    sesion.getNumerosDisponibles(),
                    objetivoSeleccionado,
                    sesion.getModo());
            if (soluciones.isEmpty()) {
                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "No hay pista disponible",
                        "No se encontró una solución para el objetivo " + objetivoSeleccionado + ".");
                return;
            }

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Pista para el objetivo " + objetivoSeleccionado,
                    "Una forma de obtener " + objetivoSeleccionado + " es:\n\n"
                            + soluciones.get(0));
        } catch (IllegalArgumentException | UnsupportedOperationException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo generar la pista",
                    excepcion.getMessage());
        }
    }

    @FXML
    private void seleccionarObjetivo(ActionEvent evento) {
        Button boton = (Button) evento.getSource();
        objetivoSeleccionado = Integer.parseInt(boton.getText());
        actualizarObjetivoSeleccionado();
    }

    @FXML
    private void agregarAlCampo(ActionEvent evento) {
        Button boton = (Button) evento.getSource();
        campoExpresion.appendText(boton.getText());
        campoExpresion.requestFocus();
        animarPulsacion(boton);
    }

    private void animarEntradaExpresion() {
        animacionEntrada.stop();
        campoExpresion.getStyleClass().remove("expression-input-flash");
        campoExpresion.getStyleClass().add("expression-input-flash");
        pausaResaltadoEntrada.stop();
        pausaResaltadoEntrada.playFromStart();
        animacionEntrada.playFromStart();
    }

    @FXML
    private void validarExpresion() {
        animarPulsacion(btnIgual);
        String expresion = campoExpresion.getText();
        String expresionParaValidar = normalizarOperadores(expresion);
        ResultadoValidacion resultado = sesion.procesarRespuesta(expresionParaValidar);

        if (resultado == ResultadoValidacion.VALIDO) {
            int objetivoResuelto = (int) Math.round(
                    new ValidadorExpresion().calcularResultado(expresionParaValidar));
            marcarObjetivosResueltos();
            campoExpresion.clear();
            objetivoSeleccionado = objetivoResuelto;
            actualizarObjetivoSeleccionado();

            if (sesion.estaCompleta()) {
                cronometro.stop();
                actualizarCronometro();
                finalizarPartida();
            } else {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "¡Correcto!",
                        "Resolviste el objetivo " + objetivoResuelto + ".");
            }
            return;
        }

        String mensaje = switch (resultado) {
            case NUMEROS_INVALIDOS ->
                "Usa los cuatro números generados exactamente una vez cada uno.";
            case OPERACION_INVALIDA ->
                "La operación no es válida. Revisa los operadores y paréntesis.";
            case RESULTADO_INCORRECTO ->
                "La expresión es válida, pero su resultado no corresponde a un objetivo pendiente.";
            case OBJETIVO_YA_RESUELTO ->
                "Ese objetivo ya fue resuelto o la partida ya terminó.";
            case VALIDO -> throw new IllegalStateException("Resultado de validación inesperado.");
        };
        mostrarAlerta(Alert.AlertType.WARNING, "No se pudo validar", mensaje);
    }

    private void actualizarCronometro() {
        long segundosTotales = sesion.getTiempoTotal() / 1000;
        cronometroLabel.setText(String.format(
                "%02d:%02d", segundosTotales / 60, segundosTotales % 60));
    }

    private void finalizarPartida() {
        String errorPersistencia = null;
        try {
            ConexionBaseDatos conexion = new ConexionBaseDatos();
            new JugadorDAO(conexion).guardar(sesion.getJugador());
            new PartidaDAO(conexion).guardar(sesion);
        } catch (SQLException | IllegalStateException excepcion) {
            errorPersistencia = excepcion.getMessage();
        }

        try {
            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-resumen.fxml"));
            Parent raiz = cargador.load();
            PantallaResumenController controlador = cargador.getController();
            controlador.configurarSesion(sesion);

            Stage escenario = (Stage) modeBadge.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo abrir el resumen",
                    "La partida terminó, pero ocurrió un error al cargar el resumen: "
                            + excepcion.getMessage());
            return;
        }

        if (errorPersistencia != null) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo guardar la partida",
                    "El resumen está disponible, pero el resultado no se guardó y no aparecerá "
                            + "en el ranking.\n\nDetalle: " + errorPersistencia);
        }
    }

    private String normalizarOperadores(String expresion) {
        return expresion.replace('×', '*').replace('÷', '/');
    }

    @FXML
    private void limpiarExpresion() {
        animarPulsacion(btnLimpiar);
        campoExpresion.clear();
        campoExpresion.requestFocus();
    }

    @FXML
    private void borrarCaracter() {
        animarPulsacion(btnBorrar);
        String expresion = campoExpresion.getText();
        if (expresion.isEmpty()) {
            return;
        }

        campoExpresion.deleteText(expresion.length() - 1, expresion.length());
        campoExpresion.requestFocus();
    }

    private boolean numerosDentroDelLimite(String expresion) {
        int[] cantidadesDisponibles = new int[10];
        for (int numero : sesion.getNumerosDisponibles()) {
            cantidadesDisponibles[numero]++;
        }

        int[] cantidadesUsadas = new int[10];
        for (int indice = 0; indice < expresion.length(); indice++) {
            char caracter = expresion.charAt(indice);
            if (caracter >= '0' && caracter <= '9') {
                int numero = caracter - '0';
                if (++cantidadesUsadas[numero] > cantidadesDisponibles[numero]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void actualizarDisponibilidadNumeros() {
        if (sesion == null) {
            return;
        }

        int[] usosPendientesPorNumero = new int[10];
        String expresion = campoExpresion.getText();
        for (int indice = 0; indice < expresion.length(); indice++) {
            char caracter = expresion.charAt(indice);
            if (caracter >= '0' && caracter <= '9') {
                usosPendientesPorNumero[caracter - '0']++;
            }
        }

        List<Button> botonesNumeros = List.of(btnDyn1, btnDyn2, btnDyn3, btnDyn4);
        for (Button boton : botonesNumeros) {
            int numero = Integer.parseInt(boton.getText());
            boolean debeDeshabilitarse = usosPendientesPorNumero[numero] > 0;
            if (boton.isDisabled() != debeDeshabilitarse) {
                boton.setDisable(debeDeshabilitarse);
                animarCambioDisponibilidad(boton, debeDeshabilitarse);
            }
            if (debeDeshabilitarse) {
                usosPendientesPorNumero[numero]--;
            }
        }
    }

    private void animarCambioDisponibilidad(Button boton, boolean deshabilitado) {
        ParallelTransition animacionAnterior = animacionesDisponibilidad.remove(boton);
        if (animacionAnterior != null) {
            animacionAnterior.stop();
        }

        FadeTransition desvanecimiento = new FadeTransition(Duration.millis(180), boton);
        desvanecimiento.setFromValue(boton.getOpacity());
        desvanecimiento.setToValue(deshabilitado ? 0.55 : 1.0);

        ScaleTransition escala = new ScaleTransition(Duration.millis(180), boton);
        escala.setInterpolator(Interpolator.EASE_BOTH);
        escala.setFromX(boton.getScaleX());
        escala.setFromY(boton.getScaleY());
        escala.setToX(deshabilitado ? 0.9 : 1.0);
        escala.setToY(deshabilitado ? 0.9 : 1.0);

        ParallelTransition animacion = new ParallelTransition(desvanecimiento, escala);
        animacionesDisponibilidad.put(boton, animacion);
        animacion.setOnFinished(evento -> animacionesDisponibilidad.remove(boton, animacion));
        animacion.playFromStart();
    }

    private void marcarObjetivosResueltos() {
        for (javafx.scene.Node nodo : objetivosGrid.getChildren()) {
            if (nodo instanceof Button boton) {
                int objetivo = Integer.parseInt(boton.getText());
                boolean resuelto = sesion.getRondas().stream()
                        .anyMatch(ronda -> ronda.getNumeroObjetivo() == objetivo
                                && ronda.isResuelta());
                if (resuelto && !boton.getStyleClass().contains("number-button-solved")) {
                    boton.getStyleClass().add("number-button-solved");
                }
            }
        }
    }

    private void animarPulsacion(Button boton) {
        boton.getStyleClass().remove("button-pressed-flash");
        boton.getStyleClass().add("button-pressed-flash");

        ScaleTransition pulso = new ScaleTransition(Duration.millis(100), boton);
        pulso.setFromX(1.0);
        pulso.setFromY(1.0);
        pulso.setToX(0.94);
        pulso.setToY(0.94);
        pulso.setInterpolator(Interpolator.EASE_BOTH);
        pulso.setAutoReverse(true);
        pulso.setCycleCount(2);
        pulso.playFromStart();

        PauseTransition pausa = new PauseTransition(Duration.millis(300));
        pausa.setOnFinished(evento -> boton.getStyleClass().remove("button-pressed-flash"));
        pausa.playFromStart();
    }

    private void actualizarObjetivoSeleccionado() {
        for (javafx.scene.Node nodo : objetivosGrid.getChildren()) {
            if (nodo instanceof Button boton) {
                boton.getStyleClass().remove("number-button-selected");
                if (boton.getText().equals(String.valueOf(objetivoSeleccionado))) {
                    boton.getStyleClass().add("number-button-selected");
                }
            }
        }
    }

    @FXML
    private void volverAlInicio() {
        try {
            Parent raiz = FXMLLoader.load(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-inicio.fxml"));
            Stage escenario = (Stage) modeBadge.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
        } catch (IOException excepcion) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("No se pudo volver al inicio");
            alerta.setHeaderText(null);
            alerta.setContentText(
                    "Ocurrió un error al cargar la pantalla de inicio: " + excepcion.getMessage());
            alerta.showAndWait();
        }
    }

    private void animarSeleccion(Button boton) {
        ScaleTransition animacion = new ScaleTransition(Duration.millis(120), boton);
        animacion.setFromX(1.0);
        animacion.setFromY(1.0);
        animacion.setToX(1.15);
        animacion.setToY(1.15);
        animacion.setInterpolator(Interpolator.EASE_BOTH);
        animacion.setAutoReverse(true);
        animacion.setCycleCount(2);
        animacion.playFromStart();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
