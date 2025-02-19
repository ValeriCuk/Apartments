package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class AddService {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);

    public AddService(Connection connection) {
        this.connection = connection;
    }

    public void addApartment() throws SQLException {
        Apartment apartment = getInputedApartment();
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Apartments (region, address, area, bedrooms, price) VALUES (?, ?, ?, ?, ?)")){
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


    private Apartment getInputedApartment() throws SQLException {
        String region = getRegion();

        System.out.println("Enter the address of the apartment: ");
        Address address = getAddress();
        double area = getArea();
        int bedrooms = getBedrooms();
        int price = getPrice();

        return new Apartment(0, region, address, area, bedrooms, price);
    }

    private String getRegion() {
        String region;
        do {
            System.out.print("Enter the region of the apartment: ");
            region = scanner.nextLine().trim();
            if (region.isEmpty() || region.matches("[0-9]+"))
                System.out.print(" -> Invalid input!\n");
        }while(region.isEmpty() || region.matches("[0-9]+"));
        return region;
    }

    private Address getAddress(){
        Address address = new Address();
        setCity(address);
        setStreet(address);
        setBuildNumber(address);
        setApartmentNumber(address);
        return address;
    }

    private void setCity(Address address) {
        String city;
        do {
            System.out.print("City -> ");
            city = scanner.nextLine();
            address.setCity(city);
            System.out.println();
            if (city.isEmpty() ||!city.matches("[a-zA-Z]+"))
                System.out.print(" -> Invalid input!\n");
        }while (city.isEmpty() || !city.matches("[a-zA-Z]+"));
    }

    private void setStreet(Address address) {
        String street;
        do {
            System.out.print("Street/Avenue/Boulevard: -> ");
            street = scanner.nextLine();
            address.setStreet(street);
            System.out.println();
            if (street.isEmpty() || street.matches("[0-9]+"))
                System.out.print(" -> Invalid input!\n");
        }while (street.isEmpty() || street.matches("[0-9]+"));
    }

    private void setBuildNumber(Address address) {
        String buildNumberStr;
        int buildNumber;

        do {
            System.out.print("Build number -> ");
            buildNumberStr = scanner.nextLine();
            if (buildNumberStr.matches("\\d+")) {
                buildNumber = Integer.parseInt(buildNumberStr);
                address.setBNum(buildNumber);
                System.out.println("-> " + buildNumber);
                if (buildNumber == 0) {
                    System.out.print(" -> Invalid input!");
                }
            } else {
                System.out.print(" -> Invalid input! Please enter a valid number.\n");
                buildNumber = 0;
            }
        } while (buildNumber == 0);
    }

    private void setApartmentNumber(Address address) {
        String apartmentNumberStr;
        int apartmentNumber;

        do {
            System.out.print("Apartment number -> ");
            apartmentNumberStr = scanner.nextLine();

            if (apartmentNumberStr.matches("\\d+")) {
                apartmentNumber = Integer.parseInt(apartmentNumberStr);
                address.setApNum(apartmentNumber);
                System.out.println("-> " + apartmentNumber);
                break;
            } else {
                System.out.println(" -> Invalid input! Please enter a valid number.");
            }
        } while (true);
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

    private int getBedrooms() {
        String bedroomsStr;
        int bedrooms;

        do {
            System.out.print("Bedrooms -> ");
            bedroomsStr = scanner.nextLine();

            if (bedroomsStr.matches("\\d+")) {
                bedrooms = Integer.parseInt(bedroomsStr);
                System.out.println("-> " + bedrooms);
                break;
            } else {
                System.out.println(" -> Invalid input! Please enter a valid number.\n");
            }
        } while (true);
        return bedrooms;
    }

    private int getPrice() {
        while (true) {
            System.out.print("Price $ -> ");
            String input = scanner.nextLine();

            if (input == null || input.trim().isEmpty()) {
                System.out.println("-> Invalid input!\n");
                continue;
            }

            try {
                return Integer.parseInt(input.trim());
            } catch (NumberFormatException e) {
                System.out.println("-> Invalid input! Please enter a valid number.\n");
            }
        }
    }
}
