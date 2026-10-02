package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GeneradorNumeros {

    private static final int MAX_INTENTOS = 1000;
    private static final Random ALEATORIO = new Random();
    private final BuscadorSoluciones buscador = new BuscadorSoluciones();

    public List<Integer> generar(ModoJuego modo) {

        Objects.requireNonNull(modo, "El modo no puede estar vacio.");

        final int cantidadNumeros = 4;
        final int numeroMinimo = 1;
        final int numeroMaximo = 9;

        for (int intento = 0; intento < MAX_INTENTOS; intento++) {

            List<Integer> numeros = new ArrayList<>(cantidadNumeros);

            // Generar exactamente cuatro números entre 1 y 9.
            for (int i = 0; i < cantidadNumeros; i++) {
                numeros.add(ALEATORIO.nextInt(numeroMaximo - numeroMinimo + 1) + numeroMinimo);
            }

            // Verificar que los cuatro números permiten
            // resolver todos los objetivos del 1 al 10.
            if (buscador.tieneSolucionTodosLosObjetivos(numeros, modo)) {
                return List.copyOf(numeros);
            }
        }

        throw new IllegalStateException(
                "No se encontró un conjunto de números solucionable tras "
                        + MAX_INTENTOS + " intentos.");
    }
}