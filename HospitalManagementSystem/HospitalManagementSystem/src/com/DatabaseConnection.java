package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class DatabaseConnection {
    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/hospital_management?useSSL=false&serverTimezone=Asia/Kolkata";
    private static final String URL = envOrDefault("DB_URL", DEFAULT_URL);
    private static final String USER = envOrDefault("DB_USER", "root");
    private static final String PASSWORD = envOrDefault("DB_PASSWORD", "Abhi$2004");

    private DatabaseConnection() { }

    private static String envOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J driver was not found. Add its JAR to the Eclipse build path.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
