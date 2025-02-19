package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UpdateService {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);

    public UpdateService(Connection connection) {
        this.connection = connection;
    }

    public void changeApartment() throws SQLException {
        System.out.print("Enter apartments id for change: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            if (!showApartmentBy(id)) return;
            System.out.println("What would you like to change? ");
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
                    updateAddressBy(id);
                    break;
                case "3":
                    System.out.print("New area: ");
                    String newArea = scanner.nextLine();
                    updateApartmentBy(id, "area", getArea());
                    break;
                case "4":
                    System.out.print("New bedrooms: ");
                    String newBedrooms = scanner.nextLine();
                    updateApartmentBy(id, "bedrooms", newBedrooms);
                    break;
                case "5":
                    System.out.print("New price: ");
                    String newPrice = scanner.nextLine();
                    updateApartmentBy(id, "bedrooms", newPrice);
                    break;
                default:
                    return;
            }
        }catch(NumberFormatException ex){
            return;
        }
    }

    private void updateAddressBy(int id) throws SQLException{
        Address address = getAddressBy(id);
        String choice = "";
        while(!choice.equals("5")) {
            System.out.print("What would you like to change? ");
            System.out.println("1: City");
            System.out.println("2: Building");
            System.out.println("3: Street name");
            System.out.println("4: Apartment number");
            System.out.println("5: Accept");
            System.out.print("-> ");
            String s = scanner.nextLine();
            switch (s) {
                case "1":
                    System.out.print("From " + address.getCity() + " to -> ");
                    String newCity = scanner.nextLine();
                    address.setCity(newCity);
                    break;
                case "2":
                    System.out.print("From " + address.getBNum() + " to -> ");
                    int newBNum = scanner.nextInt();
                    address.setBNum(newBNum);
                    break;
                case "3":
                    System.out.print("From " + address.getStreet() + " to -> ");
                    String newStreet = scanner.nextLine();
                    address.setStreet(newStreet);
                    break;
                case "4":
                    System.out.print("From " + address.getApNum() + " to -> ");
                    int newApNum = scanner.nextInt();
                    address.setApNum(newApNum);
                    break;
                case "5":
                    choice = "5";
                    break;
                default:
                    return;
            }
        }
        updateApartmentBy(id, "address", address);
    }

    private <T> void updateApartmentBy(int id, String param, T value) throws SQLException{
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

    private double getArea() {
        double area;
        while (true) {
            System.out.print("Area m² (example 30,25) -> ");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                System.out.println("-> Invalid input!\n");
                continue;
            }

            String strArea = input.trim().replace(',', '.');

            try {
                area = Double.parseDouble(strArea);
                return area;
            } catch (NumberFormatException e) {
                System.out.println("-> Invalid input! " + strArea + "\n");
            }
        }
    }

    private Address getAddressBy(int id) throws SQLException{
        Address address = new Address();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM address WHERE id = ?")){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String addressString = resultSet.getString("address");
                address.fromString(addressString);
            } else {
                throw new SQLException("Apartment with ID " + id + " not found.");
            }
        }
        return address;
    }


    private boolean showApartmentBy(int id) throws SQLException{
        try(PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Apartments WHERE id = ?")){
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) { // Перевіряємо, чи є запис
                System.out.println("Apartment found:");
                System.out.println("\tID: " + resultSet.getInt("id"));
                System.out.println("\tRegion: " + resultSet.getString("region"));
                System.out.println("\tAddress: " + resultSet.getString("address"));
                System.out.println("\tArea: " + resultSet.getDouble("area"));
                System.out.println("\tBedrooms: " + resultSet.getInt("bedrooms"));
                System.out.println("\tPrice: " + resultSet.getInt("price"));
                return true;
            } else {
                System.out.println("Apartment with ID " + id + " not found.");
                return false;
            }
        }
    }
}
