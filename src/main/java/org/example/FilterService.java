package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class FilterService {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);


    public FilterService(Connection connection) {
        this.connection = connection;
    }

    public void getFilter() throws SQLException {
        System.out.println("Select a filter -> ");
        while (true) {
            System.out.println("\t1: region");
            System.out.println("\t2: area");
            System.out.println("\t3: bedrooms");
            System.out.println("\t4: price");
            System.out.println("\t5: show all");
            System.out.print("-> ");

            String s = scanner.nextLine();
            switch (s) {
                case "1":
                    showByRegion();
                    break;
                case "2":
                    showByArea();
                    break;
                case "3":
                    showByBedrooms();
                    break;
                case "4":
                    showByPrice();
                    break;
                case "5":
                    viewTable();
                    break;
                default:
                    return;
            }
        }
    }

    private void showByRegion() throws SQLException {
        System.out.println("Enter region name \n-> ");
        String regionFilter = scanner.nextLine().trim();

        if(regionFilter.isEmpty()) {
            viewTable();
            return;
        }

        String query = "SELECT * FROM Apartments WHERE region = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, regionFilter);
            ResultSet rs = ps.executeQuery();

            boolean found = false;
            while (rs.next()) {
                found = true;
                int id = rs.getInt("id");
                String region = rs.getString("region");
                String addressStr = rs.getString("address");
                Address address = new Address();
                address.fromString(addressStr);
                double area = rs.getDouble("area");
                int bedrooms = rs.getInt("bedrooms");
                int price = rs.getInt("price");

                Apartment apartment = new Apartment(id, region, address, area, bedrooms, price);
                System.out.println(apartment);
            }
            if (!found) System.out.println("No such region");
        }
    }

    private void showByArea() throws SQLException {
        double minArea, maxArea;

        while (true) {
            System.out.print("Enter minimum area: ");
            String minAreaStr = scanner.nextLine().trim();
            System.out.print("Enter maximum area: ");
            String maxAreaStr = scanner.nextLine().trim();

            if (minAreaStr.isEmpty() || maxAreaStr.isEmpty()) {
                System.out.println("Empty input!");
                continue;
            }

            minAreaStr = minAreaStr.replace(',', '.');
            maxAreaStr = maxAreaStr.replace(',', '.');

            try {
                minArea = Double.parseDouble(minAreaStr);
                maxArea = Double.parseDouble(maxAreaStr);

                if (minArea > maxArea) {
                    System.out.println("Minimum area cannot be greater than maximum area. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format! Please enter valid numbers for area.");
            }
        }
        showWith("area", minArea, maxArea);
    }

    private void showByBedrooms() throws SQLException {
        int minBedrooms, maxBedrooms;

        while (true) {
            System.out.print("Enter minimum bedrooms: ");
            String minBedroomsStr = scanner.nextLine().trim();
            System.out.print("Enter maximum bedrooms: ");
            String maxBedroomsStr = scanner.nextLine().trim();

            if (minBedroomsStr.isEmpty() || maxBedroomsStr.isEmpty()) {
                System.out.println("Empty input!");
                continue;
            }

            if (!minBedroomsStr.matches("\\d+") || !maxBedroomsStr.matches("\\d+")) {
                System.out.println("Invalid number format! ");
                continue;
            }

            try {
                minBedrooms = Integer.parseInt(minBedroomsStr);
                maxBedrooms = Integer.parseInt(maxBedroomsStr);

                if (minBedrooms > maxBedrooms) {
                    System.out.println("The minimum number of bedrooms cannot be greater than the maximum. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format! ");
            }
        }
        showWith("bedrooms", minBedrooms, maxBedrooms);
    }

    private void showByPrice() throws SQLException {
        int minPrice, maxPrice;

        while (true) {
            System.out.print("Enter minimum price: ");
            String minPriceStr = scanner.nextLine().trim();
            System.out.print("Enter maximum price: ");
            String maxPriceStr = scanner.nextLine().trim();

            if (minPriceStr.isEmpty() || maxPriceStr.isEmpty()) {
                System.out.println("Empty input! Please enter a valid number.");
                continue;
            }

            if (!minPriceStr.matches("\\d+") || !maxPriceStr.matches("\\d+")) {
                System.out.println("Invalid input! Please enter integer values.");
                continue;
            }

            try {
                minPrice = Integer.parseInt(minPriceStr);
                maxPrice = Integer.parseInt(maxPriceStr);

                if (minPrice > maxPrice) {
                    System.out.println("Minimum price cannot be greater than maximum price. Please try again.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format! Please enter a valid integer number.");
            }
        }
        showWith("price", minPrice, maxPrice);
    }

    private void showWith(String parameter, Number minValue, Number maxValue) throws SQLException {
        String sql = "SELECT * FROM Apartments WHERE " + parameter + " BETWEEN ? AND ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (minValue instanceof Integer && maxValue instanceof Integer) {
                preparedStatement.setInt(1, minValue.intValue());
                preparedStatement.setInt(2, maxValue.intValue());
            } else {
                preparedStatement.setDouble(1, minValue.doubleValue());
                preparedStatement.setDouble(2, maxValue.doubleValue());
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean found = false;
            while (resultSet.next()) {
                found = true;
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

            if (!found) {
                System.out.println("No apartments found with this query =(");
            }
        }
    }

    private void viewTable() throws SQLException {
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
