import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Formatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;



public class Booking {
    // Declare and initialize the ArrayList to store city codes and valid flight codes
    ArrayList<String> allCityCodes = new ArrayList<>();
    ArrayList<String> validflightcodes = new ArrayList<>();
    private Scanner _scanner;
    private int _userId;
    private java.util.Date date;

    public Booking(Scanner scanner, int userId) {
        _scanner = scanner;
        _userId = userId;
    }

    public void manageBooking() {
        boolean continueRunning = true;
        System.out.print("Please Select Source city : ");
        getCities(); // Populate the list of city codes
        while (continueRunning) {
            String sourceCity = "";
            String destinationCity = "";

            // Prompt user to enter the source city code
            System.out.println("Enter Source City CODE = ");
            while (sourceCity.isEmpty()) {
                String input = _scanner.nextLine();
                input = input.toUpperCase();
                if (!input.isEmpty()) {
                    if (isValidCity(input)) {
                        sourceCity = input; // Set source city if valid
                    } else {
                        System.out.println("Wrong Code Entered!!");
                    }
                }
            }

            // Prompt user to enter the destination city code
            while (destinationCity.isEmpty()) {
                System.out.println("Enter Destination City CODE = ");
                String input = _scanner.nextLine();
                input = input.toUpperCase();
                if (!input.isEmpty()) {
                    if (isValidCity(input)) {

                        destinationCity = input; // Set destination city if valid
                        if(input.equals(sourceCity)){
                            System.out.println("Source and Destination cannot be same");
                        }

                    } else {
                        System.out.println("Wrong Code Entered!!");
                    }
                }
            }

            selectDate(_scanner);

            // Retrieve and display available flights between the source and destination
            // cities
            boolean flightFound = GetFlights(sourceCity, destinationCity);
            if (flightFound == true) {
                // Book a flight for the user
               
                BookFlight();
                DisplayTicket();
            }

            // Exit the loop after booking
            continueRunning = false;

        }

       

      //  _scanner.close();

    }

    public void selectDate(Scanner sc) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println( "Select A Date From Below Option");
        System.out.println("1. 17/09/2024");
        System.out.println("2. 18/09/2024");
        System.out.println("3. 19/09/2024");
        System.out.println("4. 20/09/2024");
        System.out.println("5. 21/09/2024" );

        while (true) {
            System.out.print("Enter Choice = ");
            int dateChoice = sc.nextInt();
            sc.nextLine(); // Consume newline
            if (dateChoice >= 1 && dateChoice <= 5) {
                var stringDate = (16 + dateChoice) +"/09/2024";
                try {
                    date = formatter.parse(stringDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            } else {
                System.out.println( "Wrong choice entered!!" );
            }
        }
    }


    private void getCities() {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            // Get all cities code and name
            String queryCities = "SELECT * FROM cities";
            PreparedStatement queryStatement = connection.prepareStatement(queryCities);
            ResultSet resultCities = queryStatement.executeQuery();

            while (resultCities.next()) {
                String cityCode = resultCities.getString("cityCode");
                String cityName = resultCities.getString("cityName");
                allCityCodes.add(cityCode);
                System.out.println(cityCode + " - " + cityName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidCity(String cityCode) {
        return allCityCodes.contains(cityCode);
    }

    private boolean GetFlights(String sourceCity, String destinationCity) {
        boolean flightFound = false;

        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {

            // Check for flights
            String flightQuery = "SELECT * FROM flights WHERE sourceCitycode = ? and destinationCityCode = ?";
            PreparedStatement flightStatement = connection.prepareStatement(flightQuery);
            flightStatement.setString(1, sourceCity);
            flightStatement.setString(2, destinationCity);
            ResultSet resultSet = flightStatement.executeQuery();

            if (!resultSet.next()) {
                System.out.println("No flights available for the selected route.");
                flightFound = false;

            } else {
                System.out.println("-----------------------------------------------------------------------");
                System.out.println("FlightCode | Source City Code | Destination City Code  | Departure Time");
                System.out.println("-----------------------------------------------------------------------");
                // Iterate through all rows of the result set and print the results
                do {
                    flightFound = true;
                    String flightCode = resultSet.getString("FlightCode");
                    String sourceCityCode = resultSet.getString("SourceCityCode");
                    String destinationCityCode = resultSet.getString("DestinationCityCode");
                    String departureTime = resultSet.getString("Time");
                    validflightcodes.add(flightCode);
                    System.out.println(
                            flightCode + " | " + sourceCityCode + " | " + destinationCityCode + " | " + departureTime);
                } while (resultSet.next());
            }

            // Close the connection
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flightFound;

    }

    private void BookFlight() {
        String flightCode = "";
        System.out.println("Enter Flight Code for booking = ");
        while (flightCode.isEmpty()) {
            String input = _scanner.nextLine();
            input = input.toUpperCase();
            if (!input.isEmpty()) {
                if (isValidFlightCode(input)) {
                    flightCode = input;
                } else {
                    System.out.println("Wrong Code Entered!!");
                }
            }
        }

        
        // Confirm booking for selected flight code
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            String insertQuery = "INSERT INTO booking (userId, flightCode,BookingDate) VALUES (?, ?,?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
            insertStatement.setInt(1, _userId);
            insertStatement.setString(2, flightCode);
            insertStatement.setDate(3, sqlDate);
            insertStatement.executeUpdate();
            System.out.println("Your booking has been done successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidFlightCode(String flightCode) {
        return validflightcodes.contains(flightCode);
    }

    public  void DisplayTicket() {
        try (Connection connection = DatabaseConnection.getInstance().getConnection()) {
            // Check for flights
            String bookingQuery = "SELECT * FROM booking b " +
                    "inner join flights f on b.FlightCode = f.FlightCode " +
                    "inner join users u on b.UserId = u.UserId " +
                    "where b.UserId = ? " +
                    "Order by bookingDate ASC";
            PreparedStatement bookingStatement = connection.prepareStatement(bookingQuery);
            bookingStatement.setInt(1, _userId);
            ResultSet resultSet = bookingStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) { // Check if the result set is empty
                System.out.println();
                System.out.println("No bookings found for this user.");
                System.out.println();
                return;
            }
            System.out.println();
            System.out.println("Please find your booking details as below");
            System.out.println("");

            Formatter formatter = new Formatter();
            formatter.format("%15s %15s %15s %15s %15s %15s %15s %15s\n", "Name", "Age", "Phone Number", "Flight Code",
                    "Source", "Destination","Booking Date", "Departure Time");

            // Iterate through all rows of the result set and print the results
            while (resultSet.next()) {
                String name = resultSet.getString("Name");
                String age = resultSet.getString("Age");
                String phoneNumber = resultSet.getString("PhoneNumber");
                String flightCode = resultSet.getString("FlightCode");
                String sourceCityCode = resultSet.getString("SourceCityCode");
                String destinationCityCode = resultSet.getString("DestinationCityCode");
                String bookingDate = resultSet.getString("BookingDate");
                String departureTime = resultSet.getString("Time");

                validflightcodes.add(flightCode);
                formatter.format("%15s %15s %15s %15s %15s %15s %15s %15s\n", name, age, phoneNumber, flightCode,
                        sourceCityCode, destinationCityCode,bookingDate, departureTime);
            }
            System.out.println(formatter);
            formatter.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
