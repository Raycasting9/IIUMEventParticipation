package iium.event.participation;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Student user, inheriting from User.
 * Contains methods for student-specific actions.
 */
public class Student extends User {
    private List<String> registeredEvents; // List of event IDs that the student is registered for

    /**
     * Constructor for Student with all fields.
     */
    public Student(String username, String password, String name, String phoneNumber, String gender, String email, String role) {
        super(username, password, name, phoneNumber, gender, email, role);
        this.registeredEvents = new ArrayList<>();
    }
    
    /**
     * Registers the student for an event.
     * @param eventId The ID of the event to register for
     * @return true if registration was successful, false if already registered
     */
    public boolean registerForEvent(String eventId) {
        if (!isRegisteredForEvent(eventId)) {
            registeredEvents.add(eventId);
            return true;
        }
        return false;
    }
    
    /**
     * Unregisters the student from an event.
     * @param eventId The ID of the event to unregister from
     * @return true if unregistration was successful, false if not registered
     */
    public boolean unregisterFromEvent(String eventId) {
        return registeredEvents.remove(eventId);
    }
    
    /**
     * Checks if the student is registered for a specific event.
     * @param eventId The ID of the event to check
     * @return true if registered, false otherwise
     */
    public boolean isRegisteredForEvent(String eventId) {
        return registeredEvents.contains(eventId);
    }
    
    /**
     * Gets a list of all event IDs the student is registered for.
     * @return List of event IDs
     */
    public List<String> getRegisteredEvents() {
        return new ArrayList<>(registeredEvents);
    }
    
    @Override
    public String toString() {
        return String.format("Student{username='%s', name='%s', email='%s', registeredEvents=%d}", 
            getUsername(), getName(), getEmail(), registeredEvents.size());
    }
}
