package co.edu.poli.allten.model;

import java.util.List;

public class Ronda {

    private int numeroObjetivo;
    private boolean resuelta;
    private String respuesta;

    public Ronda(int numeroObjetivo) {
        this.numeroObjetivo = numeroObjetivo;
        this.resuelta = false;
        this.respuesta = "";
    }

    public ResultadoValidacion resolver(
            String respuesta,
            List<Integer> numerosDisponibles,
            ModoJuego modo) {

        if (resuelta) {
            return ResultadoValidacion.OBJETIVO_YA_RESUELTO;
        }

        this.respuesta = respuesta;

        ValidadorExpresion validador = new ValidadorExpresion();

        ResultadoValidacion resultado = validador.validarExpresion(
                respuesta,
                numeroObjetivo,
                numerosDisponibles,
                modo
        );

        if (resultado == ResultadoValidacion.VALIDO) {
            this.resuelta = true;
        }

        return resultado;
    }

    public int getNumeroObjetivo() {
        return numeroObjetivo;
    }

    public boolean isResuelta() {
        return resuelta;
    }

    public String getRespuesta() {
        return respuesta;
    }
}