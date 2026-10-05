package co.edu.poli.allten.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;

public class ValidacionExpresionTest {

    private final ValidadorExpresion validador = new ValidadorExpresion();

    @Test
    public void validarExpresion_aceptaOperacionValidaYUsaTodosLosNumeros() {
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion(" (8 + 2) / 2 ", 5, Arrays.asList(8, 2, 2), ModoJuego.NORMAL));
    }

    @Test
    public void validarExpresion_aceptaLasCuatroOperacionesUsandoLosCuatroNumeros() {
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion("1 + 1 + 1 + 2", 5, Arrays.asList(1, 1, 1, 2), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion("9 - 2 - 1 - 1", 5, Arrays.asList(9, 2, 1, 1), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion("1 * 1 * 1 * 2", 2, Arrays.asList(1, 1, 1, 2), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion("(8 / 2) / (2 / 2)", 4, Arrays.asList(8, 2, 2, 2), ModoJuego.NORMAL));
    }

    @Test
    public void validarExpresion_aceptaResultadosIntermediosNegativosYFraccionarios() {
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion("1 - 8 + 6 + 6", 5, Arrays.asList(1, 8, 6, 6), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion("1 / 2 + 9 / 2", 5, Arrays.asList(1, 2, 9, 2), ModoJuego.NORMAL));
    }

    @Test
    public void validarExpresion_rechazaNumerosFaltantesRepetidosONoDisponibles() {
        assertEquals(
                ResultadoValidacion.NUMEROS_INVALIDOS,
                validador.validarExpresion("2 + 2", 4, Arrays.asList(2, 3), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.NUMEROS_INVALIDOS,
                validador.validarExpresion("2 + 2", 4, Arrays.asList(2, 2, 3), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.NUMEROS_INVALIDOS,
                validador.validarExpresion(
                        "1 + 1 + 1 + 2", 5, Arrays.asList(1, 1, 1, 3), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.NUMEROS_INVALIDOS,
                validador.validarExpresion("2147483648 + 1", 0, Arrays.asList(1, 2), ModoJuego.NORMAL));
    }

    @Test
    public void validarExpresion_rechazaOperacionesProhibidasEnModoNormal() {
        assertEquals(
                ResultadoValidacion.OPERACION_INVALIDA,
                validador.validarExpresion("2 ^ 3", 8, Arrays.asList(2, 3), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.OPERACION_INVALIDA,
                validador.validarExpresion("√9", 3, Arrays.asList(9), ModoJuego.NORMAL));
    }

    @Test
    public void validarExpresion_rechazaModoNuloSintaxisIncorrectaYDivisionPorCero() {
        assertEquals(
                ResultadoValidacion.OPERACION_INVALIDA,
                validador.validarExpresion("2 + 3", 5, Arrays.asList(2, 3), null));
        assertEquals(
                ResultadoValidacion.OPERACION_INVALIDA,
                validador.validarExpresion("2 +", 2, Arrays.asList(2), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.OPERACION_INVALIDA,
                validador.validarExpresion("1 / (2 - 2)", 0, Arrays.asList(1, 2, 2), ModoJuego.NORMAL));
    }

    @Test
    public void validarExpresion_rechazaResultadoNaN() {
        assertEquals(
                ResultadoValidacion.OPERACION_INVALIDA,
                validador.validarExpresion("(0 - 2) ^ (1 / 2)", 0, Arrays.asList(0, 2, 1, 2), ModoJuego.HARDCORE));
    }

    @Test
    public void validarExpresion_aceptaPotenciaAsociativaALaDerechaEnHardcore() {
        assertEquals(
                ResultadoValidacion.VALIDO,
                validador.validarExpresion("2 ^ 3 ^ 2", 512, Arrays.asList(2, 3, 2), ModoJuego.HARDCORE));
    }

    @Test
    public void calcularResultado_respetaPrecedenciaSignosYParentesis() {
        assertEquals(512.0, validador.calcularResultado("2^3^2"), 0.0000001);
        assertEquals(-4.0, validador.calcularResultado("-2^2"), 0.0000001);
        assertEquals(3.0, validador.calcularResultado("√9"), 0.0000001);
        assertEquals(20.0, validador.calcularResultado("(2+3)*4"), 0.0000001);
    }

    @Test
    public void ronda_noPermiteResolverDosVecesYConservaLaRespuestaCorrecta() {
        Ronda ronda = new Ronda(5);

        assertEquals(
                ResultadoValidacion.VALIDO,
                ronda.resolver("2 + 3", Arrays.asList(2, 3), ModoJuego.NORMAL));
        assertEquals(
                ResultadoValidacion.OBJETIVO_YA_RESUELTO,
                ronda.resolver("4 + 1", Arrays.asList(4, 1), ModoJuego.NORMAL));
        assertEquals("2 + 3", ronda.getRespuesta());
        assertTrue(ronda.isResuelta());
    }

    @Test
    public void ronda_guardaLaUltimaRespuestaInclusoSiEsIncorrecta() {
        Ronda ronda = new Ronda(5);

        assertEquals(
                ResultadoValidacion.RESULTADO_INCORRECTO,
                ronda.resolver("2 + 4", Arrays.asList(2, 4), ModoJuego.NORMAL));
        assertEquals("2 + 4", ronda.getRespuesta());
        assertFalse(ronda.isResuelta());
    }
}
