package co.edu.poli.allten.database;

import co.edu.poli.allten.model.ModoJuego;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Consulta el ranking de partidas completadas. */
public class RankingDAO {

    private final ConexionBaseDatos conexion;

    public RankingDAO(ConexionBaseDatos conexion) {
        this.conexion = Objects.requireNonNull(conexion, "La conexión es obligatoria.");
    }

    /**
     * Devuelve las mejores partidas completadas del modo indicado, ordenadas
     * por tiempo y luego por cantidad de intentos.
     */
    public List<ResultadoRanking> obtenerRanking(int limite, ModoJuego modo) throws SQLException {
        if (limite <= 0) {
            throw new IllegalArgumentException("El límite del ranking debe ser mayor que cero.");
        }
        Objects.requireNonNull(modo, "El modo de juego es obligatorio.");

        String sql = """
                SELECT j.apodo, p.tiempo_total, p.fecha, p.intentos, p.modo
                FROM partida p
                JOIN jugador j ON j.id = p.jugador_id
                WHERE p.estado = 'COMPLETADO' AND p.modo = ?
                ORDER BY p.tiempo_total ASC, p.intentos ASC, p.fecha ASC
                LIMIT ?
                """;

        List<ResultadoRanking> resultados = new ArrayList<>();
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(sql)) {
            sentencia.setString(1, modo.name());
            sentencia.setInt(2, limite);
            try (ResultSet filas = sentencia.executeQuery()) {
                while (filas.next()) {
                    resultados.add(new ResultadoRanking(
                            filas.getString("apodo"),
                            filas.getLong("tiempo_total"),
                            filas.getTimestamp("fecha").toLocalDateTime(),
                            filas.getInt("intentos"),
                            ModoJuego.valueOf(filas.getString("modo"))));
                }
            }
        }
        return resultados;
    }
}
