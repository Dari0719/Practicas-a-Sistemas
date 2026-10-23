package co.edu.poli.allten.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class ModeloJuegoTest {

    @Test
    public void buscarSoluciones_retornaResultadosParaObjetivoValido() {
        BuscadorSoluciones buscador = new BuscadorSoluciones();
        List<Integer> numeros = Arrays.asList(5, 2, 3, 4);

        List<String> soluciones = buscador.buscarSoluciones(numeros, 10, ModoJuego.NORMAL);

        assertFalse(soluciones.isEmpty());
        ValidadorExpresion validador = new ValidadorExpresion();
        assertTrue(soluciones.stream().allMatch(solucion ->
                validador.validarExpresion(solucion, 10, numeros, ModoJuego.NORMAL)
                        == ResultadoValidacion.VALIDO));
    }

    @Test
    public void buscarSoluciones_rechazaListaDeNumerosInvalidos() {
        BuscadorSoluciones buscador = new BuscadorSoluciones();

        assertThrows(
                IllegalArgumentException.class,
                () -> buscador.buscarSoluciones(Arrays.asList(1, 2, 3), 10, ModoJuego.NORMAL));
    }

    @Test
    public void buscarSoluciones_hardcoreEncuentraExpresionesAvanzadasParaTodosLosObjetivos() {
        BuscadorSoluciones buscador = new BuscadorSoluciones();
        ValidadorExpresion validador = new ValidadorExpresion();
        List<Integer> numeros = Arrays.asList(1, 2, 3, 4);

        assertTrue(buscador.tieneSolucionTodosLosObjetivos(numeros, ModoJuego.HARDCORE));
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            int numeroObjetivo = objetivo;
            List<String> soluciones =
                    buscador.buscarSoluciones(numeros, numeroObjetivo, ModoJuego.HARDCORE);

            assertFalse(
                    "Debe existir solución para el objetivo " + numeroObjetivo,
                    soluciones.isEmpty());
            for (String solucion : soluciones) {
                assertEquals(
                        "Expresión Hardcore inválida para el objetivo " + numeroObjetivo
                                + ": " + solucion,
                        ResultadoValidacion.VALIDO,
                        validador.validarExpresion(
                                solucion, numeroObjetivo, numeros, ModoJuego.HARDCORE));
            }
        }
    }

    @Test
    public void buscarSoluciones_rechazaModoNuloYNumerosFueraDelRango() {
        BuscadorSoluciones buscador = new BuscadorSoluciones();

        assertThrows(
                NullPointerException.class,
                () -> buscador.buscarSoluciones(Arrays.asList(1, 2, 3, 4), 10, null));
        assertThrows(
                IllegalArgumentException.class,
                () -> buscador.buscarSoluciones(Arrays.asList(0, 2, 3, 4), 10, ModoJuego.NORMAL));
        assertThrows(
                IllegalArgumentException.class,
                () -> buscador.buscarSoluciones(Arrays.asList(1, 2, null, 4), 10, ModoJuego.NORMAL));
    }

    @Test
    public void existeSolucion_identificaObjetivosAlcanzablesYNoAlcanzables() {
        BuscadorSoluciones buscador = new BuscadorSoluciones();
        List<Integer> numeros = Arrays.asList(1, 2, 3, 4);

        assertTrue(buscador.existeSolucion(numeros, 10, ModoJuego.NORMAL));
        assertFalse(buscador.existeSolucion(numeros, 1000, ModoJuego.NORMAL));
    }

    @Test
    public void generar_retornaCuatroNumerosEntreUnoY9() {
        GeneradorNumeros generador = new GeneradorNumeros();

        List<Integer> numeros = generador.generar(ModoJuego.NORMAL);

        assertEquals(4, numeros.size());
        assertTrue(numeros.stream().allMatch(numero -> numero >= 1 && numero <= 9));
    }

    @Test
    public void generar_retornaListaInmutableYRechazaModoNulo() {
        GeneradorNumeros generador = new GeneradorNumeros();
        List<Integer> numeros = generador.generar(ModoJuego.NORMAL);

        assertThrows(UnsupportedOperationException.class, () -> numeros.add(5));
        assertThrows(NullPointerException.class, () -> generador.generar(null));
    }

    @Test
    public void tieneSolucionTodosLosObjetivos_reconoceUnConjuntoSolucionable() {
        GeneradorNumeros generador = new GeneradorNumeros();
        BuscadorSoluciones buscador = new BuscadorSoluciones();

        List<Integer> numeros = generador.generar(ModoJuego.NORMAL);

        assertTrue(buscador.tieneSolucionTodosLosObjetivos(numeros, ModoJuego.NORMAL));
    }

    @Test
    public void generarHardcore_retornaNumerosQuePermitenResolverLosDiezObjetivos() {
        List<Integer> numeros = new GeneradorNumeros().generar(ModoJuego.HARDCORE);

        assertEquals(4, numeros.size());
        assertTrue(new BuscadorSoluciones()
                .tieneSolucionTodosLosObjetivos(numeros, ModoJuego.HARDCORE));
    }
}
