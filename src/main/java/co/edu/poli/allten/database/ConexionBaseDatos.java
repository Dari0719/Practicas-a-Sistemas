package co.edu.poli.allten.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBaseDatos {
    private static final String URL = "jdbc:mysql://localhost:3306/allten";
    private static final String VARIABLE_USUARIO = "ALLTEN_DB_USER";
    private static final String VARIABLE_CONTRASENA = "ALLTEN_DB_PASSWORD";

    public Connection obtenerConexion() throws SQLException {
        String usuario = variableRequerida(VARIABLE_USUARIO);
        String contrasena = variableRequerida(VARIABLE_CONTRASENA);
        return DriverManager.getConnection(URL, usuario, contrasena);
    }

    private String variableRequerida(String nombre) {
        String valor = System.getenv(nombre);
        if (valor == null || valor.isBlank()) throw new IllegalStateException("Configure la variable de entorno " + nombre + ".");
        return valor;
    }
}