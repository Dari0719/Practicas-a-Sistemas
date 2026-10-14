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
    }

    @Test
    public void buscarSoluciones_rechazaListaDeNumerosInvalidos() {
        BuscadorSoluciones buscador = new BuscadorSoluciones();

        assertThrows(
                IllegalArgumentException.class,
                () -> buscador.buscarSoluciones(Arrays.asList(1, 2, 3), 10, ModoJuego.NORMAL));
    }

    @Test
    public void buscarSoluciones_lanzaExcepcionCuandoModoNoEstaImplementado() {
        BuscadorSoluciones buscador = new BuscadorSoluciones();

        assertThrows(
                UnsupportedOperationException.class,
                () -> buscador.buscarSoluciones(Arrays.asList(1, 2, 3, 4), 10, ModoJuego.HARDCORE));
    }

    @Test
    public void generar_retornaCuatroNumerosEntreUnoY9() {
        GeneradorNumeros generador = new GeneradorNumeros();

        List<Integer> numeros = generador.generar(ModoJuego.NORMAL);

        assertEquals(4, numeros.size());
        assertTrue(numeros.stream().allMatch(numero -> numero >= 1 && numero <= 9));
    }

    @Test
    public void tieneSolucionTodosLosObjetivos_reconoceUnConjuntoSolucionable() {
        GeneradorNumeros generador = new GeneradorNumeros();
        BuscadorSoluciones buscador = new BuscadorSoluciones();

        List<Integer> numeros = generador.generar(ModoJuego.NORMAL);

        assertTrue(buscador.tieneSolucionTodosLosObjetivos(numeros, ModoJuego.NORMAL));
    }
}
