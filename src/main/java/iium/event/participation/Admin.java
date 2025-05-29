package iium.event.participation;

/**
 * Represents an Admin user, inheriting from User.
 * Contains methods for admin-specific actions.
 */
public class Admin extends User {
    /**
     * Constructor for Admin with all fields.
     */
    public Admin(String username, String password, String name, String phoneNumber, String gender, String email, String role) {
        super(username, password, name, phoneNumber, gender, email, role);
    }
    
    // Admin-specific methods will be added here
    
    @Override
    public String toString() {
        return String.format("Admin{username='%s', name='%s', email='%s'}", 
            getUsername(), getName(), getEmail());
    }
}
