package co.edu.poli.allten.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;

public class SesionJuegoTest {

    @Test
    public void constructor_rechazaJugadorOModoNulos() {
        assertThrows(
                NullPointerException.class,
                () -> new SesionJuego(null, ModoJuego.NORMAL));
        assertThrows(
                NullPointerException.class,
                () -> new SesionJuego(new Jugador("Lili"), null));
    }

    @Test
    public void iniciarSesion_soloPuedeEjecutarseUnaVez() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);
        sesion.iniciarSesion();

        assertThrows(IllegalStateException.class, sesion::iniciarSesion);
    }

    @Test
    public void iniciarSesion_asignaJugadorGeneraNumerosYCreaDiezRondas() {
        Jugador jugador = new Jugador("  Liliana  ");
        SesionJuego sesion = new SesionJuego(jugador, ModoJuego.NORMAL);

        sesion.iniciarSesion();

        assertEquals(jugador, sesion.getJugador());
        assertEquals("Liliana", sesion.getJugador().getApodo());
        assertEquals(ModoJuego.NORMAL, sesion.getModo());
        assertEquals(4, sesion.getNumerosDisponibles().size());
        assertEquals(10, sesion.getRondas().size());
        assertEquals(EstadoJuego.EN_CURSO, sesion.getEstado());
        assertFalse(sesion.estaCompleta());
        assertTrue(sesion.getHoraInicio() > 0);
        assertTrue(sesion.getFecha() != null);
    }

    @Test
    public void iniciarSesion_exponeColeccionesInmutables() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);
        sesion.iniciarSesion();

        assertThrows(
                UnsupportedOperationException.class,
                () -> sesion.getNumerosDisponibles().add(5));
        assertThrows(
                UnsupportedOperationException.class,
                () -> sesion.getRondas().clear());
    }

    @Test
    public void finalizarSesion_rechazaUnaSesionQueNoHaIniciado() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);

        assertThrows(IllegalStateException.class, sesion::finalizarSesion);
    }

    @Test
    public void procesarRespuesta_asignaExpresionAlObjetivoYCuentaIntentos() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);
        sesion.iniciarSesion();
        List<Integer> numeros = sesion.getNumerosDisponibles();
        String solucionObjetivoUno =
                new BuscadorSoluciones().buscarSoluciones(numeros, 1, ModoJuego.NORMAL).get(0);

        assertEquals(
                ResultadoValidacion.VALIDO,
                sesion.procesarRespuesta(solucionObjetivoUno));
        assertTrue(sesion.getRondas().get(0).isResuelta());
        assertEquals(solucionObjetivoUno, sesion.getRondas().get(0).getRespuesta());
        assertEquals(1, sesion.getIntentos());

        assertEquals(
                ResultadoValidacion.OBJETIVO_YA_RESUELTO,
                sesion.procesarRespuesta(solucionObjetivoUno));
        assertEquals(2, sesion.getIntentos());
    }

    @Test
    public void procesarRespuesta_permiteResolverObjetivosEnDiferenteOrden() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);
        sesion.iniciarSesion();
        BuscadorSoluciones buscador = new BuscadorSoluciones();
        int[] ordenObjetivos = {7, 2, 10};

        for (int objetivo : ordenObjetivos) {
            String solucion = buscador.buscarSoluciones(
                    sesion.getNumerosDisponibles(),
                    objetivo,
                    ModoJuego.NORMAL).get(0);

            assertEquals(ResultadoValidacion.VALIDO, sesion.procesarRespuesta(solucion));
            assertTrue(sesion.getRondas().get(objetivo - 1).isResuelta());
            assertEquals(solucion, sesion.getRondas().get(objetivo - 1).getRespuesta());
        }

        assertEquals(3, sesion.getIntentos());
        assertFalse(sesion.estaCompleta());
        assertFalse(sesion.getRondas().get(0).isResuelta());
    }

    @Test
    public void procesarRespuesta_devuelveErrorDeSintaxisSinCompletarRonda() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);
        sesion.iniciarSesion();
        List<Integer> numeros = sesion.getNumerosDisponibles();
        String expresionIncompleta = String.join(
                "+",
                numeros.stream().map(String::valueOf).toList()) + "+";

        assertEquals(
                ResultadoValidacion.OPERACION_INVALIDA,
                sesion.procesarRespuesta(expresionIncompleta));
        assertFalse(sesion.getRondas().stream().anyMatch(Ronda::isResuelta));
        assertEquals(1, sesion.getIntentos());
    }

    @Test
    public void finalizarSesion_marcaComoAbandonadaSiQuedanRondasPendientes() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);
        sesion.iniciarSesion();

        sesion.finalizarSesion();

        assertEquals(EstadoJuego.ABANDONADO, sesion.getEstado());
        assertTrue(sesion.getTiempoTotal() >= 0);
        assertThrows(IllegalStateException.class, () -> sesion.procesarRespuesta("1+2"));
    }

    @Test
    public void procesarRespuesta_completaLaSesionAlResolverTodosLosObjetivos() {
        SesionJuego sesion = new SesionJuego(new Jugador("Lili"), ModoJuego.NORMAL);
        sesion.iniciarSesion();
        BuscadorSoluciones buscador = new BuscadorSoluciones();

        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            String solucion = buscador.buscarSoluciones(
                    sesion.getNumerosDisponibles(),
                    objetivo,
                    ModoJuego.NORMAL).get(0);
            assertEquals(ResultadoValidacion.VALIDO, sesion.procesarRespuesta(solucion));
        }

        assertTrue(sesion.estaCompleta());
        assertEquals(EstadoJuego.COMPLETADO, sesion.getEstado());
        assertEquals(10, sesion.getIntentos());
        assertTrue(sesion.getTiempoTotal() >= 0);
        assertEquals(
                ResultadoValidacion.OBJETIVO_YA_RESUELTO,
                sesion.procesarRespuesta(""));
    }
}
