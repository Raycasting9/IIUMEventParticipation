package iium.event.participation;

import java.io.Serializable;
import java.util.*;

public class Event implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String title;
    private String description;
    private String date;
    private String location;
    private int capacity;
    private int registeredCount;
    private String organizerId;
    private final List<String> participants;

    public Event(String id, String title, String description, String date, String location, int capacity, String organizerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.capacity = Math.max(1, capacity);
        this.organizerId = organizerId;
        this.registeredCount = 0;
        this.participants = new ArrayList<>();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getOrganizerId() {
        return organizerId;
    }
    
    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }
    
    public int getAvailableSpaces() {
        return capacity - registeredCount;
    }
    
    public int getRegisteredCount() {
        return registeredCount;
    }
    
    public boolean registerParticipant(String username) {
        if (registeredCount >= capacity || participants.contains(username)) {
            return false;
        }
        participants.add(username);
        registeredCount++;
        return true;
    }
    
    public boolean unregisterParticipant(String username) {
        boolean removed = participants.remove(username);
        if (removed) {
            registeredCount--;
        }
        return removed;
    }
    
    public List<String> getParticipants() {
        return new ArrayList<>(participants);
    }
    
    public String toFileString() {
        return String.format("%s,%s,%s,%s,%s,%d,%d,%s", 
            id, title, description, date, location, capacity, registeredCount, organizerId);
    }
    
    public static Event fromFileString(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 8) return null;
        
        try {
            String id = parts[0];
            String title = parts[1];
            String description = parts[2];
            String date = parts[3];
            String location = parts[4];
            int capacity = Integer.parseInt(parts[5]);
            int registeredCount = Integer.parseInt(parts[6]);
            String organizerId = parts[7];
            
            Event event = new Event(id, title, description, date, location, capacity, organizerId);
            event.registeredCount = registeredCount;
            
            // Add participants if any
            for (int i = 8; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    event.participants.add(parts[i]);
                }
            }
            
            return event;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("ID: %s\nTitle: %s\nDate: %s\nCapacity: %d\nOrganizer: %s\nDescription: %s",
            id, title, date, capacity, organizerId, description);
    }
}
