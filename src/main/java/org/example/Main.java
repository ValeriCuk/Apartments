package org.example;

import java.sql.*;
import java.util.Random;
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
                        addApartment(scanner);
                        break;
                    case "2":
                        insertRandomApartments(scanner);
                        break;
                    case "3":
                        deleteApartment(scanner);
                        break;
                    case "4":
                        changeApartment(scanner);
                        break;
                    case "5":
                        viewTable();
                        break;
                    default:
                        return;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private static void initDB() throws SQLException{
        try (Statement statement = connection.createStatement();){
            statement.execute("DROP TABLE IF EXISTS Apartments");
            statement.execute("CREATE TABLE Apartments (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "region VARCHAR(20) NOT NULL, "+
                    "address VARCHAR(50) NOT NULL, "+
                    "area DECIMAL(5, 2) NOT NULL, "+
                    "bedrooms INT NOT NULL, "+
                    "price INT NOT NULL "
                    +")");
        }
    }

    private static void addApartment(Scanner scanner) throws SQLException{
        Apartment apartment = getInputedApartment(scanner);
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Apartments (region, address, area, bedrooms) VALUES (?, ?, ?, ?)")){
            preparedStatement.setString(1, apartment.getRegion());//REGION
            Address address = apartment.getAddress();
            preparedStatement.setString(2, address.toString());//ADDRESS
            preparedStatement.setDouble(3, apartment.getArea());//AREA
            preparedStatement.setInt(4, apartment.getBedrooms());//BEDROOMS
            preparedStatement.setInt(5, apartment.getPrice());//PRICE
            preparedStatement.executeUpdate();
        }

        System.out.println("The apartment has been added to the database.");
    }

    private static Apartment getInputedApartment(Scanner scanner) throws SQLException{
        System.out.print("Enter the region of the apartment: ");
        String region = scanner.nextLine();
        System.out.println("Enter the address of the apartment: ");

        Address address = new Address();

        System.out.print("City -> ");
        String city = scanner.nextLine();
        address.setCity(city);
        System.out.println();
        System.out.print("Street/Avenue/Boulevard: -> ");
        String street = scanner.nextLine();
        address.setStreet(street);
        System.out.println();
        System.out.print("Build number -> ");
        int buildNumber = scanner.nextInt();
        address.setBNum(buildNumber);
        System.out.println();
        System.out.print("Apartment number -> ");
        int apartmentNumber = scanner.nextInt();
        address.setApNum(apartmentNumber);
        System.out.println();

        System.out.println("Area m² (example 30.2) -> ");
        double area = scanner.nextDouble();
        System.out.println("Bedrooms -> ");
        int bedrooms = scanner.nextInt();
        System.out.println("Price $ -> ");
        int price = Integer.parseInt(scanner.nextLine());
        Apartment apartment = new Apartment(0, region, address, area, bedrooms, price);

        return apartment;
    }

    private static void insertRandomApartments(Scanner scanner) throws SQLException{
        System.out.print("Enter apartments count: ");
        String sCount = scanner.nextLine();
        int apartmentsCount = Integer.parseInt(sCount);
        Random rnd = new Random();
        connection.setAutoCommit(false);

        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Apartments (region, address, area, bedrooms, price) VALUES(?, ?, ?, ?, ?)")){
            try{
                for (int i = 0; i < apartmentsCount; i++) {
                    preparedStatement.setString(1, "Region" + (i+1));//REGION
                    Address address = new Address("City", "Street ", (10 + i), (i+1));
                    preparedStatement.setString(2, address.toString());//ADDRESS
                    preparedStatement.setDouble(3, rnd.nextDouble(13.0, 170.0));//AREA
                    preparedStatement.setInt(4, rnd.nextInt(1, 4));//BEDROOMS
                    preparedStatement.setInt(5, rnd.nextInt(20000, 100000));//PRICE
                    preparedStatement.executeUpdate();
                }
                connection.commit();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("RollBack");
                connection.rollback();
            }
        }finally {
            connection.setAutoCommit(true);
        }
    }

    private static void deleteApartment(Scanner scanner) throws SQLException{
        System.out.print("Enter apartments id: ");
        int id = Integer.parseInt(scanner.nextLine());
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Apartments WHERE id = ?")){
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private static void changeApartment(Scanner scanner) throws SQLException{
        System.out.print("Enter apartments id for change: ");
        int id = Integer.parseInt(scanner.nextLine());
        showApartmentBy(id);
        System.out.print("What would you like to change? ");
        System.out.println("1: Region");
        System.out.println("2: Address");
        System.out.println("3: Area");
        System.out.println("4: Bedrooms");
        System.out.println("5: Price");
        System.out.print("-> ");

        String s = scanner.nextLine();
        switch (s) {
            case "1":
                System.out.print("New region: ");
                String newRegion = scanner.nextLine();
                updateApartmentBy(id, "region", newRegion);
                break;
            case "2":
                //City, 10 Street, Apt 1 TODO: change address
                break;
            case "3":
                System.out.print("New area: ");
                String newArea = scanner.nextLine();
                updateApartmentBy(id, "area", newArea);
                break;
            case "4":
                System.out.print("New bedrooms: ");
                String newBedrooms = scanner.nextLine();
                updateApartmentBy(id, "bedrooms", newBedrooms);
                break;
            case "5":
                viewTable();
                break;
            default:
                return;
        }
    }

    private static <T> void updateApartmentBy(int id, String param, T value) throws SQLException{
            String query = "UPDATE Apartments SET " + param + " = ? WHERE id = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
            if (value instanceof String) {
                preparedStatement.setString(1, (String) value);
            } else if (value instanceof Integer) {
                preparedStatement.setInt(1, (Integer) value);
            } else if (value instanceof Double) {
                preparedStatement.setDouble(1, (Double) value);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass().getSimpleName());
            }
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    private static void showApartmentBy(int id) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Apartments WHERE id = ?")){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) { // Перевіряємо, чи є запис
                System.out.println("Apartment found:");
                System.out.println("\tID: " + resultSet.getInt("id"));
                System.out.println("\tRegion: " + resultSet.getString("region"));
                System.out.println("\tAddress: " + resultSet.getString("adress"));
                System.out.println("\tArea: " + resultSet.getDouble("area"));
                System.out.println("\tRooms: " + resultSet.getInt("bedrooms"));
                System.out.println("\tPrice: " + resultSet.getInt("price"));
            } else {
                System.out.println("Apartment with ID " + id + " not found.");
            }
        }
    }

    private static void viewTable() throws SQLException{
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Apartments")){
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String region = resultSet.getString("region");
                String addressStr = resultSet.getString("address");
                Address address = new Address();
                address.fromString(addressStr);
                double area = resultSet.getDouble("area");
                int bedrooms = resultSet.getInt("bedrooms");
                int price = resultSet.getInt("price");
                Apartment apartment = new Apartment(id, region, address, area, bedrooms, price);
                System.out.println(apartment);
            }
        }
    }
}