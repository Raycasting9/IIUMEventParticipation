package iium.event.participation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Manages user accounts (registration, login, loading/saving).
 */
public class UserManager {
    private static final String USER_FILE = "users.txt"; // File to store user data
    private final List<User> users; // In-memory list of users
    private static UserManager instance;

    private UserManager() {
        this.users = new ArrayList<>();
        loadUsers(); // Load existing users when UserManager is created
    }
    
    public static synchronized UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Loads users from the text file into the in-memory list.
     * Creates the file if it doesn't exist.
     */
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                // Create a default admin user if the file is newly created
                users.add(new Admin("admin", "admin123", "System Administrator", "0123456789", "Male", "admin@iium.edu.my", "Admin"));
                saveUsers();
                return;
            } catch (IOException e) {
                System.err.println("Error creating users file: " + e.getMessage());
                return;
            }
        }

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length < 5) continue; // Skip invalid lines

                if (parts.length < 7) continue; // Ensure all fields are present
                
                String username = parts[0];
                String password = parts[1];
                String userType = parts[2];
                String name = parts[3];
                String phoneNumber = parts[4];
                String gender = parts[5];
                String email = parts[6];

                if ("Admin".equals(userType)) {
                    users.add(new Admin(username, password, name, phoneNumber, gender, email, "Admin"));
                } else {
                    users.add(new Student(username, password, name, phoneNumber, gender, email, "Student"));
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    /**
     * Saves the current list of users to the text file.
     */
    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USER_FILE))) {
            for (User user : users) {
                writer.println(String.format("%s,%s,%s,%s,%s,%s,%s", 
                    user.getUsername(), 
                    user.getPassword(),
                    (user instanceof Admin) ? "Admin" : "Student",
                    user.getName(),
                    user.getPhoneNumber(),
                    user.getGender(),
                    user.getEmail()
                ));
            }
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Registers a new user with the given details.
     * @param username The username for the new user
     * @param password The password for the new user
     * @param userType The type of user ("Admin" or "Student")
     * @param name The full name of the user
     * @param phoneNumber The phone number of the user
     * @param gender The gender of the user
     * @return The newly created User object if registration was successful, null if the username is already taken
     */
    public User registerUser(String username, String password, String userType, String name, String phoneNumber, String gender) {
        // Check if username is already taken
        if (findUser(username).isPresent()) {
            return null;
        }
        
        // Generate email from username
        String email = username + "@student.iium.edu.my";
        if ("Admin".equalsIgnoreCase(userType)) {
            email = username + "@iium.edu.my";
        }
        
        // Create the appropriate user type
        User newUser;
        if ("Admin".equalsIgnoreCase(userType)) {
            newUser = new Admin(username, password, name, phoneNumber, gender, email, "Admin");
        } else {
            newUser = new Student(username, password, name, phoneNumber, gender, email, "Student");
        }
        
        users.add(newUser);
        saveUsers();
        return newUser;
    }

    /**
     * Logs in a user.
     * 
     * @param username The username
     * @param password The password
     * @return The User object if login is successful, null otherwise
     */
    public User loginUser(String username, String password) {
        return findUser(username, password).orElse(null);
    }

    /**
     * Finds a user by username.
     * 
     * @param username The username to search for
     * @return An Optional containing the User if found, empty otherwise
     */
    private Optional<User> findUser(String username) {
        return users.stream()
                   .filter(user -> user.getUsername().equals(username))
                   .findFirst();
    }

    /**
     * Finds a user by username and password.
     * 
     * @param username The username
     * @param password The password
     * @return An Optional containing the User if found with matching credentials, empty otherwise
     */
    private Optional<User> findUser(String username, String password) {
        return users.stream()
                   .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
                   .findFirst();
    }

    /**
     * Gets a list of all users.
     * 
     * @return A list of all users
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
