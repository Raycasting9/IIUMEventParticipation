import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents an Admin user, inheriting from User.
 * Contains methods for admin-specific actions.
 */
public class Admin extends User {

    public Admin(String username, String password, String name, String phoneNumber, String gender, String email) {
        super(username, password, name, phoneNumber, gender, email, "Admin"); // Pass email
    }

    /**
     * Displays the Admin dashboard and handles admin actions.
     */
    @Override
    public void showDashboard(Scanner scanner, UserManager userManager, EventManager eventManager) {
        System.out.println("\n--- Admin Dashboard ---");
        System.out.println("Welcome, " + getName() + "!");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. Create New Event");
            System.out.println("2. Delete Event");
            System.out.println("3. Edit Event Details");
            System.out.println("4. View Event Details");
            System.out.println("5. View All Events");
            System.out.println("6. Get List of Participants for an Event");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); 
                continue;
            }
            scanner.nextLine(); 

            switch (choice) {
                case 1:
                    eventManager.createEvent(scanner);
                    break;
                case 2:
                    eventManager.deleteEvent(scanner);
                    break;
                case 3:
                    eventManager.editEvent(scanner);
                    break;
                case 4:
                    eventManager.viewEventDetailsById(scanner);
                    break;
                case 5:
                    eventManager.viewAllEvents();
                    break;
                case 6:
                    eventManager.generateParticipantListFile(scanner);
                    break;
                case 7:
                    System.out.println("Logging out...");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
