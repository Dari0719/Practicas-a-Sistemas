package co.edu.poli.allten.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import co.edu.poli.allten.model.ModoJuego;
import co.edu.poli.allten.view.App;
import co.edu.poli.allten.view.GestorTema;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 * Ventana de pista: muestra los números del reto y, de una en una, formas de
 * lograr el objetivo elegido. También muestra mensajes de estado (objetivo ya
 * resuelto, sin solución o error) con el mismo diseño.
 */
public class VentanaPistaController {

    public enum EstadoPista {
        RESUELTO,
        SIN_SOLUCION,
        ERROR
    }

    // Máximo de formas distintas que ofrece la ventana
    private static final int MAX_FORMAS = 10;

    @FXML
    private Label objetivoLabel;

    @FXML
    private VBox seccionPista;

    @FXML
    private HBox numerosBox;

    @FXML
    private ScrollPane solucionesScroll;

    @FXML
    private VBox solucionesBox;

    @FXML
    private Label contadorLabel;

    @FXML
    private Button otraFormaButton;

    @FXML
    private Label notaHardcoreLabel;

    @FXML
    private VBox seccionEstado;

    @FXML
    private Label estadoIconoLabel;

    @FXML
    private Label estadoTituloLabel;

    @FXML
    private Label estadoMensajeLabel;

    @FXML
    private Button cerrarButton;

    private List<String> formas = List.of();
    private int mostradas;

    /** Abre la pista de un objetivo con las soluciones encontradas. */
    public static void mostrarPista(
            Window propietario,
            int objetivo,
            List<Integer> numeros,
            ModoJuego modo,
            List<String> soluciones) {
        abrir(propietario, controlador ->
                controlador.configurarPista(objetivo, numeros, modo, soluciones));
    }

    /** Abre la ventana con un mensaje de estado en lugar de soluciones. */
    public static void mostrarEstado(
            Window propietario,
            int objetivo,
            EstadoPista estado,
            String mensaje) {
        abrir(propietario, controlador ->
                controlador.configurarEstado(objetivo, estado, mensaje));
    }

    private static void abrir(Window propietario, Consumer<VentanaPistaController> configuracion) {
        try {
            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/ventana-pista.fxml"));
            Parent raiz = cargador.load();
            VentanaPistaController controlador = cargador.getController();
            configuracion.accept(controlador);

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
            ventana.setTitle("Pista");
            ventana.setResizable(false);
            ventana.setScene(escena);
            ventana.showAndWait();
        } catch (IOException excepcion) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("No se pudo abrir la pista");
            alerta.setHeaderText(null);
            alerta.setContentText("Ocurrió un error al cargar la ventana de pista: "
                    + excepcion.getMessage());
            alerta.showAndWait();
        }
    }

    private void configurarPista(
            int objetivo,
            List<Integer> numeros,
            ModoJuego modo,
            List<String> soluciones) {
        objetivoLabel.setText("Objetivo " + objetivo);
        mostrarSi(seccionPista, true);
        mostrarSi(seccionEstado, false);
        mostrarSi(notaHardcoreLabel, modo == ModoJuego.HARDCORE);

        for (Integer numero : numeros) {
            Label ficha = new Label(String.valueOf(numero));
            ficha.getStyleClass().add("numero-chip");
            numerosBox.getChildren().add(ficha);
        }

        // Se ofrecen primero las formas más simples: pocos paréntesis y más cortas
        formas = soluciones.stream()
                .map(VentanaPistaController::formatear)
                .distinct()
                .sorted(Comparator.<String>comparingInt(VentanaPistaController::complejidad)
                        .thenComparing(Comparator.<String>naturalOrder()))
                .limit(MAX_FORMAS)
                .toList();
        mostradas = 0;
        mostrarSiguienteForma();
    }

    private void configurarEstado(int objetivo, EstadoPista estado, String mensaje) {
        objetivoLabel.setText("Objetivo " + objetivo);
        mostrarSi(seccionPista, false);
        mostrarSi(seccionEstado, true);

        String icono;
        String titulo;
        String clase;
        switch (estado) {
            case RESUELTO -> {
                icono = "✓";
                titulo = "Objetivo resuelto";
                clase = "estado-ok";
            }
            case SIN_SOLUCION -> {
                icono = "!";
                titulo = "No hay pista disponible";
                clase = "estado-aviso";
            }
            default -> {
                icono = "×";
                titulo = "No se pudo generar la pista";
                clase = "estado-error";
            }
        }
        estadoIconoLabel.setText(icono);
        estadoIconoLabel.getStyleClass().add(clase);
        estadoTituloLabel.setText(titulo);
        estadoMensajeLabel.setText(mensaje);
    }

    @FXML
    private void mostrarOtraForma() {
        mostrarSiguienteForma();
    }

    private void mostrarSiguienteForma() {
        if (mostradas >= formas.size()) {
            return;
        }

        String forma = formas.get(mostradas);
        mostradas++;
        solucionesBox.getChildren().add(crearTarjetaForma(mostradas, forma));
        contadorLabel.setText("Forma " + mostradas + " de " + formas.size());

        boolean hayMas = mostradas < formas.size();
        otraFormaButton.setDisable(!hayMas);
        otraFormaButton.setText(hayMas ? "Mostrar otra forma" : "No hay más formas");

        // Baja hasta la forma recién agregada una vez que la lista se acomodó
        Platform.runLater(() -> solucionesScroll.setVvalue(1.0));
    }

    private Node crearTarjetaForma(int numero, String forma) {
        Label indice = new Label(String.valueOf(numero));
        indice.getStyleClass().add("solucion-indice");

        Label texto = new Label(forma);
        texto.getStyleClass().add("solucion-texto");
        texto.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(texto, Priority.ALWAYS);

        Button copiar = new Button("Copiar");
        copiar.getStyleClass().add("boton-copiar");
        copiar.setMinWidth(92);
        copiar.setOnAction(evento -> copiarAlPortapapeles(copiar, forma));

        HBox tarjeta = new HBox(12, indice, texto, copiar);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.getStyleClass().add("solucion-tarjeta");
        return tarjeta;
    }

    private void copiarAlPortapapeles(Button boton, String texto) {
        ClipboardContent contenido = new ClipboardContent();
        contenido.putString(texto);
        Clipboard.getSystemClipboard().setContent(contenido);

        boton.setText("¡Copiado!");
        PauseTransition pausa = new PauseTransition(Duration.millis(1200));
        pausa.setOnFinished(evento -> boton.setText("Copiar"));
        pausa.play();
    }

    @FXML
    private void cerrar() {
        ((Stage) cerrarButton.getScene().getWindow()).close();
    }

    // Convierte "((3 + 5) * 2)" en "(3 + 5) × 2": sin paréntesis de más y con los
    // mismos símbolos que el campo de la expresión (que acepta × y ÷).
    private static String formatear(String expresion) {
        String actual = expresion;
        String anterior;
        do {
            anterior = actual;
            actual = quitarParentesisExternos(actual);
        } while (!actual.equals(anterior));
        return actual.replace('*', '×').replace('/', '÷');
    }

    private static String quitarParentesisExternos(String expresion) {
        if (!expresion.startsWith("(") || !expresion.endsWith(")")) {
            return expresion;
        }
        int profundidad = 0;
        for (int indice = 0; indice < expresion.length(); indice++) {
            char caracter = expresion.charAt(indice);
            if (caracter == '(') {
                profundidad++;
            } else if (caracter == ')') {
                profundidad--;
                // El primer paréntesis se cierra antes del final: no envuelve todo
                if (profundidad == 0 && indice < expresion.length() - 1) {
                    return expresion;
                }
            }
        }
        return expresion.substring(1, expresion.length() - 1);
    }

    private static int complejidad(String texto) {
        int parentesis = 0;
        for (char caracter : texto.toCharArray()) {
            if (caracter == '(') {
                parentesis++;
            }
        }
        return parentesis * 100 + texto.length();
    }

    private static void mostrarSi(Node nodo, boolean mostrar) {
        nodo.setVisible(mostrar);
        nodo.setManaged(mostrar);
    }
}