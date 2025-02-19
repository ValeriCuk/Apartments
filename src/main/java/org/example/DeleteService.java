package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DeleteService {

    private final Connection connection;
    private final Scanner scanner = new Scanner(System.in);

    public DeleteService(Connection connection) {
        this.connection = connection;
    }

    public void deleteApartment() throws SQLException {
        int id = getID();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Apartments WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    private int getID(){
        int result;
        while (true) {
            System.out.print("Enter apartments id for deletion: ");
            String id = scanner.nextLine();
            if (id == null || id.trim().isEmpty()) {
                System.out.println("-> Invalid input!\n");
                continue;
            }
            if (!id.trim().matches("\\d+")) {
                System.out.println("-> Invalid input!\n");
                continue;
            }
            result = Integer.parseInt(id.trim());
            break;
        }
        return result;
    }
}
