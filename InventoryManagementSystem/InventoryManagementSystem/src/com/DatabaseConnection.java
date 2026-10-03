package com;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String URL =
            "jdbc:mysql://localhost:3306/inventory_management?useSSL=false&serverTimezone=Asia/Kolkata";
    private static final String USER = "root";
    private static final String PASSWORD = "Abhi$2004";

    private DatabaseConnection() { }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Connector/J driver is missing from the project build path.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
