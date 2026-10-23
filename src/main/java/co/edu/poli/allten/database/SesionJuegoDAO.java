package co.edu.poli.allten.database;

import co.edu.poli.allten.model.SesionJuego;
import co.edu.poli.allten.model.EstadoJuego;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;

/** Persiste el resultado y los metadatos de una sesión de juego. */
public class SesionJuegoDAO {

    private final ConexionBaseDatos conexion;

    public SesionJuegoDAO(ConexionBaseDatos conexion) {
        this.conexion = Objects.requireNonNull(conexion, "La conexión es obligatoria.");
    }

    /**
     * Guarda una sesión finalizada asociándola a un jugador previamente persistido.
     * Se conservan las partidas completadas y abandonadas; las que siguen en curso
     * no se persisten ni pueden aparecer en el ranking.
     */
    public void guardar(SesionJuego sesion) throws SQLException {
        Objects.requireNonNull(sesion, "La sesión es obligatoria.");
        if (sesion.getEstado() == null || sesion.getFecha() == null) {
            throw new IllegalStateException("Solo se puede guardar una sesión iniciada.");
        }
        if (sesion.getEstado() == EstadoJuego.EN_CURSO) {
            throw new IllegalStateException("No se puede guardar una partida que sigue en curso.");
        }
        if (sesion.getJugador().getId() <= 0) {
            throw new IllegalStateException(
                    "El jugador debe guardarse antes de guardar su partida.");
        }

        String sql = """
                INSERT INTO partida (jugador_id, tiempo_total, intentos, estado, modo, fecha)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(sql)) {
            sentencia.setInt(1, sesion.getJugador().getId());
            sentencia.setLong(2, sesion.getTiempoTotal());
            sentencia.setInt(3, sesion.getIntentos());
            sentencia.setString(4, sesion.getEstado().name());
            sentencia.setString(5, sesion.getModo().name());
            sentencia.setTimestamp(6, Timestamp.valueOf(sesion.getFecha()));
            sentencia.executeUpdate();
        }
    }
}
