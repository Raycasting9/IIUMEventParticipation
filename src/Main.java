import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Main application class for the Event Participation System.
 * Handles the main menu and user interactions.
 */
public class Main {

    private static UserManager userManager;
    private static EventManager eventManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        userManager = new UserManager(); // Loads users
        eventManager = new EventManager(); // Loads events and registrations
        scanner = new Scanner(System.in);

        System.out.println("==============================");
        System.out.println(" Event Participation System");
        System.out.println("==============================");

        runMainMenu();

        scanner.close(); 
        System.out.println("Exiting Event Participation System. Goodbye!");
    }

    /**
     * Runs the main menu loop (Login, Register, Exit).
     */
    private static void runMainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = -1;
            try {
                 choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                 System.out.println("Invalid input. Please enter a number (1-3).");
                 scanner.next(); 
                 continue; 
            }
            scanner.nextLine();

            switch (choice) {
                case 1:
                    User loggedInUser = userManager.loginUser(scanner);
                    if (loggedInUser != null) {
                        // Delegate to the user's specific dashboard
                        loggedInUser.showDashboard(scanner, userManager, eventManager);
                    }
                    break;
                case 2:
                    userManager.registerUser(scanner);
                    break;
                case 3:
                    running = false; 
                    break;
                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }
}
