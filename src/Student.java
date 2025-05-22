import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Represents a Student user, inheriting from User.
 * Contains methods for student-specific actions.
 */
public class Student extends User {

    
    public Student(String username, String password, String name, String phoneNumber, String gender, String email) {
        super(username, password, name, phoneNumber, gender, email, "Student"); 
    }

    /**
     * Displays the Student dashboard and handles student actions.
     */
    @Override
    public void showDashboard(Scanner scanner, UserManager userManager, EventManager eventManager) {
        System.out.println("\n--- Student Dashboard ---");
        System.out.println("Welcome, " + getName() + "!");

        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\nStudent Menu:");
            System.out.println("1. View Available Events");
            System.out.println("2. View Event Details");
            System.out.println("3. Register for an Event");
            System.out.println("4. View My Registered Events");
            System.out.println("5. Unregister from an Event");
            System.out.println("6. Logout");
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
                    eventManager.viewAllEvents();
                    break;
                case 2:
                     eventManager.viewEventDetailsById(scanner);
                    break;
                case 3:
                    eventManager.registerStudentForEvent(scanner, this.username);
                    break;
                case 4:
                    eventManager.viewRegisteredEvents(this.username);
                    break;
                case 5:
                    eventManager.unregisterStudentFromEvent(scanner, this.username);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    loggedIn = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
