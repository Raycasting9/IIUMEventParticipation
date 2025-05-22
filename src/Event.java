
/**
 * Represents an event in the system
 */
public class Event {
    private static int nextEventId = 1;
    private int eventId;
    private String eventName;
    private String description;
    private String date; 
    private String location;
    private int capacity; 

    // Constructor for creating a new event
    public Event(String eventName, String description, String date, String location, int capacity) {
        this.eventId = nextEventId++; 
        this.eventName = eventName;
        this.description = description;
        this.date = date;
        this.location = location;
        this.capacity = Math.max(1, capacity); 
    }

    // Constructor for loading from file 
    public Event(int eventId, String eventName, String description, String date, String location, int capacity) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.description = description;
        this.date = date;
        this.location = location;
        this.capacity = Math.max(1, capacity);
        nextEventId = Math.max(nextEventId, eventId + 1);
    }

    // Getters
    public int getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    // Setters 
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCapacity(int capacity) {
        this.capacity = Math.max(1, capacity);
    }

     /**
     * Provides a string representation suitable for saving to a text file.
     * Uses a delimiter (e.g., ";") to separate fields.
     * Includes capacity now.
     *
     * @return A delimited string representation of the event.
     */
    public String toFileString() {
        String delimiter = ";";
        return eventId + delimiter + eventName + delimiter + description + delimiter + date + delimiter + location + delimiter + capacity;
    }

     /**
     * Creates an Event object from a string read from the file.
     * 
     *
     * @param fileString The delimited string from the event file.
     * @return An Event object, or null if parsing fails.
     */
     public static Event fromFileString(String fileString) {
        String[] parts = fileString.split(";", -1);
        if (parts.length == 6) {
            try {
                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String desc = parts[2];
                String date = parts[3];
                String loc = parts[4];
                int capacity = Integer.parseInt(parts[5]);

                return new Event(id, name, desc, date, loc, capacity);

            } catch (NumberFormatException e) {
                System.err.println("Error parsing event ID or capacity from line: " + fileString + " - " + e.getMessage());
                return null;
            }
        } else {
             System.err.println("Skipping invalid event line (expected 6 parts, got " + parts.length + "): " + fileString);
             return null; 
        }
    }


    @Override
    public String toString() {
        return "Event ID: " + eventId + "\n" +
               "  Name: " + eventName + "\n" +
               "  Description: " + description + "\n" +
               "  Date: " + date + "\n" +
               "  Location: " + location + "\n" +
               "  Capacity: " + capacity; 
    }
}
