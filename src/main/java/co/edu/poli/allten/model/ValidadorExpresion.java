package co.edu.poli.allten.model;

import java.util.ArrayList;
import java.util.List;

public class ValidadorExpresion {

    public ResultadoValidacion validarExpresion(
            String expresion,
            int numeroObjetivo,
            List<Integer> numerosDisponibles,
            ModoJuego modo) {

        if (expresion == null || expresion.isBlank() || modo == null) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        if (numerosDisponibles == null
                || numerosDisponibles.isEmpty()
                || numerosDisponibles.stream().anyMatch(numero -> numero == null)) {
            return ResultadoValidacion.NUMEROS_INVALIDOS;
        }

        String expresionSinEspacios = quitarEspacios(expresion);
        if (modo == ModoJuego.NORMAL
                && (expresionSinEspacios.indexOf('^') >= 0
                || expresionSinEspacios.indexOf('√') >= 0)) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        List<Integer> numerosUsados;
        try {
            numerosUsados = extraerNumeros(expresionSinEspacios);
        } catch (NumberFormatException e) {
            return ResultadoValidacion.NUMEROS_INVALIDOS;
        }

        if (!coincidenNumeros(numerosUsados, numerosDisponibles)) {
            return ResultadoValidacion.NUMEROS_INVALIDOS;
        }

        double resultado;
        try {
            resultado = calcularResultado(expresionSinEspacios);
        } catch (IllegalArgumentException | ArithmeticException e) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        if (!Double.isFinite(resultado)) {
            return ResultadoValidacion.OPERACION_INVALIDA;
        }

        if (Math.abs(resultado - numeroObjetivo) > 0.0000001) {
            return ResultadoValidacion.RESULTADO_INCORRECTO;
        }

        return ResultadoValidacion.VALIDO;
    }

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

    private List<Integer> extraerNumeros(String expresion) {
        List<Integer> numeros = new ArrayList<>();
        int indice = 0;

        while (indice < expresion.length()) {
            if (esDigito(expresion.charAt(indice))) {
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

    private boolean coincidenNumeros(
            List<Integer> numerosUsados,
            List<Integer> numerosDisponibles) {
        if (numerosUsados.size() != numerosDisponibles.size()) {
            return false;
        }

        List<Integer> disponibles = new ArrayList<>(numerosDisponibles);
        for (Integer numero : numerosUsados) {
            if (!disponibles.remove(numero)) {
                return false;
            }
        }
        return disponibles.isEmpty();
    }

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

    private boolean esDigito(char caracter) {
        return caracter >= '0' && caracter <= '9';
    }

    private static class AnalizadorExpresion {
        private final String expresion;
        private int indice;

        private AnalizadorExpresion(String expresion) {
            this.expresion = expresion;
        }

        private double calcular() {
            double resultado = parsearSuma();
            if (indice != expresion.length()) {
                throw new IllegalArgumentException("Hay caracteres inesperados");
            }
            return resultado;
        }

        private double parsearSuma() {
            double resultado = parsearProducto();
            while (haySiguiente('+') || haySiguiente('-')) {
                char operador = expresion.charAt(indice++);
                double siguiente = parsearProducto();
                resultado = operador == '+' ? resultado + siguiente : resultado - siguiente;
            }
            return resultado;
        }

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

        private double parsearPotencia() {
            double base = parsearRaiz();
            if (haySiguiente('^')) {
                indice++;
                return Math.pow(base, parsearUnario());
            }
            return base;
        }

        private double parsearRaiz() {
            if (haySiguiente('√')) {
                indice++;
                double radicando = parsearRaiz();
                if (radicando < 0) {
                    throw new ArithmeticException("No se puede calcular la raíz de un negativo");
                }
                return Math.sqrt(radicando);
            }
            return parsearPrimario();
        }

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

        private boolean haySiguiente(char caracter) {
            return indice < expresion.length() && expresion.charAt(indice) == caracter;
        }
    }
}
