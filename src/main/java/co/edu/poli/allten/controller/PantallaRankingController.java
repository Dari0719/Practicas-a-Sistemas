package co.edu.poli.allten.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import co.edu.poli.allten.database.ConexionBaseDatos;
import co.edu.poli.allten.database.RankingDAO;
import co.edu.poli.allten.database.ResultadoRanking;
import co.edu.poli.allten.model.ModoJuego;
import co.edu.poli.allten.model.SesionJuego;
import co.edu.poli.allten.view.App;
import co.edu.poli.allten.view.ExportadorPNG;
import co.edu.poli.allten.view.GestorTema;

public class PantallaRankingController {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private TableView<ResultadoRanking> tablaRanking;

    @FXML
    private TableColumn<ResultadoRanking, String> apodoColumn;

    @FXML
    private TableColumn<ResultadoRanking, String> tiempoColumn;

    @FXML
    private TableColumn<ResultadoRanking, Integer> intentosColumn;

    @FXML
    private TableColumn<ResultadoRanking, String> fechaColumn;

    @FXML
    private Label modoLabel;

    @FXML
    private Label estadoRankingLabel;

    @FXML
    private VBox rankingRoot;

    @FXML
    private Button temaButton;

    private SesionJuego sesion;
    private ModoJuego modo = ModoJuego.NORMAL;
    private Integer posicionRanking;
    private String errorRanking;

    @FXML
    private void initialize() {
        actualizarTextoTema();
        apodoColumn.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().getApodo()));
        tiempoColumn.setCellValueFactory(fila ->
                new SimpleStringProperty(formatearTiempo(fila.getValue().getTiempoTotal())));
        intentosColumn.setCellValueFactory(fila ->
                new SimpleObjectProperty<>(fila.getValue().getIntentos()));
        fechaColumn.setCellValueFactory(fila ->
                new SimpleStringProperty(fila.getValue().getFecha().format(FORMATO_FECHA)));
        tablaRanking.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    }

    @FXML
    private void alternarTema() {
        GestorTema.alternar();
        actualizarTextoTema();
    }

    private void actualizarTextoTema() {
        temaButton.setText(GestorTema.esClaro() ? "☾  Tema oscuro" : "☀  Tema claro");
    }

    public void configurarSesion(SesionJuego sesion, Integer posicionRanking, String errorRanking) {
        this.sesion = sesion;
        this.posicionRanking = posicionRanking;
        this.errorRanking = errorRanking;
        configurarModo(sesion.getModo());
    }

    public void configurarModo(ModoJuego modo) {
        this.modo = modo;
        modoLabel.setText("Modo " + modo.name());
        cargarRanking();
    }

    @FXML
    private void volverAlResumen() {
        if (sesion == null) {
            volverAlInicio();
            return;
        }

        try {
            FXMLLoader cargador = new FXMLLoader(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-resumen.fxml"));
            Parent raiz = cargador.load();
            PantallaResumenController controlador = cargador.getController();
            controlador.configurarSesion(sesion, posicionRanking, errorRanking);

            Stage escenario = (Stage) tablaRanking.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo volver al resumen",
                    "Ocurrió un error al cargar el resumen: " + excepcion.getMessage());
        }
    }

    @FXML
    private void volverAlInicio() {
        try {
            Parent raiz = FXMLLoader.load(
                    App.class.getResource("/co/edu/poli/allten/views/pantalla-inicio.fxml"));
            Stage escenario = (Stage) tablaRanking.getScene().getWindow();
            escenario.setScene(new Scene(raiz));
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo volver al inicio",
                    "Ocurrió un error al cargar la pantalla de inicio: " + excepcion.getMessage());
        }
    }

    @FXML
    private void actualizarRanking() {
        cargarRanking();
    }

    @FXML
    private void descargarRanking() {
        Stage escenario = (Stage) tablaRanking.getScene().getWindow();
        try {
            if (ExportadorPNG.guardar(
                    rankingRoot,
                    escenario,
                    "ranking-allten-" + modo.name().toLowerCase() + ".png")) {
                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Ranking descargado",
                        "La imagen del ranking actual se guardó correctamente.");
            }
        } catch (IOException excepcion) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo descargar el ranking",
                    "Ocurrió un error al guardar la imagen: " + excepcion.getMessage());
        }
    }

    private void cargarRanking() {
        try {
            var resultados = new RankingDAO(new ConexionBaseDatos())
                    .obtenerRanking(100, modo);
            tablaRanking.setItems(FXCollections.observableArrayList(resultados));
            estadoRankingLabel.setText(resultados.isEmpty()
                    ? "Aún no hay partidas completadas en este modo."
                    : "");
        } catch (SQLException | IllegalStateException excepcion) {
            estadoRankingLabel.setText("No fue posible consultar el ranking.");
            String detalle = excepcion instanceof SQLException sqlException
                    && sqlException.getErrorCode() == 1045
                            ? "MySQL rechazó el acceso. Revisa ALLTEN_DB_USER y "
                                    + "ALLTEN_DB_PASSWORD y confirma que ese usuario tenga "
                                    + "permisos sobre la base allten."
                            : excepcion.getMessage();
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "No se pudo cargar el ranking",
                    "Ocurrió un error al consultar las partidas: " + detalle);
        }
    }

    private String formatearTiempo(long tiempoMilisegundos) {
        long segundosTotales = tiempoMilisegundos / 1000;
        return String.format("%02d:%02d", segundosTotales / 60, segundosTotales % 60);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
