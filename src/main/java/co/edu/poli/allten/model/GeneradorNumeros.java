package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Genera los cuatro números que el jugador usará para resolver las rondas.
 *
 * No basta con sortear cuatro números al azar, porque algunas combinaciones no
 * permiten obtener todos los objetivos del 1 al 10. Por eso esta clase sortea un
 * conjunto, le pregunta a BuscadorSoluciones si se pueden resolver los diez
 * objetivos y, si no, descarta el conjunto y sortea otro. Así se garantiza que
 * toda partida sea resoluble.
 *
 * SesionJuego la usa al iniciar una partida (iniciarSesion).
 */
public class GeneradorNumeros {

    // Limita los intentos para no repetir la búsqueda indefinidamente.
    // Es una medida de seguridad: si por algún motivo no existiera ninguna
    // combinación resoluble, el programa lanza un error en lugar de quedarse
    // en un ciclo infinito.
    private static final int MAX_INTENTOS = 1000;
    // Se reutiliza este generador aleatorio en todas las partidas.
    // Es estático para crearlo una sola vez, y no uno nuevo por cada partida.
    private static final Random ALEATORIO = new Random();
    // Comprueba que los números elegidos sirvan para resolver todas las rondas.
    // Es el mismo buscador que usan las pistas del juego.
    private final BuscadorSoluciones buscador = new BuscadorSoluciones();

    /**
     * Prueba conjuntos aleatorios de cuatro números del 1 al 9 y devuelve el
     * primero que permite resolver todos los objetivos. La lista devuelta es
     * inmutable.
     *
     * Los números pueden repetirse dentro del conjunto (por ejemplo 3, 3, 7, 9).
     * Cada conjunto candidato se evalúa con las reglas del modo recibido, de modo
     * que en modo HARDCORE el conjunto elegido es resoluble usando potencias y
     * raíces, y en modo NORMAL, solo con las cuatro operaciones básicas.
     *
     * @param modo modo de juego para el que se generan los números; no puede
     *             ser nulo
     * @return lista inmutable con los cuatro números generados
     * @throws NullPointerException si el modo es nulo
     * @throws IllegalStateException si tras el máximo de intentos no se encontró
     *                               un conjunto resoluble
     */
    public List<Integer> generar(ModoJuego modo) {

        // Sin modo no se puede saber qué operaciones están permitidas.
        Objects.requireNonNull(modo, "El modo no puede estar vacio.");

        // Reglas de la partida: cuatro números, cada uno entre 1 y 9.
        final int cantidadNumeros = 4;
        final int numeroMinimo = 1;
        final int numeroMaximo = 9;

        // Se repite el sorteo hasta encontrar un conjunto resoluble o agotar
        // los intentos permitidos.
        for (int intento = 0; intento < MAX_INTENTOS; intento++) {

            List<Integer> numeros = new ArrayList<>(cantidadNumeros);

            // Sortea cuatro números. nextInt(9) da valores de 0 a 8, y al sumar
            // numeroMinimo el rango queda de 1 a 9.
            for (int i = 0; i < cantidadNumeros; i++) {
                numeros.add(ALEATORIO.nextInt(numeroMaximo - numeroMinimo + 1) + numeroMinimo);
            }

            // Solo se acepta el conjunto si permite resolver los diez objetivos.
            // List.copyOf lo vuelve inmutable para que nadie lo modifique después.
            if (buscador.tieneSolucionTodosLosObjetivos(numeros, modo)) {
                return List.copyOf(numeros);
            }
        }

        // Solo se llega aquí si ningún sorteo fue resoluble en todos los intentos.
        throw new IllegalStateException(
                "No se encontró un conjunto de números solucionable tras "
                        + MAX_INTENTOS + " intentos.");
    }
}
