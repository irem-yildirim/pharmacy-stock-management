package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton DB connection manager.
 * getConnection() always returns a valid, open connection.
 * If the previous connection was closed (e.g. by try-with-resources in DAOs),
 * a fresh one is created transparently.
 */
public class DBConnection {
    private static DBConnection instance;

    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    private DBConnection() {
        // Private constructor — Singleton Pattern
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Returns a valid, open JDBC connection.
     * Creates a new one if the current connection is null or closed.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] New connection established.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed: " + e.getMessage());
        }
        return connection;
    }
}
