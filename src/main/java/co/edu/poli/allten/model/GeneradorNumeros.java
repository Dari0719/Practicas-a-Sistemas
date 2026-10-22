package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

// Genera los cuatro números que el jugador usará para resolver las rondas.
public class GeneradorNumeros {

    // Limita los intentos para no repetir la búsqueda indefinidamente.
    private static final int MAX_INTENTOS = 1000;
    // Se reutiliza este generador aleatorio en todas las partidas.
    private static final Random ALEATORIO = new Random();
    // Comprueba que los números elegidos sirvan para resolver todas las rondas.
    private final BuscadorSoluciones buscador = new BuscadorSoluciones();

    // Prueba conjuntos aleatorios de cuatro números del 1 al 9 y devuelve el
    // primero que permite resolver todos los objetivos. La lista devuelta es inmutable.
    public List<Integer> generar(ModoJuego modo) {

        Objects.requireNonNull(modo, "El modo no puede estar vacio.");

        final int cantidadNumeros = 4;
        final int numeroMinimo = 1;
        final int numeroMaximo = 9;

        for (int intento = 0; intento < MAX_INTENTOS; intento++) {

            List<Integer> numeros = new ArrayList<>(cantidadNumeros);

            for (int i = 0; i < cantidadNumeros; i++) {
                numeros.add(ALEATORIO.nextInt(numeroMaximo - numeroMinimo + 1) + numeroMinimo);
            }

            if (buscador.tieneSolucionTodosLosObjetivos(numeros, modo)) {
                return List.copyOf(numeros);
            }
        }

        throw new IllegalStateException(
                "No se encontró un conjunto de números solucionable tras "
                        + MAX_INTENTOS + " intentos.");
    }
}