import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class UserManagement {

    public int manageUser(Scanner scanner) {
        int result = 0;
       // Scanner scanner = new Scanner(System.in);
       System.out.println("-----------------------------------------------------------------------");
       System.out.println("WELCOME  TO  AIRLINE BOOKING PORTAL");
       System.out.println("-----------------------------------------------------------------------");
       System.out.println();

        
        
       String name = "";
       while (true) {
           System.out.print("Enter Name: ");
           name = scanner.next();
           if (isValidName(name)) {
               break;
           } else {
               System.out.println("Invalid Name! Name should only contain characters.");
           }
       }
    

       int age = 0;
       while (true) {
           System.out.print("Enter Age: ");
           age = scanner.nextInt();
           if (age <= 110 && age > 0) { // Ensure age is positive and less than 100
               break;
           } else {
               System.out.println("Invalid Age! Age should be a positive number less than 100.");
           }
       }


        

        // Check valid phone number
        String phoneNumber;
        while (true) {
            System.out.print("Enter Phone Number: ");
            phoneNumber = scanner.next();
            if (isValidPhoneNumber(phoneNumber)) {
                break;
            } else {
                System.out.println("Invalid phone number. Please enter a 10-digit phone number.");
            }
        }

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {

            // Check for existing user record
            String checkQuery = "SELECT UserId FROM users WHERE phonenumber = ?";
            PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
            checkStatement.setString(1, phoneNumber);
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {
                System.out.println();
                System.out.println("Welcome to AirLine booking Portal");
                System.out.println();
                result = resultSet.getInt(1);
            } else {
                // create new user
                String insertQuery = "INSERT INTO users (name, age, phonenumber) VALUES (?, ?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS);
                insertStatement.setString(1, name);
                insertStatement.setInt(2, age);
                insertStatement.setString(3, phoneNumber);
                insertStatement.executeUpdate();
                System.out.println("Your user has been created successfully.");
                
                int userId = 0;
                // Retrieve the generated keys
                ResultSet generatedKeys = insertStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
                result = userId;
            }

            // Close the connection
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = 0;
        }

       // scanner.close();

       return result;
    }

    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.length() == 10 && phoneNumber.matches("\\d{10}");
    }

    public static boolean isValidName(String name) {
        return name.matches("[a-zA-Z]+");
    }
}
