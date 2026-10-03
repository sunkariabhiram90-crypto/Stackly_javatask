package com;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in); Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Connected to MySQL database successfully.");
            new ComplaintManagementSystem(scanner, connection).run();
        } catch (SQLException e) {
            System.out.println("Database connection failed. Verify MySQL is running, the database exists, DB_USER/DB_PASSWORD are correct, and Connector/J is added to Eclipse.");
            System.out.println("Technical detail: " + e.getMessage());
        }
    }
}
