package co.edu.poli.allten.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Busca soluciones para los objetivos usando los números disponibles.
 */
public class BuscadorSoluciones {

    /**
     * Busca todas las expresiones posibles que permiten alcanzar
     * el objetivo utilizando los cuatro números exactamente una vez.
     */
    public List<String> buscarSoluciones(List<Integer> numeros,int objetivo,ModoJuego modo) {

        Objects.requireNonNull(numeros, "La lista de números no puede ser vacia.");
        Objects.requireNonNull(modo, "El modo no puede estar vacio.");

        // Por ahora solo se implementa el modo NORMAL.
        if (modo != ModoJuego.NORMAL) {
            throw new UnsupportedOperationException(
                    "El modo " + modo + " todavía no está implementado.");
        }

        final int cantidadNumeros = 4;
        final int numeroMinimo = 1;
        final int numeroMaximo = 9;
        final double tolerancia = 1e-9;
        // Orden: x+y, x-y, x*y, x/y y luego y-x, y/x (los dos últimos invierten operandos).
        final String operadores = "+-*/-/";

        if (numeros.size() != cantidadNumeros) {
            throw new IllegalArgumentException(
                    "Se requieren exactamente " + cantidadNumeros + " números.");
        }
        for (Integer numero : numeros) {
            if (numero == null || numero < numeroMinimo || numero > numeroMaximo) {
                throw new IllegalArgumentException(
                        "Cada número debe estar entre " + numeroMinimo + " y " + numeroMaximo + ".");
            }
        }

        // Árbol de la expresión: hoja (operador == '\0') u operación entre dos nodos.
        // No guarda texto: la cadena solo se arma para las soluciones que coinciden.
        record Nodo(double valor, int numero, char operador, Nodo izquierda, Nodo derecha) { }

        // tabla.get(mascara) = todos los resultados posibles usando exactamente
        // los números cuyos bits están encendidos en la máscara.
        final int total = 1 << cantidadNumeros;
        List<List<Nodo>> tabla = new ArrayList<>(total);
        for (int mascara = 0; mascara < total; mascara++) {
            tabla.add(new ArrayList<>());
        }
        for (int i = 0; i < cantidadNumeros; i++) {
            int valor = numeros.get(i);
            tabla.get(1 << i).add(new Nodo(valor, valor, '\0', null, null));
        }

        // Combina cada subconjunto con todas sus divisiones en dos partes.
        for (int mascara = 1; mascara < total; mascara++) {
            if (Integer.bitCount(mascara) < 2) {
                continue;
            }
            List<Nodo> destino = tabla.get(mascara);

            for (int parteA = (mascara - 1) & mascara; parteA > 0; parteA = (parteA - 1) & mascara) {
                int parteB = mascara ^ parteA;
                if (parteA < parteB) {
                    continue; // cada división se procesa una sola vez
                }
                for (Nodo x : tabla.get(parteA)) {
                    for (Nodo y : tabla.get(parteB)) {
                        for (int k = 0; k < operadores.length(); k++) {
                            char operador = operadores.charAt(k);
                            Nodo izquierda = k >= 4 ? y : x;
                            Nodo derecha = k >= 4 ? x : y;

                            double valor = switch (operador) {
                                case '+' -> izquierda.valor() + derecha.valor();
                                case '-' -> izquierda.valor() - derecha.valor();
                                case '*' -> izquierda.valor() * derecha.valor();
                                default -> Math.abs(derecha.valor()) < tolerancia
                                        ? Double.NaN
                                        : izquierda.valor() / derecha.valor();
                            };

                            if (Double.isFinite(valor)) {
                                destino.add(new Nodo(valor, 0, operador, izquierda, derecha));
                            }
                        }
                    }
                }
            }
        }

        // Solo se construye el texto de las expresiones que alcanzan el objetivo.
        Set<String> soluciones = new LinkedHashSet<>();
        for (Nodo raiz : tabla.get(total - 1)) {
            if (Math.abs(raiz.valor() - objetivo) >= tolerancia) {
                continue;
            }

            StringBuilder expresion = new StringBuilder();
            Deque<Object> pila = new ArrayDeque<>();
            pila.push(raiz);

            while (!pila.isEmpty()) {
                Object elemento = pila.pop();
                if (elemento instanceof String texto) {
                    expresion.append(texto);
                } else {
                    Nodo nodo = (Nodo) elemento;
                    if (nodo.operador() == '\0') {
                        expresion.append(nodo.numero());
                    } else {
                        pila.push(")");
                        pila.push(nodo.derecha());
                        pila.push(" " + nodo.operador() + " ");
                        pila.push(nodo.izquierda());
                        pila.push("(");
                    }
                }
            }

            // Se quitan los paréntesis externos redundantes.
            soluciones.add(expresion.substring(1, expresion.length() - 1));
        }

        return new ArrayList<>(soluciones);
    }

    /**
     * Verifica si existe al menos una solución para un objetivo.
     */
    public boolean existeSolucion(
            List<Integer> numeros,
            int objetivo,
            ModoJuego modo) {

        return !buscarSoluciones(numeros, objetivo, modo).isEmpty();
    }

    /**
     * Verifica si los números permiten resolver todos los objetivos
     * del 1 al 10.
     */
    public boolean tieneSolucionTodosLosObjetivos(
            List<Integer> numeros,
            ModoJuego modo) {

        final int objetivoMinimo = 1;
        final int objetivoMaximo = 10;

        for (int objetivo = objetivoMinimo; objetivo <= objetivoMaximo; objetivo++) {
            if (!existeSolucion(numeros, objetivo, modo)) {
                return false;
            }
        }

        return true;
    }
}