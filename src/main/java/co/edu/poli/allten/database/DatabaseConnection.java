package co.edu.poli.allten.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Punto de entrada para la configuracion de la base de datos relacional.
 */
public class DatabaseConnection {

	private static final String URL = "jdbc:mysql://localhost:3306/allten";
	private static final String USER_ENVIRONMENT_VARIABLE = "ALLTEN_DB_USER";
	private static final String PASSWORD_ENVIRONMENT_VARIABLE = "ALLTEN_DB_PASSWORD";

	private DatabaseConnection() {
	}

	public static Connection getConnection() throws SQLException {
		String user = requiredEnvironmentVariable(USER_ENVIRONMENT_VARIABLE);
		String password = requiredEnvironmentVariable(PASSWORD_ENVIRONMENT_VARIABLE);
		return DriverManager.getConnection(URL, user, password);
	}

	private static String requiredEnvironmentVariable(String name) {
		String value = System.getenv(name);
		if (value == null || value.isBlank()) {
			throw new IllegalStateException(
					"Configure la variable de entorno " + name + " antes de conectar con MySQL.");
		}
		return value;
	}
}
