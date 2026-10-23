package co.edu.poli.allten.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Controla el tema de la aplicación (oscuro o claro).
 *
 * El tema oscuro es el que ya traen las hojas de estilo de cada pantalla. El claro
 * se agrega encima con tema-claro.css, que solo cambia colores. La elección se
 * recuerda entre ejecuciones y se aplica sola a cada pantalla nueva.
 */
public final class GestorTema {

    private static final String CLASE_CLARO = "tema-claro";
    private static final String HOJA_CLARO = "/co/edu/poli/allten/styles/tema-claro.css";
    private static final String CLAVE = "tema";
    private static final Path ARCHIVO =
            Path.of(System.getProperty("user.home"), ".allten", "tema.properties");

    private static Stage escenario;
    private static boolean claro = cargarPreferencia();

    private GestorTema() {
    }

    /** Llamar una vez en App.start, antes de mostrar la primera pantalla. */
    public static void inicializar(Stage stage) {
        escenario = stage;
        // Cada vez que se cambia de pantalla (stage.setScene) se aplica el tema elegido
        stage.sceneProperty().addListener((observable, anterior, nueva) -> aplicar(nueva));
        aplicar(stage.getScene());
    }

    public static boolean esClaro() {
        return claro;
    }

    /** Cambia entre claro y oscuro, lo guarda y actualiza la pantalla actual. */
    public static void alternar() {
        claro = !claro;
        guardarPreferencia();
        if (escenario != null) {
            aplicar(escenario.getScene());
        }
    }

    private static void aplicar(Scene escena) {
        if (escena == null) {
            return;
        }
        Parent raiz = escena.getRoot();
        URL hoja = GestorTema.class.getResource(HOJA_CLARO);

        raiz.getStyleClass().remove(CLASE_CLARO);
        if (hoja != null) {
            raiz.getStylesheets().remove(hoja.toExternalForm());
        }

        if (!claro) {
            return;
        }
        if (hoja == null) {
            System.err.println("No se encontró " + HOJA_CLARO + "; se mantiene el tema oscuro.");
            return;
        }
        raiz.getStyleClass().add(CLASE_CLARO);
        raiz.getStylesheets().add(hoja.toExternalForm());
    }

    private static boolean cargarPreferencia() {
        try (InputStream entrada = Files.newInputStream(ARCHIVO)) {
            Properties propiedades = new Properties();
            propiedades.load(entrada);
            return "claro".equals(propiedades.getProperty(CLAVE));
        } catch (IOException excepcion) {
            return false; // sin archivo todavía: tema oscuro por defecto
        }
    }

    private static void guardarPreferencia() {
        try {
            Files.createDirectories(ARCHIVO.getParent());
            Properties propiedades = new Properties();
            propiedades.setProperty(CLAVE, claro ? "claro" : "oscuro");
            try (OutputStream salida = Files.newOutputStream(ARCHIVO)) {
                propiedades.store(salida, "Preferencias de Allten");
            }
        } catch (IOException excepcion) {
            System.err.println("No se pudo guardar el tema: " + excepcion.getMessage());
        }
    }
}