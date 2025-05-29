package iium.event.participation;

/**
 * Base class for all users (Admin and Student).
 * Stores common user information
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

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    
    /**
     * Formats user data for file storage.
     * @return A string representation of the user for saving to file
     */
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s", 
            username, 
            password, 
            getClass().getSimpleName(),
            name,
            email);
    }

    @Override
    public String toString() {
        return String.format("Username: %s\nName: %s\nEmail: %s\nRole: %s", 
            username, name, email, getClass().getSimpleName());
    }
}
