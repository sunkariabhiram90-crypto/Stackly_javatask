package com;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Connected to inventory_management successfully.");
            InventoryManagementSystem system = new InventoryManagementSystem(connection, scanner);
            system.run();
        } catch (SQLException e) {
            System.err.println("Could not connect to MySQL: " + e.getMessage());
            System.err.println("Check that MySQL is running, the database exists, Connector/J is installed,");
            System.err.println("and DatabaseConnection.java contains the correct username and password.");
        }
        System.out.println("Inventory Management System closed.");
    }
}
