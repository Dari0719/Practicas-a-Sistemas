package co.edu.poli.allten.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Crea las conexiones JDBC a la base de datos MySQL del juego.
 *
 * Es la unica clase que sabe como llegar a la base de datos. Los DAO
 * (JugadorDAO, SesionJuegoDAO y RankingDAO) no la usan directamente: reciben
 * una Connection ya abierta, y quien las necesita la pide con
 * obtenerConexion.
 *
 * La direccion de la base de datos es fija (localhost, puerto 3306, esquema
 * allten). El usuario y la contrasena no estan escritos en el codigo: se leen
 * de las variables de entorno ALLTEN_DB_USER y ALLTEN_DB_PASSWORD, para no
 * guardar credenciales en el repositorio.
 *
 * Cada llamada a obtenerConexion abre una conexion nueva, y quien la pide es
 * responsable de cerrarla.
 */
public class ConexionBaseDatos {

    // Direccion JDBC de la base de datos: servidor local, puerto 3306 y esquema
    // allten.
    private static final String URL = "jdbc:mysql://localhost:3306/allten";

    // Nombre de la variable de entorno que contiene el usuario de MySQL.
    private static final String VARIABLE_USUARIO = "ALLTEN_DB_USER";

    // Nombre de la variable de entorno que contiene la contrasena de MySQL.
    private static final String VARIABLE_CONTRASENA = "ALLTEN_DB_PASSWORD";

    /**
     * Abre una conexion nueva a la base de datos usando el usuario y la
     * contrasena definidos en las variables de entorno.
     *
     * @return una conexion abierta; quien la reciba debe cerrarla
     * @throws SQLException si MySQL rechaza la conexion (servidor apagado,
     *         credenciales incorrectas, esquema inexistente, etc.)
     * @throws IllegalStateException si alguna de las dos variables de entorno
     *         no esta definida o esta vacia
     */
    public Connection obtenerConexion() throws SQLException {
        String usuario = variableRequerida(VARIABLE_USUARIO);
        String contrasena = variableRequerida(VARIABLE_CONTRASENA);
        return DriverManager.getConnection(URL, usuario, contrasena);
    }

    /**
     * Lee una variable de entorno obligatoria.
     *
     * @param nombre nombre de la variable de entorno que se quiere leer
     * @return el valor de la variable
     * @throws IllegalStateException si la variable no existe o esta en blanco;
     *         el mensaje indica cual variable hay que configurar
     */
    private String variableRequerida(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) throw new IllegalStateException("Configure la variable de entorno " + nombre + ".");
        return valor;
    }
}
