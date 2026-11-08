package co.edu.poli.allten.view;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/** Guarda una captura de un nodo JavaFX como archivo PNG. */
public final class ExportadorPNG {

    private ExportadorPNG() {
    }

    /**
     * Abre un selector y guarda la captura. Devuelve false si el usuario cancela.
     */
    public static boolean guardar(Node contenido, Stage escenario, String nombreInicial)
            throws IOException {
        FileChooser selector = new FileChooser();
        selector.setTitle("Guardar imagen PNG");
        selector.setInitialFileName(nombreInicial);
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagen PNG", "*.png"));

        File archivo = selector.showSaveDialog(escenario);
        if (archivo == null) {
            return false;
        }
        if (!archivo.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".png")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".png");
        }

        WritableImage imagen = contenido.snapshot(new SnapshotParameters(), null);
        if (!ImageIO.write(SwingFXUtils.fromFXImage(imagen, null), "png", archivo)) {
            throw new IOException("No hay un escritor disponible para el formato PNG.");
        }
        return true;
    }
}
