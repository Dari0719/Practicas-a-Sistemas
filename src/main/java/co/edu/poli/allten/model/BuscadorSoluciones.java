package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Busca soluciones para los objetivos usando los números disponibles.
 */
public class BuscadorSoluciones {

    private final ValidadorExpresion validador = new ValidadorExpresion();
    private static final String[] OPERADORES = {"+", "-", "*", "/"};

    /**
     * Genera todas las expresiones posibles con los números y verifica cuáles alcanzan el objetivo.
     */
    public List<String> buscarSoluciones(List<Integer> numeros, int objetivo, ModoJuego modo) {
        if (numeros == null || numeros.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> soluciones = new ArrayList<>();
        generarExpresiones(numeros, "", 0, objetivo, modo, soluciones);
        return soluciones;
    }

    private void generarExpresiones(List<Integer> numeros, String expresion, int index,
                                    int objetivo, ModoJuego modo, List<String> soluciones) {
        if (index == numeros.size()) {
            if (validador.validarExpresion(expresion, objetivo, numeros, modo) == ResultadoValidacion.VALIDO) {
                soluciones.add(expresion);
            }
            return;
        }

        int numero = numeros.get(index);

        if (expresion.isEmpty()) {
            generarExpresiones(numeros, String.valueOf(numero), index + 1, objetivo, modo, soluciones);
        } else {
            for (String op : OPERADORES) {
                generarExpresiones(numeros, expresion + " " + op + " " + numero, index + 1, objetivo, modo, soluciones);
            }
        }
    }

    /**
     * Verifica si existe al menos una solución para un objetivo.
     */
    public boolean existeSolucion(List<Integer> numeros, int objetivo, ModoJuego modo) {
        return !buscarSoluciones(numeros, objetivo, modo).isEmpty();
    }

    /**
     * Verifica si los números permiten resolver todos los objetivos del 1 al 10.
     */
    public boolean tieneSolucionTodosObjetivos(List<Integer> numeros, ModoJuego modo) {
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            if (!existeSolucion(numeros, objetivo, modo)) {
                return false;
            }
        }
        return true;
    }
}
