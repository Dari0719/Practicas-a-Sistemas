package co.edu.poli.allten.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import co.edu.poli.allten.model.BuscadorSoluciones;
import co.edu.poli.allten.model.EstadoJuego;
import co.edu.poli.allten.model.Jugador;
import co.edu.poli.allten.model.ModoJuego;
import co.edu.poli.allten.model.ResultadoValidacion;
import co.edu.poli.allten.model.SesionJuego;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

public class DatabaseIntegrationTest {

    @Test
    public void guardaPartidaPersistenteParaVerverecho() throws SQLException {
        assumeTrue("Activa la prueba con -Dallten.db.integration=true",
                Boolean.getBoolean("allten.db.integration"));
        assumeTrue(System.getenv("ALLTEN_DB_USER") != null
                && !System.getenv("ALLTEN_DB_USER").isBlank()
                && System.getenv("ALLTEN_DB_PASSWORD") != null
                && !System.getenv("ALLTEN_DB_PASSWORD").isBlank());

        ConexionBaseDatos conexion = new ConexionBaseDatos();
        JugadorDAO jugadorDAO = new JugadorDAO(conexion);
        SesionJuegoDAO sesionJuegoDAO = new SesionJuegoDAO(conexion);
        String apodo = "manolo";

        Jugador jugador = jugadorDAO.buscarPorApodo(apodo);
        if (jugador == null) {
            jugador = new Jugador(apodo);
        }
        jugadorDAO.guardar(jugador);
        assertTrue("El jugador debe tener un ID de base de datos.", jugador.getId() > 0);

        SesionJuego sesion = new SesionJuego(jugador, ModoJuego.NORMAL);
        sesion.iniciarSesion();
        BuscadorSoluciones buscador = new BuscadorSoluciones();
        for (int objetivo = 1; objetivo <= 10; objetivo++) {
            List<String> soluciones = buscador.buscarSoluciones(
                    sesion.getNumerosDisponibles(), objetivo, ModoJuego.NORMAL);
            assertTrue("Debe existir solución para el objetivo " + objetivo, !soluciones.isEmpty());
            assertEquals(
                    ResultadoValidacion.VALIDO,
                    sesion.procesarRespuesta(soluciones.get(0)));
        }
        assertEquals(EstadoJuego.COMPLETADO, sesion.getEstado());

        sesionJuegoDAO.guardar(sesion);

        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement("""
                        SELECT j.apodo, p.tiempo_total, p.intentos, p.estado, p.modo, p.fecha
                        FROM partida p
                        JOIN jugador j ON j.id = p.jugador_id
                        WHERE p.jugador_id = ?
                        ORDER BY p.id DESC
                        LIMIT 1
                        """)) {
            sentencia.setInt(1, jugador.getId());
            try (ResultSet fila = sentencia.executeQuery()) {
                assertTrue("La partida debe haber quedado insertada en MySQL.", fila.next());
                assertEquals(apodo, fila.getString("apodo"));
                assertEquals(sesion.getTiempoTotal(), fila.getLong("tiempo_total"));
                assertEquals(10, fila.getInt("intentos"));
                assertEquals(EstadoJuego.COMPLETADO.name(), fila.getString("estado"));
                assertEquals(ModoJuego.NORMAL.name(), fila.getString("modo"));
            }
        }
    }

    @Test
    public void guardaJugadorPartidaYLaExponeEnRanking() throws SQLException {
        assumeTrue("Activa la prueba con -Dallten.db.integration=true",
                Boolean.getBoolean("allten.db.integration"));
        assumeTrue(System.getenv("ALLTEN_DB_USER") != null
                && !System.getenv("ALLTEN_DB_USER").isBlank()
                && System.getenv("ALLTEN_DB_PASSWORD") != null
                && !System.getenv("ALLTEN_DB_PASSWORD").isBlank());

        ConexionBaseDatos conexion = new ConexionBaseDatos();
        JugadorDAO jugadorDAO = new JugadorDAO(conexion);
        SesionJuegoDAO sesionJuegoDAO = new SesionJuegoDAO(conexion);
        RankingDAO rankingDAO = new RankingDAO(conexion);
        String apodo = "junit_" + UUID.randomUUID().toString().replace("-", "");
        int jugadorId = 0;

        try {
            try (Connection conn = conexion.obtenerConexion()) {
                assertTrue("La conexión con MySQL debe ser válida.", conn.isValid(2));
            }

            Jugador jugador = new Jugador(apodo);
            jugadorDAO.guardar(jugador);
            jugadorId = jugador.getId();
            assertTrue("El DAO debe asignar el ID generado por MySQL.", jugadorId > 0);

            var jugadorRecuperado = jugadorDAO.buscarPorApodo(apodo);
            assertNotNull("El jugador guardado debe poder buscarse por apodo.", jugadorRecuperado);
            assertEquals(jugadorId, jugadorRecuperado.getId());

            SesionJuego sesion = new SesionJuego(jugador, ModoJuego.NORMAL);
            sesion.iniciarSesion();
            BuscadorSoluciones buscador = new BuscadorSoluciones();
            for (int objetivo = 1; objetivo <= 10; objetivo++) {
                List<String> soluciones = buscador.buscarSoluciones(
                        sesion.getNumerosDisponibles(), objetivo, ModoJuego.NORMAL);
                assertTrue("El generador debe encontrar una solución para " + objetivo,
                        !soluciones.isEmpty());
                assertEquals(
                        ResultadoValidacion.VALIDO,
                        sesion.procesarRespuesta(soluciones.get(0)));
            }
            assertEquals(EstadoJuego.COMPLETADO, sesion.getEstado());

            sesionJuegoDAO.guardar(sesion);

            List<ResultadoRanking> ranking = rankingDAO.obtenerRanking(10000, ModoJuego.NORMAL);
            ResultadoRanking resultadoGuardado = ranking.stream()
                    .filter(resultado -> resultado.getApodo().equals(apodo))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError(
                            "La partida guardada debe aparecer en el ranking."));
            assertEquals(sesion.getTiempoTotal(), resultadoGuardado.getTiempoTotal());
            assertEquals(10, resultadoGuardado.getIntentos());
            assertEquals(ModoJuego.NORMAL, resultadoGuardado.getModo());

            try (Connection conn = conexion.obtenerConexion();
                    PreparedStatement sentencia = conn.prepareStatement(
                            "SELECT COUNT(*) FROM partida WHERE jugador_id = ? AND estado = ?")) {
                sentencia.setInt(1, jugadorId);
                sentencia.setString(2, "COMPLETADO");
                try (ResultSet filas = sentencia.executeQuery()) {
                    assertTrue(filas.next());
                    assertEquals(1, filas.getInt(1));
                }
            }
        } finally {
            if (jugadorId > 0) {
                try (Connection conn = conexion.obtenerConexion();
                        PreparedStatement borrarPartidas =
                                conn.prepareStatement("DELETE FROM partida WHERE jugador_id = ?");
                        PreparedStatement borrarJugador =
                                conn.prepareStatement("DELETE FROM jugador WHERE id = ?")) {
                    borrarPartidas.setInt(1, jugadorId);
                    borrarPartidas.executeUpdate();
                    borrarJugador.setInt(1, jugadorId);
                    borrarJugador.executeUpdate();
                }
            }
        }
    }

}
