package org.example;

import java.sql.*;
import java.util.Scanner;

public class Main {

    private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/apartmentsdb?serverTimezone=Europe/Kiev";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD= "";

    private static Connection connection;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try(Connection connection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD)){
            Main.connection = connection;
            initDB();
            while (true) {
                System.out.println("1: add new apartment");
                System.out.println("2: add random apartments");
                System.out.println("3: delete apartment");
                System.out.println("4: change apartment");
                System.out.println("5: view apartments");
                System.out.print("-> ");

                String s = scanner.nextLine();
                switch (s) {
                    case "1":
                        new AddService(connection).addApartment();
                        break;
                    case "2":
                        new InsertRandomService(connection).insertRandomApartments();
                        break;
                    case "3":
                        new DeleteService(connection).deleteApartment();
                        break;
                    case "4":
                        new UpdateService(connection).changeApartment();
                        break;
                    case "5":
                        new FilterService(connection).getFilter();
                        break;
                    default:
                        return;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void initDB() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DROP TABLE IF EXISTS Apartments");
            statement.execute("CREATE TABLE Apartments (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "region VARCHAR(20) NOT NULL, " +
                    "address VARCHAR(50) NOT NULL, " +
                    "area DECIMAL(5, 2) NOT NULL, " +
                    "bedrooms INT NOT NULL, " +
                    "price INT NOT NULL "
                    + ")");
        }
    }
}