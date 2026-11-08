package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Valida y calcula las expresiones matemáticas que escribe el jugador.
 *
 * Para que una expresión sea aceptada debe cumplir, en este orden: usar
 * exactamente los números disponibles, respetar las operaciones permitidas del
 * modo de juego, poder calcularse sin errores y dar como resultado el objetivo
 * pedido. El veredicto se entrega como un ResultadoValidacion.
 *
 * La clase no guarda estado: se puede crear una instancia nueva cada vez que se
 * necesite, como hacen Ronda y SesionJuego.
 *
 * Los operadores que reconoce son + - * / ^ y la raíz cuadrada (√), además de
 * paréntesis. No reconoce los símbolos × ni ÷, así que la interfaz debe
 * convertirlos antes de llamar a esta clase.
 */
public class ValidadorExpresion {

    /**
     * Comprueba si una expresión es válida para un objetivo y devuelve el
     * veredicto correspondiente.
     *
     * Los espacios se ignoran por completo, incluso entre dígitos: "2 3" se lee
     * como el número 23. La clase no exige que sean cuatro números; compara
     * contra la lista que reciba. Que sean cuatro lo garantiza GeneradorNumeros.
     *
     * @param expresion          texto escrito por el jugador, por ejemplo
     *                           "(8 + 2) / 2"
     * @param numeroObjetivo     resultado que la expresión debe producir
     * @param numerosDisponibles números que deben usarse, cada uno exactamente
     *                           una vez (pueden estar repetidos)
     * @param modo               modo de juego, que define las operaciones
     *                           permitidas
     * @return VALIDO si cumple todo; NUMEROS_INVALIDOS si los números usados no
     *         coinciden con los disponibles; OPERACION_INVALIDA si no se puede
     *         calcular o rompe las reglas del modo; RESULTADO_INCORRECTO si es
     *         correcta pero no da el objetivo
     */
    public ResultadoValidacion validarExpresion(
            String expresion,
            int numeroObjetivo,
            List<Integer> numerosDisponibles,
            ModoJuego modo) {

        // Paso 1: sin texto o sin modo no hay nada que validar.
        if (expresion == null || expresion.isBlank() || modo == null) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        // Paso 2: la lista de números disponibles debe existir y no tener nulos.
        if (numerosDisponibles == null
                || numerosDisponibles.isEmpty()
                || numerosDisponibles.stream().anyMatch(numero -> numero == null)) {
            return ResultadoValidacion.NUMEROS_INVALIDOS;
        }

        // Paso 3: se quitan los espacios y, en modo NORMAL, se rechaza de una vez
        // cualquier uso de potencia o raíz sin necesidad de analizar la expresión.
        String expresionSinEspacios = quitarEspacios(expresion);
        if (modo == ModoJuego.NORMAL
                && (expresionSinEspacios.indexOf('^') >= 0
                || expresionSinEspacios.indexOf('√') >= 0)) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        // Paso 4: se sacan los números escritos en la expresión. Falla con
        // NumberFormatException si alguno es demasiado grande para un int.
        List<Integer> numerosUsados;
        try {
            numerosUsados = extraerNumeros(expresionSinEspacios);
        } catch (NumberFormatException e) {
            return ResultadoValidacion.NUMEROS_INVALIDOS;
        }

        // Paso 5: los números usados deben ser exactamente los disponibles.
        // Esto se revisa antes de calcular, por eso un error de números tiene
        // prioridad sobre un error de sintaxis.
        if (!coincidenNumeros(numerosUsados, numerosDisponibles)) {
            return ResultadoValidacion.NUMEROS_INVALIDOS;
        }

        // Paso 6: se calcula la expresión. Cualquier error de sintaxis, división
        // por cero o raíz de un negativo se traduce en OPERACION_INVALIDA.
        double resultado;
        AnalizadorExpresion analizador =
                new AnalizadorExpresion(expresionSinEspacios);
        try {
            resultado = analizador.calcular();
        } catch (IllegalArgumentException | ArithmeticException e) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        // Paso 7: reglas extra del modo HARDCORE. Como máximo una raíz cuadrada y
        // al menos una operación avanzada no trivial. Esto solo se puede saber
        // después de analizar la expresión, porque el analizador va contando.
        if (modo == ModoJuego.HARDCORE
                && (analizador.getCantidadRaices() > 1
                || !analizador.tieneOperacionAvanzadaNoTrivial())) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        // Paso 8: se descartan resultados infinitos o que no son un número, por
        // ejemplo una potencia que se desborda o una base negativa con exponente
        // fraccionario.
        if (!Double.isFinite(resultado)) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        // Paso 9: se compara con el objetivo usando una tolerancia mínima, porque
        // los cálculos con decimales (double) pueden tener errores diminutos.
        if (Math.abs(resultado - numeroObjetivo) > 0.0000001) {
            return ResultadoValidacion.RESULTADO_INCORRECTO;
        }

        return ResultadoValidacion.VALIDO;
    }

    /**
     * Calcula el valor de una expresión sin comprobar números ni modo de juego.
     * SesionJuego lo usa para saber a qué objetivo corresponde una expresión, y
     * la pantalla de juego para mostrar qué objetivo se resolvió.
     *
     * A diferencia de validarExpresion, no devuelve un veredicto sino que lanza
     * excepciones si algo falla.
     *
     * @param expresion texto de la expresión a calcular
     * @return el valor numérico de la expresión
     * @throws IllegalArgumentException si el texto está vacío, tiene errores de
     *                                  sintaxis o el resultado no es finito
     * @throws ArithmeticException      si hay una división por cero o una raíz
     *                                  de un número negativo
     */
    public double calcularResultado(String expresion) {
        if (expresion == null || expresion.isBlank()) {
            throw new IllegalArgumentException("La expresión no puede estar vacía");
        }

        AnalizadorExpresion analizador =
                new AnalizadorExpresion(quitarEspacios(expresion));
        double resultado = analizador.calcular();
        if (!Double.isFinite(resultado)) {
            throw new IllegalArgumentException("La expresión no produce un resultado finito");
        }
        return resultado;
    }

    /**
     * Recorre la expresión y devuelve, en orden, todos los números enteros que
     * encuentra. Una secuencia de dígitos seguidos forma un solo número, por lo
     * que "12" es el número 12 y no los números 1 y 2. Los demás caracteres se
     * saltan.
     *
     * @param expresion expresión ya sin espacios
     * @return lista con los números encontrados
     * @throws NumberFormatException si algún número no cabe en un int
     */
    private List<Integer> extraerNumeros(String expresion) {
        List<Integer> numeros = new ArrayList<>();
        int indice = 0;

        while (indice < expresion.length()) {
            if (esDigito(expresion.charAt(indice))) {
                // Se avanza mientras haya dígitos para leer el número completo.
                int inicio = indice;
                while (indice < expresion.length() && esDigito(expresion.charAt(indice))) {
                    indice++;
                }
                numeros.add(Integer.parseInt(expresion.substring(inicio, indice)));
            } else {
                indice++;
            }
        }

        return numeros;
    }

    /**
     * Verifica que los números usados sean exactamente los disponibles, sin
     * importar el orden, y respetando las repeticiones. Si los disponibles son
     * 3, 3, 7, 9, la expresión debe usar dos treses, un siete y un nueve.
     *
     * @param numerosUsados      números encontrados en la expresión
     * @param numerosDisponibles números permitidos para la partida
     * @return true si ambas listas tienen los mismos elementos
     */
    private boolean coincidenNumeros(
            List<Integer> numerosUsados,
            List<Integer> numerosDisponibles) {
        // Si la cantidad es distinta, ya no pueden coincidir.
        if (numerosUsados.size() != numerosDisponibles.size()) {
            return false;
        }

        // Se trabaja sobre una copia para no modificar la lista original. Cada
        // número usado "gasta" uno de los disponibles; si no queda ninguno igual,
        // el número no estaba permitido o se usó de más.
        List<Integer> disponibles = new ArrayList<>(numerosDisponibles);
        for (Integer numero : numerosUsados) {
            if (!disponibles.remove(numero)) {
                return false;
            }
        }
        return disponibles.isEmpty();
    }

    /**
     * Elimina todos los espacios en blanco del texto, incluidos tabuladores y
     * saltos de línea. Esto simplifica el análisis posterior.
     *
     * @param expresion texto original
     * @return el mismo texto sin caracteres de espacio
     */
    private String quitarEspacios(String expresion) {
        StringBuilder resultado = new StringBuilder(expresion.length());
        for (int i = 0; i < expresion.length(); i++) {
            char caracter = expresion.charAt(i);
            if (!Character.isWhitespace(caracter)) {
                resultado.append(caracter);
            }
        }
        return resultado.toString();
    }

    /**
     * Indica si un carácter es un dígito del 0 al 9. Solo acepta dígitos
     * ASCII, a diferencia de Character.isDigit que también acepta otros alfabetos.
     *
     * @param caracter carácter a comprobar
     * @return true si es un dígito del 0 al 9
     */
    private boolean esDigito(char caracter) {
        return caracter >= '0' && caracter <= '9';
    }

    /**
     * Analizador de expresiones por descenso recursivo. Lee el texto de
     * izquierda a derecha y calcula el resultado respetando la prioridad de las
     * operaciones. Cada método de "parsear" resuelve un nivel de prioridad y
     * llama al siguiente nivel, que es más fuerte.
     *
     * Niveles, del más débil al más fuerte:
     *   1. Suma y resta (+ -)
     *   2. Multiplicación y división (* /)
     *   3. Signo unario (+ o - delante de un valor)
     *   4. Potencia (^)
     *   5. Raíz cuadrada (√)
     *   6. Paréntesis y números
     *
     * Mientras calcula, también registra cuántas raíces aparecen y si hubo alguna
     * operación avanzada no trivial, datos que el modo HARDCORE necesita.
     *
     * Es privada y estática porque solo sirve a ValidadorExpresion y no necesita
     * acceder a sus datos. Un objeto de esta clase se usa para una sola expresión.
     */
    private static class AnalizadorExpresion {
        // Expresión a analizar, sin espacios.
        private final String expresion;
        // Posición del próximo carácter por leer. Avanza durante el análisis.
        private int indice;
        // Cuántas veces apareció el símbolo de raíz cuadrada.
        private int cantidadRaices;
        // Se activa si hay una potencia con exponente distinto de 0 y 1, o una
        // raíz de un valor mayor que 1. Elevar a 0 o 1 o sacar la raíz de 0 o 1
        // son operaciones triviales que no cuentan como avanzadas.
        private boolean operacionAvanzadaNoTrivial;

        private AnalizadorExpresion(String expresion) {
            this.expresion = expresion;
        }

        /**
         * Punto de entrada: calcula toda la expresión. Si después de calcular
         * quedan caracteres sin leer, la expresión está mal escrita, por ejemplo
         * "2)" o "2(3)".
         *
         * @return valor de la expresión
         * @throws IllegalArgumentException si sobran caracteres o hay errores de
         *                                  sintaxis
         */
        private double calcular() {
            double resultado = parsearSuma();
            if (indice != expresion.length()) {
                throw new IllegalArgumentException("Hay caracteres inesperados");
            }
            return resultado;
        }

        // Cantidad de raíces cuadradas encontradas. Solo es fiable después de
        // llamar a calcular.
        private int getCantidadRaices() {
            return cantidadRaices;
        }

        // Indica si hubo alguna operación avanzada no trivial. Solo es fiable
        // después de llamar a calcular.
        private boolean tieneOperacionAvanzadaNoTrivial() {
            return operacionAvanzadaNoTrivial;
        }

        /**
         * Nivel 1: suma y resta. Calcula un producto y, mientras siga habiendo
         * + o -, calcula el siguiente producto y lo combina de izquierda a
         * derecha. Así "10 - 3 - 2" da 5 y no 9.
         */
        private double parsearSuma() {
            double resultado = parsearProducto();
            while (haySiguiente('+') || haySiguiente('-')) {
                char operador = expresion.charAt(indice++);
                double siguiente = parsearProducto();
                resultado = operador == '+' ? resultado + siguiente : resultado - siguiente;
            }
            return resultado;
        }

        /**
         * Nivel 2: multiplicación y división, también de izquierda a derecha.
         * Lanza ArithmeticException si se intenta dividir entre cero. Dividir
         * entre un número que da exactamente cero, como (2 - 2), también falla.
         */
        private double parsearProducto() {
            double resultado = parsearUnario();
            while (haySiguiente('*') || haySiguiente('/')) {
                char operador = expresion.charAt(indice++);
                double siguiente = parsearUnario();
                if (operador == '/' && siguiente == 0.0) {
                    throw new ArithmeticException("No se puede dividir por cero");
                }
                resultado = operador == '*' ? resultado * siguiente : resultado / siguiente;
            }
            return resultado;
        }

        /**
         * Nivel 3: signo unario. Permite escribir +3 o -3, y varios seguidos
         * como --3. Como el signo es más débil que la potencia, -2^2 se lee como
         * -(2^2) y da -4, no 4.
         */
        private double parsearUnario() {
            if (haySiguiente('+')) {
                indice++;
                return parsearUnario();
            }
            if (haySiguiente('-')) {
                indice++;
                return -parsearUnario();
            }
            return parsearPotencia();
        }

        /**
         * Nivel 4: potencia. El exponente se lee con parsearUnario, de modo que
         * la potencia se agrupa de derecha a izquierda ("2^3^2" es 2^(3^2) = 512)
         * y el exponente puede llevar signo ("2^-1"). Si el exponente es distinto
         * de 0 y de 1, la operación se marca como avanzada no trivial.
         */
        private double parsearPotencia() {
            double base = parsearRaiz();
            if (haySiguiente('^')) {
                indice++;
                double exponente = parsearUnario();
                if (exponente != 0 && exponente != 1) {
                    operacionAvanzadaNoTrivial = true;
                }
                return Math.pow(base, exponente);
            }
            return base;
        }

        /**
         * Nivel 5: raíz cuadrada. El símbolo √ afecta solo al valor que tiene
         * pegado: √9 es 3, pero √4+5 se lee como (√4)+5. Para sacar la raíz de
         * una operación hay que usar paréntesis: √(4+5). Cuenta cada raíz y
         * lanza ArithmeticException si el valor es negativo. Si el valor es
         * mayor que 1, la operación se marca como avanzada no trivial.
         */
        private double parsearRaiz() {
            if (haySiguiente('√')) {
                indice++;
                cantidadRaices++;
                double radicando = parsearRaiz();
                if (radicando < 0) {
                    throw new ArithmeticException("No se puede calcular la raíz de un negativo");
                }
                if (radicando > 1) {
                    operacionAvanzadaNoTrivial = true;
                }
                return Math.sqrt(radicando);
            }
            return parsearPrimario();
        }

        /**
         * Nivel 6: el elemento más básico. Puede ser una expresión entre
         * paréntesis, que se vuelve a calcular desde el nivel 1, o un número
         * entero sin decimales. Lanza IllegalArgumentException si falta cerrar
         * un paréntesis o si en esa posición no hay un número.
         */
        private double parsearPrimario() {
            if (haySiguiente('(')) {
                indice++;
                double resultado = parsearSuma();
                if (!haySiguiente(')')) {
                    throw new IllegalArgumentException("Falta cerrar un paréntesis");
                }
                indice++;
                return resultado;
            }

            // Se leen todos los dígitos seguidos que formen el número.
            int inicio = indice;
            while (indice < expresion.length()
                    && expresion.charAt(indice) >= '0'
                    && expresion.charAt(indice) <= '9') {
                indice++;
            }
            if (inicio == indice) {
                throw new IllegalArgumentException("Se esperaba un número");
            }
            return Double.parseDouble(expresion.substring(inicio, indice));
        }

        // Permite revisar el carácter actual sin avanzar el índice.
        private boolean haySiguiente(char caracter) {
            return indice < expresion.length() && expresion.charAt(indice) == caracter;
        }
    }
}
