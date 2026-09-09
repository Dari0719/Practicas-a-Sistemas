package co.edu.poli.allten.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.Test;

public class DatabaseConnectionTest {

    @Test
    public void shouldConnectToAlltenDatabase() throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            assertNotNull(connection);
            assertTrue(connection.isValid(2));
            assertEquals("allten", connection.getCatalog());
        }
    }
}