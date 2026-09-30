package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/** Genera grupos de cuatro números que permiten resolver todos los objetivos del 1 al 10. */
public class GeneradorNumeros {

    private static final int MAX_INTENTOS = 100;
    private final Random ALEATORIO = new Random();
    private final BuscadorSoluciones buscador = new BuscadorSoluciones();

    /**
     * Intenta generar cuatro números que tengan solución para cada objetivo del 1 al 10.
     * Devuelve una lista vacía si no encuentra una combinación dentro del límite de intentos.
     */
    public List<Integer> generar(ModoJuego modo) {
        if (modo == null) {
            return new ArrayList<>();
        }

        for (int intento = 0; intento < MAX_INTENTOS; intento++) {
            List<Integer> numeros = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                int numero = ALEATORIO.nextInt(9) + 1;
                numeros.add(numero);
            }

            if (buscador.tieneSolucionTodosObjetivos(numeros, modo)) {
                return numeros;
            }
        }

        return new ArrayList<>();
    }
}
