package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Scanner;

public class InsertRandomService {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);

    public InsertRandomService(Connection connection) {
        this.connection = connection;
    }

    public void insertRandomApartments() throws SQLException {
        int apartmentsCount = getApartmentsCount();
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

    private int getApartmentsCount() {
        int apartmentsCount;
        while (true) {
            System.out.print("Enter apartments count: ");
            String sCount = scanner.nextLine();
            if (sCount == null || sCount.trim().isEmpty()) {
                System.out.println("-> Invalid input!\n");
                continue;
            }
            if (!sCount.trim().matches("\\d+")) {
                System.out.println("-> Invalid input!\n");
                continue;
            }
            apartmentsCount = Integer.parseInt(sCount.trim());
            break;
        }
        return apartmentsCount;
    }
}
