package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the connection to the MySQL database.
 * This is a utility class that all DAO classes will use.
 * We keep the connection details in one single place for easy maintenance.
 * This class is not meant to be instantiated (hence the private constructor).
 */
public class DatabaseConnection {

    // --- JDBC Connection Details ---
    // These are the "credentials" for our Java application to log into MySQL.

    /**
     * The JDBC driver class for MySQL.
     * We need to tell Java's DriverManager what "language" to speak.
     */
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    /**
     * The connection string (or URL) to our database.
     * It follows the format: jdbc:<driver>://<host>:<port>/<database_name>
     * 'blood_bank_db' is the database we created.
     */
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/blood_bank_db";

    /**
     * The MySQL user we created and gave permissions to.
     */
    private static final String DATABASE_USER = "JeanLucJava";

    /**
     * The password for our 'JeanLucJava' user.
     * !! YOU MUST CHANGE THIS TO SUIT YOUR DATABASE PASSWORD, THIS IS MINE BUT WON'T ACCESS YOURS UNLESS YOU MODIFY YOUR PASSWORD TO MATCH YOUR DB'S!!
     */
    private static final String DATABASE_PASSWORD = "StrongPass123!"; // <-- IMPORTANT!

    /**
     * Private constructor to prevent anyone from creating a
     * 'new DatabaseConnection()' object. This is a utility class,
     * not a data-holder, so it should never be instantiated.
     */
    private DatabaseConnection() {
        // This is intentionally left blank.
    }

    /**
     * This is the "gatekeeper" method.
     * Any DAO class will call 'DatabaseConnection.getConnection()'
     * to get a live connection to the database.
     *
     * @return A 'java.sql.Connection' object.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Step 1: Load the MySQL driver class.
            // This "registers" the driver with Java's DriverManager.
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException e) {
            // This is a "setup" error, not a "runtime" error.
            // It means the MySQL .jar file is missing from the project.
            System.err.println("CRITICAL ERROR: MySQL JDBC Driver not found.");
            e.printStackTrace();
            // We re-throw this as an SQLException so the calling method
            // (in the DAO) knows the database connection failed.
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

        // Step 2: Ask the DriverManager to create a connection
        // using our URL, username, and password.
        return DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
    }

    // --- Helper Main Method (for testing) ---
    /**
     * A simple main method to test if the database connection is working.
     * You can run this file directly to check your credentials.
     */
    public static void main(String[] args) {
        System.out.println("--- Attempting Database Connection Test ---");
        try (Connection conn = getConnection()) {
            // The 'try-with-resources' block automatically closes the connection.

            if (conn != null && !conn.isClosed()) {
                System.out.println("Connection successful!");
                System.out.println("Connected to database: " + conn.getCatalog());
            } else {
                System.err.println("Connection failed (object is null or closed).");
            }
        } catch (SQLException e) {
            System.err.println("--- Connection Test FAILED ---");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Message: " + e.getMessage());
            System.err.println("----------------------------------");
            e.printStackTrace();
        }

        System.out.println("--- Connection Test Finished ---");
    }
}