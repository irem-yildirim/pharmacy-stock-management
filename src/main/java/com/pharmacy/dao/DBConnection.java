package com.pharmacy.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Veritabanı bağlantı yönetimi.
 * Her seferinde yeni bağlantı açıp sistemi yormamak için
 * Singleton tasarım desenini tercih ettik.
 */
public class DBConnection {
    private static DBConnection instance;

    private static final String URL = "jdbc:mysql://localhost:3306/pharmacy_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    private Connection connection;

    private DBConnection() {
        // Private constructor — Singleton Pattern.
        // Dışarıdan yeni nesne oluşturulmasın diye
        // constructor'ı kapattık (Singleton kuralı).
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    /**
     * Aktif bir bağlantı varsa onu döndürür,
     * yoksa veya kopmuşsa yenisini oluşturup verir.
     */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database connection failed: " + e.getMessage());
        }
        return connection;
    }
}
