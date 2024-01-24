package at.fhtw.sampleapp.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE;

    public Connection getConnection()
    {
        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/swen1db",
                    "postgres",
                    "nathaniel17");
        } catch (SQLException e) {
            throw new DataAccessException("Datenbankverbindungsaufbau nicht erfolgreich", e);
        }
    }

    public static void main(String[] args) {
        // Attempt to get a database connection
        Connection connection = DatabaseManager.INSTANCE.getConnection();

        // Check if the connection was successfully established
        if (connection != null) {
            System.out.println("Database connection established successfully!");
        } else {
            System.out.println("Failed to establish database connection.");
        }

        try {
            String sql = "SELECT * FROM users";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new DataAccessException("query nd erfolgreich", e);
        }

        // Close the connection when done (if not using a connection pool)
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
