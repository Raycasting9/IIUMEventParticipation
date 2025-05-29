package iium.event.participation;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class EventManager {
    private static final String EVENT_FILE = "events.txt";
    private final List<Event> events;
    private static EventManager instance;

    private EventManager() {
        this.events = new ArrayList<>();
        loadEvents();
    }

    public static synchronized EventManager getInstance() {
        if (instance == null) {
            instance = new EventManager();
        }
        return instance;
    }

    /**
     * Manages events (creation, deletion, editing, viewing, registration).
     * Handles event capacity.
     */

    // --- File Operations ---
    private void loadEvents() {
        File file = new File(EVENT_FILE);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                Event event = Event.fromFileString(line);
                if (event != null) {
                    events.add(event);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
    }

    private void saveEvents() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EVENT_FILE))) {
            for (Event event : events) {
                writer.println(event.toFileString());
            }
        } catch (IOException e) {
            System.err.println("Error saving events: " + e.getMessage());
        }
    }

    // --- Event Management ---
    public boolean addEvent(Event event) {
        if (event == null || event.getTitle() == null || event.getTitle().trim().isEmpty() || 
            event.getDate() == null || event.getCapacity() <= 0) {
            return false;
        }
        
        events.add(event);
        saveEvents();
        return true;
    }
    
    public boolean updateEvent(Event updatedEvent) {
        if (updatedEvent == null || updatedEvent.getId() == null) {
            return false;
        }
        
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getId().equals(updatedEvent.getId())) {
                events.set(i, updatedEvent);
                saveEvents();
                return true;
            }
        }
        return false;
    }
    
    public boolean deleteEvent(String eventId) {
        if (eventId == null) {
            return false;
        }
        
        boolean removed = events.removeIf(event -> event.getId().equals(eventId));
        if (removed) {
            saveEvents();
        }
        return removed;
    }

    // --- Getters ---
    public List<Event> getAllEvents() {
        return new ArrayList<>(events);
    }
    
    public List<Event> getEventsByOrganizer(String organizerId) {
        return events.stream()
            .filter(event -> event.getOrganizerId().equals(organizerId))
            .collect(Collectors.toList());
    }
    
    public Optional<Event> getEventById(String eventId) {
        return events.stream()
            .filter(event -> event.getId().equals(eventId))
            .findFirst();
    }

    // --- Registration Management ---
    public boolean registerParticipant(String eventId, String username) {
        if (username == null || username.trim().isEmpty() || eventId == null) {
            return false;
        }
        
        Optional<Event> eventOpt = getEventById(eventId);
        if (!eventOpt.isPresent()) {
            return false;
        }
        
        Event event = eventOpt.get();
        boolean success = event.registerParticipant(username);
        if (success) {
            saveEvents();
        }
        return success;
    }
    
    public boolean unregisterParticipant(String eventId, String username) {
        if (username == null || username.trim().isEmpty() || eventId == null) {
            return false;
        }
        
        Optional<Event> eventOpt = getEventById(eventId);
        if (!eventOpt.isPresent()) {
            return false;
        }
        
        Event event = eventOpt.get();
        boolean success = event.unregisterParticipant(username);
        if (success) {
            saveEvents();
        }
        return success;
    }
    
    public List<String> getEventParticipants(String eventId) {
        return getEventById(eventId)
            .map(Event::getParticipants)
            .map(ArrayList::new)
            .orElse(new ArrayList<>());
    }
}
