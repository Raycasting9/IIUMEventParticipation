import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Manages user accounts (registration, login, loading/saving).
 * 
 */
public class UserManager {
    private List<User> users; // In-memory list of users
    private final String USER_FILE = "users.txt"; // File to store user data

    public UserManager() {
        this.users = new ArrayList<>();
        loadUsers(); // Load existing users when UserManager is created
    }

    /**
     * Loads users from the text file into the in-memory list.
     */
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            System.out.println("User file (" + USER_FILE + ") not found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = User.fromFileString(line);
                if (user != null) {
                    users.add(user);
                } else {
                     
                }
            }
            System.out.println("Loaded " + users.size() + " users.");
        } catch (IOException e) {
            System.err.println("Error loading users from " + USER_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Saves the current list of users to the text file.
     * 
     */
    private void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE, false))) { // false = overwrite
            for (User user : users) {
                writer.println(user.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving users to " + USER_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Handles the user registration process.
     * 
     *
     * @param scanner Scanner for user input.
     */
    public void registerUser(Scanner scanner) {
        System.out.println("\n--- User Registration ---");
        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        String username;
        while (true) {
            System.out.print("Enter Username: ");
            username = scanner.nextLine();
            if (username.trim().isEmpty()) {
                 System.out.println("Username cannot be empty.");
            } else if (username.contains(";")) {
                 System.out.println("Username cannot contain the character ';'. Please choose another.");
            } else if (isUsernameTaken(username)) {
                System.out.println("Username already taken. Please choose another.");
            } else {
                break; 
            }
        }

        // Password input
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
         if (password.contains(";")) {
             System.out.println("Warning: Password contains ';'. This might cause issues.");
         }

         // Email input and basic validation loop
         String email;
         while(true) {
             System.out.print("Enter Email: ");
             email = scanner.nextLine();
             if (email.trim().isEmpty()) {
                 System.out.println("Email cannot be empty.");
             } else if (email.contains(";")) {
                 System.out.println("Email cannot contain the character ';'.");
             } else if (!email.contains("@") || !email.contains(".")) { // Very basic check
                 System.out.println("Please enter a valid-looking email address (containing '@' and '.').");
             }
              else {
                 break; 
             }
         }


        // Phone number input
        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();

        // Gender input validation loop (M/F)
        String genderInput;
        String gender = ""; 
        while (true) {
            System.out.print("Enter Gender (M=Male / F=Female): ");
            genderInput = scanner.nextLine().toUpperCase();
            if ("M".equals(genderInput)) {
                gender = "Male";
                break;
            } else if ("F".equals(genderInput)) {
                gender = "Female";
                break;
            } else {
                System.out.println("Invalid input. Please enter 'M' or 'F'.");
            }
        }

        // Role input validation loop (A/S)
        String roleInput;
        String roleType = ""; 
        while (true) {
            System.out.print("Register as (A=Admin / S=Student): ");
            roleInput = scanner.nextLine().toUpperCase();
            if ("A".equals(roleInput)) {
                roleType = "Admin";
                break;
            } else if ("S".equals(roleInput)) {
                roleType = "Student";
                break;
            } else {
                System.out.println("Invalid input. Please enter 'A' or 'S'.");
            }
        }

        // Create the appropriate User object
        User newUser;
        if ("Admin".equals(roleType)) {
            newUser = new Admin(username, password, name, phone, gender, email);
        } else { // Must be Student
            newUser = new Student(username, password, name, phone, gender, email);
        }

        users.add(newUser);
        saveUsers();
        System.out.println("Registration successful for " + newUser.getRole() + ": " + username);
    }

    /**
     * Checks if a username is already present in the user list 
     *
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    private boolean isUsernameTaken(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false; 
        }
        for (User user : users) {
            if (user != null && user.getUsername() != null && user.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Handles the user login process.
     * Prompts for username and password, checks against the loaded users.
     *
     * @param scanner Scanner for user input.
     * @return The logged-in User object if successful, otherwise null.
     */
    public User loginUser(Scanner scanner) {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        Optional<User> userOpt = findUserByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getPassword() != null && user.getPassword().equals(password)) {
                 System.out.println("Login successful! Role: " + user.getRole());
                 return user; // Return the matched User object
            }
        }

        System.out.println("Login failed: Invalid username or password.");
        return null; // Login failed
    }

     /**
      * Finds a user by username 
      * Returns an Optional for better handling of user not found cases.
      *
      * @param username 
      * @return
      */
     public Optional<User> findUserByUsername(String username) {
         if (username == null || username.trim().isEmpty()) {
             return Optional.empty(); 
         }
         for (User user : users) {
             
             if (user != null && user.getUsername() != null && user.getUsername().equalsIgnoreCase(username)) {
                 return Optional.of(user); 
             }
         }
         return Optional.empty(); 
     }
}
