package co.edu.poli.allten.view;

import javafx.application.Application;

/**
 * Punto de entrada alterno de la aplicacion.
 *
 * Esta clase no extiende Application: solo llama a Application.launch con la
 * clase App. Sirve para arrancar el juego desde una clase principal comun,
 * sin que JavaFX tenga que detectar la aplicacion por si mismo, lo que suele
 * ser necesario al ejecutar el proyecto empaquetado o desde algunos entornos de
 * desarrollo. Toda la logica de arranque de la interfaz esta en App.
 *
 * @see App
 */
public class Launcher {

    /**
     * Inicia la aplicacion JavaFX.
     *
     * @param args argumentos de linea de comandos, que se pasan tal cual a
     *             JavaFX
     */
    public static void main(String[] args){
        Application.launch(App.class, args);
    }
}
