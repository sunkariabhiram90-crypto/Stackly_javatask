package com;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DatabaseConnection.getConnection()) {
            System.out.println("Database connection established.");
            new HospitalManagementSystem(scanner, connection).run();
        } catch (SQLException e) {
            System.err.println("Unable to start the application.");
            System.err.println("Check that MySQL is running, the database exists, and DB_USER/DB_PASSWORD are correct.");
            System.err.println("Technical message: " + e.getMessage());
        }
        System.out.println("Hospital Management System closed.");
    }
}
