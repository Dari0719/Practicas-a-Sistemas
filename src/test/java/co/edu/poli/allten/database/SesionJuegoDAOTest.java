package co.edu.poli.allten.database;

import static org.junit.Assert.assertThrows;

import co.edu.poli.allten.model.Jugador;
import co.edu.poli.allten.model.ModoJuego;
import co.edu.poli.allten.model.SesionJuego;
import org.junit.Test;

public class SesionJuegoDAOTest {

    @Test
    public void guardar_rechazaUnaPartidaQueSigueEnCurso() {
        Jugador jugador = new Jugador(1, "jugador_prueba");
        SesionJuego sesion = new SesionJuego(jugador, ModoJuego.NORMAL);
        sesion.iniciarSesion();
        SesionJuegoDAO sesionJuegoDAO = new SesionJuegoDAO(new ConexionBaseDatos());

        assertThrows(IllegalStateException.class, () -> sesionJuegoDAO.guardar(sesion));
    }
}
