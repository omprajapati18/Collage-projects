import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        UserManagement userManagement = new UserManagement();
        int userId = userManagement.manageUser(scanner);
        Booking booking = new Booking(scanner, userId);
        if (userId > 0) {

            while (true) {

                System.out.println("Enter 1 : View Current Booking ");
                System.out.println("Enter 2 : Make New Booking");
                System.out.println("Enter 3 : Exit Booking");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        booking.DisplayTicket();

                        break;

                    case 2:
                        booking.manageBooking();

                        break;

                    case 3:
                        scanner.close();

                    default:
                        System.out.println("Enter valid input");

                }

            }

        }

    }
}
