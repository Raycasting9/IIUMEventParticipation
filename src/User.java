
/**
 * Base class for all users (Admin and Student).
 * Stores common user information
 * 
 */
public abstract class User {

    protected String username;
    protected String password; 
    protected String name;
    protected String phoneNumber;
    protected String gender; 
    protected String email;
    protected String role; 

    public User(String username, String password, String name, String phoneNumber, String gender, String email, String role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.email = email; 
        this.role = role;
    }

    // Getters an Setters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    /**
     * Abstract method to display the user-specific dashboard.
     * Must be implemented by subclasses (Admin, Student).
     *
     * @param scanner The Scanner object for user input.
     * @param userManager The UserManager instance.
     * @param eventManager The EventManager instance.
     */
    public abstract void showDashboard(java.util.Scanner scanner, UserManager userManager, EventManager eventManager);

    /**
     * Provides a string representation suitable for saving to a text file.
     * Uses a delimiter (e.g., ";") to separate fields.
     * Now includes email.
     *
     * @return 
     */
    public String toFileString() {
        String delimiter = ";";
        return username + delimiter + password + delimiter + name + delimiter + phoneNumber + delimiter + gender + delimiter + email + delimiter + role;
    }

    /**
     * Creates a User object from a string read from the file.
     * 
     *
     * @param fileString The delimited string from the user file.
     * @return A User object (Admin or Student), or null if parsing fails.
     */
    public static User fromFileString(String fileString) {
        String[] parts = fileString.split(";", -1); // -1 limit preserves trailing empty strings
        // Expecting 7 parts now (username, password, name, phone, gender, email, role)
        if (parts.length == 7) {
            String username = parts[0];
            String password = parts[1];
            String name = parts[2];
            String phone = parts[3];
            String gender = parts[4];
            String email = parts[5]; // Parse email
            String role = parts[6];

            // Basic validation on loaded data 
            if (username.isEmpty() || role.isEmpty()) {
                 System.err.println("Skipping user line with empty username or role: " + fileString);
                 return null;
            }


            if ("Admin".equalsIgnoreCase(role)) {
              
                return new Admin(username, password, name, phone, gender, email);
            } else if ("Student".equalsIgnoreCase(role)) {
            
                return new Student(username, password, name, phone, gender, email);
            } else {
                 System.err.println("Skipping user line with unknown role: " + role + " in line: " + fileString);
                 return null;
            }
        } else {
             System.err.println("Skipping invalid user line (expected 7 parts, got " + parts.length + "): " + fileString);
             return null; 
        }
    }

    @Override
    public String toString() {
        // Added email to the string representation
        return "User{" +
               "username='" + username + '\'' +
               ", name='" + name + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", gender='" + gender + '\'' +
               ", email='" + email + '\'' + 
               ", role='" + role + '\'' +
               '}';
    }
}
