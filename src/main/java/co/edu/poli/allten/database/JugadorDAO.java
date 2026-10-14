package co.edu.poli.allten.database;

import co.edu.poli.allten.model.Jugador;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/** Acceso a datos de jugadores. */
public class JugadorDAO {

    private final ConexionBaseDatos conexion;

    public JugadorDAO(ConexionBaseDatos conexion) {
        this.conexion = Objects.requireNonNull(conexion, "La conexión es obligatoria.");
    }

    /**
     * Inserta al jugador o recupera el identificador del apodo ya registrado.
     * El identificador generado queda asignado al objeto recibido.
     */
    public void guardar(Jugador jugador) throws SQLException {
        Objects.requireNonNull(jugador, "El jugador es obligatorio.");
        String sql = """
                INSERT INTO jugador (apodo)
                VALUES (?)
                ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id)
                """;

        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia =
                        conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            sentencia.setString(1, jugador.getApodo());
            sentencia.executeUpdate();

            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new SQLException("MySQL no devolvió el identificador del jugador.");
                }
                jugador.setId(claves.getInt(1));
            }
        }
    }

    /**
     * Busca un jugador por su apodo único.
     *
     * @return el jugador encontrado o {@code null} si no existe
     */
    public Jugador buscarPorApodo(String apodo) throws SQLException {
        if (apodo == null || apodo.isBlank()) {
            throw new IllegalArgumentException("El apodo no puede estar vacío.");
        }

        String sql = "SELECT id, apodo FROM jugador WHERE apodo = ?";
        try (Connection conn = conexion.obtenerConexion();
                PreparedStatement sentencia = conn.prepareStatement(sql)) {
            sentencia.setString(1, apodo.trim());
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) {
                    return null;
                }
                return new Jugador(resultado.getInt("id"), resultado.getString("apodo"));
            }
        }
    }
}
