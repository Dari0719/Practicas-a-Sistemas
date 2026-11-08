package co.edu.poli.allten.view;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicacion JavaFX de All Ten.
 *
 * Es la que JavaFX ejecuta al arrancar: prepara la ventana, aplica el tema
 * visual y muestra la pantalla de inicio. A partir de ahi, cada controlador se
 * encarga de cambiar a la pantalla siguiente (juego, resumen o ranking)
 * reemplazando la escena de esta misma ventana.
 *
 * @see GestorTema
 * @see Launcher
 */
public class App extends Application {

    /**
     * Se ejecuta cuando JavaFX esta listo para mostrar la interfaz. Inicializa
     * el gestor de temas con la ventana principal, carga la pantalla de inicio
     * desde su archivo FXML, le pone el titulo "Allten" a la ventana y la
     * muestra.
     *
     * @param stage ventana principal que entrega JavaFX
     * @throws IOException si no se puede cargar el archivo FXML de la pantalla
     *         de inicio
     */
    @Override
    public void start(Stage stage) throws IOException {
        GestorTema.inicializar(stage);
        Parent root = FXMLLoader.load(
                App.class.getResource("/co/edu/poli/allten/views/pantalla-inicio.fxml"));
        stage.setTitle("Allten");
        stage.setScene(new Scene(root));
        stage.show();
    }

    /**
     * Metodo principal de la clase. Llama a launch para arrancar JavaFX, que a
     * su vez ejecuta el metodo start.
     *
     * @param args argumentos de linea de comandos
     */
    public static void main(String[] args) {
        launch(args);
    }
}
