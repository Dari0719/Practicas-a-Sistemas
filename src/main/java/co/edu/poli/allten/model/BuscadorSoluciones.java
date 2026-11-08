package co.edu.poli.allten.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Busca todas las expresiones que, usando los cuatro números de la partida
 * exactamente una vez cada uno, producen un objetivo dado.
 *
 * Tiene dos usos en el juego:
 * - GeneradorNumeros lo usa para comprobar que un conjunto de números permite
 *   resolver los diez objetivos antes de dárselo al jugador.
 * - La ventana de pistas lo usa para mostrar soluciones posibles.
 *
 * Cómo funciona: usa programación dinámica sobre subconjuntos de números. Cada
 * subconjunto se representa con una máscara de bits (con 4 números hay 16
 * máscaras, de 0 a 15). Para cada subconjunto se guarda la lista de todos los
 * valores que se pueden formar con sus números y la expresión que los produce.
 * Los subconjuntos grandes se arman combinando dos subconjuntos más pequeños con
 * cada operación permitida. Al final, el subconjunto con los cuatro números
 * (máscara 15) contiene todas las expresiones completas posibles.
 *
 * Ejemplo de máscaras con los números [a, b, c, d]:
 *   0001 = solo a, 0010 = solo b, 0011 = a y b, 1111 = los cuatro.
 *
 * La clase no guarda estado entre llamadas: cada búsqueda construye su propia
 * tabla desde cero.
 */
public class BuscadorSoluciones {

    // Diferencia máxima para considerar que dos decimales son iguales. Evita
    // fallos por los errores diminutos de los cálculos con double. Es más
    // estricta (1e-9) que la del validador de expresiones (1e-7).
    private static final double TOLERANCIA = 1e-9;
    // Cantidad de números que tiene cada partida.
    private static final int CANTIDAD_NUMEROS = 4;
    // Rango válido de cada número: del 1 al 9.
    private static final int NUMERO_MINIMO = 1;
    private static final int NUMERO_MAXIMO = 9;

    /**
     * Nodo de un árbol de expresión. Representa un valor calculado y guarda cómo
     * se obtuvo, para poder reconstruir el texto de la expresión después.
     *
     * Hay tres tipos de nodo:
     * - Hoja: un número original. Su operador es '\0' y no tiene hijos.
     * - Operación binaria (+ - * / ^): tiene hijo izquierdo y derecho.
     * - Raíz cuadrada: su operador es '√', solo tiene hijo izquierdo y el
     *   derecho es null.
     *
     * valor                       resultado numérico de esta expresión
     * numero                      el número original (solo tiene sentido en las
     *                             hojas; en los demás nodos vale 0)
     * operador                    operación que produjo el nodo
     * izquierda, derecha          subexpresiones que se combinaron
     * cantidadRaices              cuántas raíces cuadradas hay en toda la
     *                             expresión (el juego permite máximo una)
     * operacionAvanzadaNoTrivial  true si la expresión contiene una potencia o
     *                             raíz que realmente cuenta como avanzada (ver
     *                             las reglas del modo HARDCORE)
     *
     * Es un record de Java: una clase de datos inmutable con los campos de
     * arriba, a la que Java le genera automáticamente constructor y accesores
     * como valor() u operador().
     */
    private record Nodo(
            double valor,
            int numero,
            char operador,
            Nodo izquierda,
            Nodo derecha,
            int cantidadRaices,
            boolean operacionAvanzadaNoTrivial) { }

    /**
     * Devuelve todas las expresiones distintas que producen el objetivo usando
     * los cuatro números exactamente una vez.
     *
     * Cada expresión viene totalmente entre paréntesis, por ejemplo
     * "((8 + 2) / (1 + 4))". En modo HARDCORE solo se devuelven las que incluyen
     * una operación avanzada no trivial. No se repiten expresiones idénticas,
     * pero sí pueden aparecer expresiones diferentes que dan el mismo valor.
     *
     * @param numeros  los cuatro números de la partida, cada uno de 1 a 9
     * @param objetivo valor que deben producir las expresiones
     * @param modo     modo de juego, que define las operaciones permitidas
     * @return lista de expresiones en texto; vacía si no hay solución
     * @throws NullPointerException     si la lista de números o el modo son nulos
     * @throws IllegalArgumentException si no son exactamente cuatro números o
     *                                  alguno está fuera del rango de 1 a 9
     */
    public List<String> buscarSoluciones(List<Integer> numeros, int objetivo, ModoJuego modo) {
        validarEntrada(numeros, modo);
        return filtrarSoluciones(construirTabla(numeros, modo), objetivo, modo);
    }
    /**
     * Recorre las expresiones completas (las que usan los cuatro números) y se
     * queda con las que dan el objetivo, convertidas a texto. En HARDCORE además
     * descarta las que no tienen una operación avanzada no trivial.
     *
     * @param tabla    tabla con las expresiones de cada subconjunto de números
     * @param objetivo valor buscado
     * @param modo     modo de juego
     * @return expresiones solución sin repetir, en el orden en que se encontraron
     */
    private List<String> filtrarSoluciones(List<List<Nodo>> tabla, int objetivo, ModoJuego modo) {
        // LinkedHashSet elimina textos repetidos conservando el orden de aparición.
        Set<String> soluciones = new LinkedHashSet<>();
        // (1 << 4) - 1 = 15 = 1111 en binario: el subconjunto con los cuatro números.
        for (Nodo raiz : tabla.get((1 << CANTIDAD_NUMEROS) - 1)) {
            if (Math.abs(raiz.valor() - objetivo) > TOLERANCIA
                    || (modo == ModoJuego.HARDCORE && !raiz.operacionAvanzadaNoTrivial())) {
                continue;
            }
            soluciones.add(formatear(raiz));
        }
        return new ArrayList<>(soluciones);
    }

    /**
     * Comprueba que la entrada sea válida antes de buscar.
     *
     * @throws NullPointerException     si la lista o el modo son nulos
     * @throws IllegalArgumentException si no hay exactamente cuatro números o
     *                                  alguno es nulo o está fuera de 1 a 9
     */
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

    /**
     * Construye la tabla de programación dinámica: para cada subconjunto de los
     * cuatro números (identificado por su máscara de bits), la lista de todos
     * los nodos (valor y expresión) que se pueden formar con él.
     *
     * Pasos:
     * 1. Crear una lista vacía por cada máscara (16 en total).
     * 2. Poner cada número solo en su subconjunto de un elemento (hojas).
     * 3. Recorrer las máscaras de menor a mayor. Las que tienen al menos dos
     *    números se arman dividiéndolas en dos partes disjuntas y combinando
     *    cada expresión de una parte con cada expresión de la otra.
     * 4. En HARDCORE, aplicar además la raíz cuadrada a los resultados.
     *
     * Como una máscara siempre es mayor que sus subconjuntos, recorrer en orden
     * ascendente garantiza que las partes ya estén calculadas al combinarlas.
     *
     * @return la tabla indexada por máscara; la posición 15 tiene las
     *         expresiones que usan los cuatro números
     */
    private List<List<Nodo>> construirTabla(List<Integer> numeros, ModoJuego modo) {
        // 1 << 4 = 16 subconjuntos posibles (incluido el vacío).
        int total = 1 << CANTIDAD_NUMEROS;
        List<List<Nodo>> tabla = new ArrayList<>(total);
        for (int mascara = 0; mascara < total; mascara++) {
            tabla.add(new ArrayList<>());
        }
        // Caso base: el número i-ésimo vive en la máscara que solo tiene el bit i
        // encendido (1, 2, 4 y 8). Es una hoja sin operador ni hijos.
        for (int i = 0; i < CANTIDAD_NUMEROS; i++) {
            int valor = numeros.get(i);
            tabla.get(1 << i).add(new Nodo(valor, valor, '\0', null, null, 0, false));
        }

        for (int mascara = 1; mascara < total; mascara++) {
            // Los subconjuntos de un solo número ya están resueltos (hojas).
            if (Integer.bitCount(mascara) < 2) {
                continue;
            }
            List<Nodo> destino = tabla.get(mascara);
            // Recorre todas las formas de partir la máscara en dos partes. La
            // expresión (parteA - 1) & mascara va enumerando los subconjuntos de
            // mayor a menor. parteB es lo que queda (el complemento de parteA).
            for (int parteA = (mascara - 1) & mascara;
                    parteA > 0;
                    parteA = (parteA - 1) & mascara) {
                int parteB = mascara ^ parteA;
                // Dividir en (A, B) o en (B, A) es lo mismo. Para no repetir
                // trabajo, se procesa solo la división donde parteA es mayor. Las
                // operaciones que no son conmutativas se prueban en ambos órdenes
                // más abajo.
                if (parteA < parteB) {
                    continue;
                }
                for (Nodo x : tabla.get(parteA)) {
                    for (Nodo y : tabla.get(parteB)) {
                        // Suma y multiplicación dan igual en cualquier orden,
                        // así que se prueban una sola vez. Resta, división y
                        // potencia sí dependen del orden: se prueban x?y e y?x.
                        agregarOperacion(destino, x, y, '+', false);
                        agregarOperacion(destino, x, y, '-', false);
                        agregarOperacion(destino, y, x, '-', false);
                        agregarOperacion(destino, x, y, '*', false);
                        agregarOperacion(destino, x, y, '/', false);
                        agregarOperacion(destino, y, x, '/', false);
                        // La potencia solo existe en HARDCORE (el último
                        // parámetro la marca como operación avanzada).
                        if (modo == ModoJuego.HARDCORE) {
                            agregarOperacion(destino, x, y, '^', true);
                            agregarOperacion(destino, y, x, '^', true);
                        }
                    }
                }
            }

            // En HARDCORE se agrega la raíz cuadrada de cada resultado. Se trabaja
            // sobre una copia de la lista para no modificarla mientras se recorre.
            // Solo se saca raíz a expresiones que aún no tienen ninguna (máximo
            // una por expresión) y cuyo valor no es negativo.
            if (modo == ModoJuego.HARDCORE) {
                List<Nodo> expresionesSinRaiz = new ArrayList<>(destino);
                for (Nodo nodo : expresionesSinRaiz) {
                    if (nodo.cantidadRaices() == 0 && nodo.valor() >= 0) {
                        double valor = Math.sqrt(nodo.valor());
                        if (Double.isFinite(valor)) {
                            // La raíz cuenta como avanzada si el valor es mayor
                            // que 1 (la raíz de 0 o de 1 es trivial), o si la
                            // subexpresión ya traía una operación avanzada.
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

    /**
     * Combina dos nodos con un operador y, si el resultado es válido, lo agrega
     * a la lista destino. Descarta el resultado sin agregarlo si:
     * - la expresión tendría más de una raíz cuadrada;
     * - hay una división por cero (o por un valor casi cero);
     * - el valor no es finito (desbordamiento o potencia no real, como una base
     *   negativa con exponente fraccionario).
     *
     * @param destino   lista donde se guarda el nuevo nodo
     * @param izquierda operando de la izquierda
     * @param derecha   operando de la derecha
     * @param operador  uno de + - * / ^
     * @param avanzada  true si el operador es una potencia, para evaluar si es
     *                  no trivial
     * @throws IllegalArgumentException si el operador no es reconocido
     */
    private void agregarOperacion(
            List<Nodo> destino,
            Nodo izquierda,
            Nodo derecha,
            char operador,
            boolean avanzada) {
        // Las raíces de ambos lados se suman: el total no puede pasar de una.
        int cantidadRaices = izquierda.cantidadRaices() + derecha.cantidadRaices();
        if (cantidadRaices > 1) {
            return;
        }

        double valor = switch (operador) {
            case '+' -> izquierda.valor() + derecha.valor();
            case '-' -> izquierda.valor() - derecha.valor();
            case '*' -> izquierda.valor() * derecha.valor();
            // Dividir entre cero produce NaN, que se descarta más abajo.
            case '/' -> Math.abs(derecha.valor()) < TOLERANCIA
                    ? Double.NaN
                    : izquierda.valor() / derecha.valor();
            case '^' -> Math.pow(izquierda.valor(), derecha.valor());
            default -> throw new IllegalArgumentException("Operador no reconocido: " + operador);
        };

        // Descarta NaN e infinitos.
        if (!Double.isFinite(valor)) {
            return;
        }
        // Una potencia es "no trivial" si el exponente no es 0 ni 1, porque
        // elevar a 0 o a 1 no cambia nada relevante.
        boolean potenciaNoTrivial = avanzada
                && derecha.valor() != 0
                && derecha.valor() != 1;
        // El nuevo nodo hereda la marca de "avanzada" de cualquiera de sus partes.
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

    /**
     * Convierte un árbol de expresión en texto. Cada operación queda entre
     * paréntesis, por ejemplo "((2 + 3) * 4)", y la raíz se escribe como
     * "√(...)". Los operadores llevan espacios alrededor.
     *
     * Se usa una pila en vez de recursión: se apilan los trozos de texto y los
     * nodos en orden inverso al que deben escribirse, de modo que al sacarlos
     * salen en el orden correcto: "(", izquierda, operador, derecha, ")".
     *
     * @param raiz nodo raíz del árbol
     * @return la expresión como texto
     */
    private String formatear(Nodo raiz) {
        StringBuilder expresion = new StringBuilder();
        // La pila mezcla nodos pendientes de escribir y textos ya listos.
        Deque<Object> pila = new ArrayDeque<>();
        pila.push(raiz);
        while (!pila.isEmpty()) {
            Object elemento = pila.pop();
            // Un texto (paréntesis u operador) se escribe tal cual.
            if (elemento instanceof String texto) {
                expresion.append(texto);
                continue;
            }

            Nodo nodo = (Nodo) elemento;
            if (nodo.operador() == '\0') {
                // Hoja: se escribe el número original.
                expresion.append(nodo.numero());
            } else if (nodo.operador() == '√') {
                // Raíz: sale "√(" + hijo + ")". Se apila al revés.
                pila.push(")");
                pila.push(nodo.izquierda());
                pila.push("√(");
            } else {
                // Operación binaria: sale "(" + izq + " op " + der + ")".
                pila.push(")");
                pila.push(nodo.derecha());
                pila.push(" " + nodo.operador() + " ");
                pila.push(nodo.izquierda());
                pila.push("(");
            }
        }
        return expresion.toString();
    }

    /**
     * Indica si existe al menos una solución para el objetivo.
     *
     * @param numeros  los cuatro números de la partida
     * @param objetivo valor buscado
     * @param modo     modo de juego
     * @return true si hay al menos una expresión que lo produce
     */
    public boolean existeSolucion(List<Integer> numeros, int objetivo, ModoJuego modo) {
        return !buscarSoluciones(numeros, objetivo, modo).isEmpty();
    }

    // Revisa uno por uno los objetivos del juego y se detiene al encontrar uno irresoluble.
    /**
     * Indica si con estos números se pueden resolver los diez objetivos (del 1
     * al 10) en el modo dado. Es la comprobación que usa GeneradorNumeros para
     * garantizar que la partida sea resoluble.
     *
     * Construye la tabla una sola vez y la reutiliza para los diez objetivos. En
     * HARDCORE, cada objetivo debe poder lograrse con una expresión que incluya
     * una operación avanzada no trivial.
     *
     * @param numeros los cuatro números de la partida
     * @param modo    modo de juego
     * @return true solo si los diez objetivos tienen solución
     */
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
            // Si un objetivo no tiene ninguna solución, no hace falta seguir.
            if (!encontrado) {
                return false;
            }
        }
        return true;
    }
}
