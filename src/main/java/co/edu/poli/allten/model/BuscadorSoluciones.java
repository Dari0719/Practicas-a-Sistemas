package co.edu.poli.allten.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BuscadorSoluciones {

    private static final double TOLERANCIA = 1e-9;
    private static final int CANTIDAD_NUMEROS = 4;
    private static final int NUMERO_MINIMO = 1;
    private static final int NUMERO_MAXIMO = 9;

    private record Nodo(
            double valor,
            int numero,
            char operador,
            Nodo izquierda,
            Nodo derecha,
            int cantidadRaices,
            boolean operacionAvanzadaNoTrivial) { }

    public List<String> buscarSoluciones(List<Integer> numeros, int objetivo, ModoJuego modo) {
        validarEntrada(numeros, modo);
        return filtrarSoluciones(construirTabla(numeros, modo), objetivo, modo);
    }
    private List<String> filtrarSoluciones(List<List<Nodo>> tabla, int objetivo, ModoJuego modo) {
        Set<String> soluciones = new LinkedHashSet<>();
        for (Nodo raiz : tabla.get((1 << CANTIDAD_NUMEROS) - 1)) {
            if (Math.abs(raiz.valor() - objetivo) > TOLERANCIA
                    || (modo == ModoJuego.HARDCORE && !raiz.operacionAvanzadaNoTrivial())) {
                continue;
            }
            soluciones.add(formatear(raiz));
        }
        return new ArrayList<>(soluciones);
    }

    private void validarEntrada(List<Integer> numeros, ModoJuego modo) {
        Objects.requireNonNull(numeros, "La lista de números no puede estar vacía.");
        Objects.requireNonNull(modo, "El modo no puede estar vacío.");

        if (numeros.size() != CANTIDAD_NUMEROS) {
            throw new IllegalArgumentException(
                    "Se requieren exactamente " + CANTIDAD_NUMEROS + " números.");
        }
        for (Integer numero : numeros) {
            if (numero == null || numero < NUMERO_MINIMO || numero > NUMERO_MAXIMO) {
                throw new IllegalArgumentException(
                        "Cada número debe estar entre " + NUMERO_MINIMO + " y " + NUMERO_MAXIMO + ".");
            }
        }
    }

    private List<List<Nodo>> construirTabla(List<Integer> numeros, ModoJuego modo) {
        int total = 1 << CANTIDAD_NUMEROS;
        List<List<Nodo>> tabla = new ArrayList<>(total);
        for (int mascara = 0; mascara < total; mascara++) {
            tabla.add(new ArrayList<>());
        }
        for (int i = 0; i < CANTIDAD_NUMEROS; i++) {
            int valor = numeros.get(i);
            tabla.get(1 << i).add(new Nodo(valor, valor, '\0', null, null, 0, false));
        }

        for (int mascara = 1; mascara < total; mascara++) {
            if (Integer.bitCount(mascara) < 2) {
                continue;
            }
            List<Nodo> destino = tabla.get(mascara);
            for (int parteA = (mascara - 1) & mascara;
                    parteA > 0;
                    parteA = (parteA - 1) & mascara) {
                int parteB = mascara ^ parteA;
                if (parteA < parteB) {
                    continue;
                }
                for (Nodo x : tabla.get(parteA)) {
                    for (Nodo y : tabla.get(parteB)) {
                        agregarOperacion(destino, x, y, '+', false);
                        agregarOperacion(destino, x, y, '-', false);
                        agregarOperacion(destino, y, x, '-', false);
                        agregarOperacion(destino, x, y, '*', false);
                        agregarOperacion(destino, x, y, '/', false);
                        agregarOperacion(destino, y, x, '/', false);
                        if (modo == ModoJuego.HARDCORE) {
                            agregarOperacion(destino, x, y, '^', true);
                            agregarOperacion(destino, y, x, '^', true);
                        }
                    }
                }
            }

            if (modo == ModoJuego.HARDCORE) {
                List<Nodo> expresionesSinRaiz = new ArrayList<>(destino);
                for (Nodo nodo : expresionesSinRaiz) {
                    if (nodo.cantidadRaices() == 0 && nodo.valor() >= 0) {
                        double valor = Math.sqrt(nodo.valor());
                        if (Double.isFinite(valor)) {
                            destino.add(new Nodo(
                                    valor,
                                    0,
                                    '√',
                                    nodo,
                                    null,
                                    1,
                                    nodo.operacionAvanzadaNoTrivial() || nodo.valor() > 1));
                        }
                    }
                }
            }
        }
        return tabla;
    }

    private void agregarOperacion(
            List<Nodo> destino,
            Nodo izquierda,
            Nodo derecha,
            char operador,
            boolean avanzada) {
        int cantidadRaices = izquierda.cantidadRaices() + derecha.cantidadRaices();
        if (cantidadRaices > 1) {
            return;
        }

        double valor = switch (operador) {
            case '+' -> izquierda.valor() + derecha.valor();
            case '-' -> izquierda.valor() - derecha.valor();
            case '*' -> izquierda.valor() * derecha.valor();
            case '/' -> Math.abs(derecha.valor()) < TOLERANCIA
                    ? Double.NaN
                    : izquierda.valor() / derecha.valor();
            case '^' -> Math.pow(izquierda.valor(), derecha.valor());
            default -> throw new IllegalArgumentException("Operador no reconocido: " + operador);
        };

        if (!Double.isFinite(valor)) {
            return;
        }
        boolean potenciaNoTrivial = avanzada
                && derecha.valor() != 0
                && derecha.valor() != 1;
        destino.add(new Nodo(
                valor,
                0,
                operador,
                izquierda,
                derecha,
                cantidadRaices,
                izquierda.operacionAvanzadaNoTrivial()
                        || derecha.operacionAvanzadaNoTrivial()
                        || potenciaNoTrivial));
    }

    private String formatear(Nodo raiz) {
        StringBuilder expresion = new StringBuilder();
        Deque<Object> pila = new ArrayDeque<>();
        pila.push(raiz);
        while (!pila.isEmpty()) {
            Object elemento = pila.pop();
            if (elemento instanceof String texto) {
                expresion.append(texto);
                continue;
            }

            Nodo nodo = (Nodo) elemento;
            if (nodo.operador() == '\0') {
                expresion.append(nodo.numero());
            } else if (nodo.operador() == '√') {
                pila.push(")");
                pila.push(nodo.izquierda());
                pila.push("√(");
            } else {
                pila.push(")");
                pila.push(nodo.derecha());
                pila.push(" " + nodo.operador() + " ");
                pila.push(nodo.izquierda());
                pila.push("(");
            }
        }
        return expresion.toString();
    }

    public boolean existeSolucion(List<Integer> numeros, int objetivo, ModoJuego modo) {
        return !buscarSoluciones(numeros, objetivo, modo).isEmpty();
    }

    // Revisa uno por uno los objetivos del juego y se detiene al encontrar uno irresoluble.
    public boolean tieneSolucionTodosLosObjetivos(List<Integer> numeros, ModoJuego modo) {
        validarEntrada(numeros, modo);
        List<List<Nodo>> tabla = construirTabla(numeros, modo);
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            boolean encontrado = false;
            for (Nodo raiz : tabla.get((1 << CANTIDAD_NUMEROS) - 1)) {
                if (Math.abs(raiz.valor() - objetivo) <= TOLERANCIA
                        && (modo == ModoJuego.NORMAL || raiz.operacionAvanzadaNoTrivial())) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                return false;
            }
        }
        return true;
    }
}
