import java.io.*;
import java.util.*;

/**
 * Manages events (creation, deletion, editing, viewing, registration).
 * Handles event capacity.
 */
public class EventManager {
    private List<Event> events; 
    private Map<Integer, List<String>> registrations; 
    private final String EVENT_FILE = "events.txt";
    private final String REGISTRATION_FILE = "registrations.txt";

    public EventManager() {
        this.events = new ArrayList<>();
        this.registrations = new HashMap<>();
        loadEvents();
        loadRegistrations();
    }

    // --- File Loading ---

    private void loadEvents() {
        File file = new File(EVENT_FILE);
        if (!file.exists()) return;

        // Using try-with-resources for automatic closing
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // nextEventId is now managed within Event class constructor
            while ((line = reader.readLine()) != null) {
                Event event = Event.fromFileString(line); // Updated to parse capacity
                if (event != null) {
                    events.add(event);
                    // Static ID counter updated in Event constructor
                } else {
                     // Error message printed in Event.fromFileString
                }
            }
            System.out.println("Loaded " + events.size() + " events.");
        } catch (IOException e) {
            System.err.println("Error loading events: " + e.getMessage());
        }
    }

    private void loadRegistrations() {
        File file = new File(REGISTRATION_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length == 2) {
                    try {
                        int eventId = Integer.parseInt(parts[0]);
                        String studentUsername = parts[1];
                        // Add registration to the map
                        // computeIfAbsent is fine, it's not a stream operation
                        registrations.computeIfAbsent(eventId, k -> new ArrayList<>()).add(studentUsername);
                    } catch (NumberFormatException e) {
                         System.err.println("Skipping invalid registration line (bad event ID): " + line);
                    }
                } else {
                    System.err.println("Skipping invalid registration line (wrong format): " + line);
                }
            }
             System.out.println("Loaded registrations.");
        } catch (IOException e) {
            System.err.println("Error loading registrations: " + e.getMessage());
        }
    }

    // --- File Saving ---

    private void saveEvents() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(EVENT_FILE, false))) { // Overwrite
            for (Event event : events) {
                writer.println(event.toFileString()); // Updated to include capacity
            }
        } catch (IOException e) {
            System.err.println("Error saving events: " + e.getMessage());
        }
    }

    private void saveRegistrations() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(REGISTRATION_FILE, false))) { // Overwrite
            for (Map.Entry<Integer, List<String>> entry : registrations.entrySet()) {
                int eventId = entry.getKey();
                for (String username : entry.getValue()) {
                    writer.println(eventId + ";" + username);
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving registrations: " + e.getMessage());
        }
    }

    // --- Event Management (Admin) ---

    public void createEvent(Scanner scanner) {
        System.out.println("\n--- Create New Event ---");
        System.out.print("Enter Event Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Event Description: ");
        String desc = scanner.nextLine();
        System.out.print("Enter Event Date (e.g., YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Event Location: ");
        String loc = scanner.nextLine();

        int capacity = 0;
        boolean validCapacity = false;
        while (!validCapacity) {
            System.out.print("Enter Event Capacity (max participants, must be > 0): ");
            try {
                capacity = scanner.nextInt();
                if (capacity > 0) {
                    validCapacity = true;
                } else {
                    System.out.println("Capacity must be a positive number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a whole number for capacity.");
                scanner.next(); 
            }
        }
        scanner.nextLine();


        // Basic validation for other fields
         if (name.trim().isEmpty() || date.trim().isEmpty() || loc.trim().isEmpty()) {
             System.out.println("Event creation failed: Name, Date, and Location cannot be empty.");
             return;
         }
         if (name.contains(";") || desc.contains(";") || date.contains(";") || loc.contains(";")) {
             System.out.println("Event creation failed: Fields cannot contain the character ';'.");
             return;
         }


        Event newEvent = new Event(name, desc, date, loc, capacity); 
        events.add(newEvent);
        saveEvents(); 
        System.out.println("Event '" + name + "' created successfully with ID: " + newEvent.getEventId() + " and Capacity: " + capacity);
    }

     /**
      * Finds an event by its ID using a simple loop.
      * @param eventId The ID of the event to find.
      * @return An Optional containing the Event if found, otherwise empty.
      */
     private Optional<Event> findEventById(int eventId) {
         for (Event event : events) {
             if (event.getEventId() == eventId) {
                 return Optional.of(event);
             }
         }
         return Optional.empty(); 
     }

    public void deleteEvent(Scanner scanner) {
        System.out.println("\n--- Delete Event ---");
        viewAllEventsHeaderOnly(); 
        if (events.isEmpty()) return;

        System.out.print("Enter the ID of the event to delete: ");
        int eventId;
        try {
            eventId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); 
            return;
        }
        scanner.nextLine(); 

        Optional<Event> eventOpt = findEventById(eventId);

        if (eventOpt.isPresent()) {
            Event eventToDelete = eventOpt.get();
            events.remove(eventToDelete); // Remove from event list
            registrations.remove(eventId); // Remove all registrations for this event

            saveEvents();
            saveRegistrations();
            System.out.println("Event ID " + eventId + " and its registrations deleted successfully.");
        } else {
            System.out.println("Event with ID " + eventId + " not found.");
        }
    }

    public void editEvent(Scanner scanner) {
        System.out.println("\n--- Edit Event Details ---");
         viewAllEventsHeaderOnly(); // Show events to help user choose
         if (events.isEmpty()) return;

        System.out.print("Enter the ID of the event to edit: ");
         int eventId;
         try {
             eventId = scanner.nextInt();
         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a number.");
             scanner.next(); // Consume invalid input
             return;
         }
        scanner.nextLine(); // Consume newline

        Optional<Event> eventOpt = findEventById(eventId);

        if (eventOpt.isPresent()) {
            Event eventToEdit = eventOpt.get();
            System.out.println("Editing Event: " + eventToEdit.getEventName() + " (ID: " + eventId + ")");
            System.out.println("Current details: \n" + eventToEdit); // toString now includes capacity
            System.out.println("Enter new details (leave blank to keep current value):");

            System.out.print("New Name [" + eventToEdit.getEventName() + "]: ");
            String newName = scanner.nextLine();
            System.out.print("New Description [" + eventToEdit.getDescription() + "]: ");
            String newDesc = scanner.nextLine();
            System.out.print("New Date [" + eventToEdit.getDate() + "]: ");
            String newDate = scanner.nextLine();
            System.out.print("New Location [" + eventToEdit.getLocation() + "]: ");
            String newLoc = scanner.nextLine();
            System.out.print("New Capacity [" + eventToEdit.getCapacity() + "]: ");
            String newCapacityStr = scanner.nextLine();


            boolean changed = false;
            // Validate and set basic fields
            if (!newName.trim().isEmpty() && !newName.contains(";")) {
                eventToEdit.setEventName(newName.trim());
                changed = true;
            }
             if (!newDesc.contains(";")) { // Allow description update
                 if (!newDesc.equals(eventToEdit.getDescription())) {
                     eventToEdit.setDescription(newDesc);
                     changed = true;
                 }
            }
            if (!newDate.trim().isEmpty() && !newDate.contains(";")) {
                eventToEdit.setDate(newDate.trim());
                changed = true;
            }
            if (!newLoc.trim().isEmpty() && !newLoc.contains(";")) {
                eventToEdit.setLocation(newLoc.trim());
                changed = true;
            }

             // Validate and set capacity
             if (!newCapacityStr.trim().isEmpty()) {
                 try {
                     int newCapacity = Integer.parseInt(newCapacityStr.trim());
                     int currentRegistrations = registrations.getOrDefault(eventId, Collections.emptyList()).size();
                     if (newCapacity > 0 && newCapacity < currentRegistrations) {
                         System.out.println("Warning: New capacity (" + newCapacity + ") is less than the number of currently registered students (" + currentRegistrations + ").");

                     }
                     if (newCapacity != eventToEdit.getCapacity()) {
                         eventToEdit.setCapacity(newCapacity); 
                         changed = true;
                     }

                 } catch (NumberFormatException e) {
                     System.out.println("Invalid capacity format entered (" + newCapacityStr + "). Keeping original value.");
                 }
             }


             // Final check for disallowed characters before saving
             if (newName.contains(";") || newDesc.contains(";") || newDate.contains(";") || newLoc.contains(";")) {
                 System.out.println("Edit failed: Fields cannot contain the character ';'. No changes saved.");
                 // Revert changes if necessary or reload object state - simpler to just not save
                 return;
             }


            if (changed) {
                saveEvents(); // Save changes to file
                System.out.println("Event details updated successfully.");
            } else {
                System.out.println("No changes made.");
            }

        } else {
            System.out.println("Event with ID " + eventId + " not found.");
        }
    }

     public void viewEventDetailsById(Scanner scanner) {
         System.out.println("\n--- View Event Details ---");
         if (events.isEmpty()) {
             System.out.println("No events available to view.");
             return;
         }
         viewAllEventsHeaderOnly(); // Show IDs and Names

         System.out.print("Enter the ID of the event to view details: ");
         int eventId;
          try {
             eventId = scanner.nextInt();
         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a number.");
             scanner.next();
             return;
         }
         scanner.nextLine(); 

         Optional<Event> eventOpt = findEventById(eventId);
         if (eventOpt.isPresent()) {
             Event event = eventOpt.get();
             System.out.println("\n--- Event Details ---");
             System.out.println(event); // Uses Event's toString() 
             // Show participants and registration status
             List<String> participants = registrations.getOrDefault(eventId, Collections.emptyList());
             System.out.println("  Registered Students: " + participants.size() + " / " + event.getCapacity()); // Show count vs capacity
             if (!participants.isEmpty()) {
                 // Simple join for display
                 StringBuilder participantList = new StringBuilder();
                 for(int i=0; i < participants.size(); i++) {
                     participantList.append(participants.get(i));
                     if (i < participants.size() - 1) {
                         participantList.append(", ");
                     }
                 }
                 System.out.println("  Participants: " + participantList.toString());
             }
             System.out.println("--------------------");
         } else {
             System.out.println("Event with ID " + eventId + " not found.");
         }
     }

    public void generateParticipantListFile(Scanner scanner) {
        System.out.println("\n--- Generate Participant List File ---");
         viewAllEventsHeaderOnly(); // Show IDs and Names
         if (events.isEmpty()) return;

        System.out.print("Enter the ID of the event to get participants for: ");
        int eventId;
         try {
             eventId = scanner.nextInt();
         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a number.");
             scanner.next();
             return;
         }
        scanner.nextLine(); 

        Optional<Event> eventOpt = findEventById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            List<String> participants = registrations.getOrDefault(eventId, Collections.emptyList());
            // Sanitize filename 
            String safeEventName = event.getEventName().replaceAll("[^a-zA-Z0-9]", "_");
            String fileName = "event_" + eventId + "_" + safeEventName + "_participants.txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("Participants for Event: " + event.getEventName() + " (ID: " + eventId + ")");
                writer.println("Date: " + event.getDate());
                writer.println("Location: " + event.getLocation());
                writer.println("Capacity: " + event.getCapacity()); 
                writer.println("Registered: " + participants.size()); 
                writer.println("-------------------------------------");
                if (participants.isEmpty()) {
                    writer.println("No registered participants.");
                } else {
                    writer.println("Registered Student Usernames ("+ participants.size() +"):");
                    for (String username : participants) { 
                        writer.println(username);
                    }
                }
                System.out.println("Participant list saved to file: " + fileName);
            } catch (IOException e) {
                System.err.println("Error writing participant list to file '" + fileName +"': " + e.getMessage());
            }
        } else {
            System.out.println("Event with ID " + eventId + " not found.");
        }
    }

    // --- Event Viewing (Student) ---

     private void viewAllEventsHeaderOnly() {
         System.out.println("\n--- Available Events ---");
         if (events.isEmpty()) {
             System.out.println("No events found.");
             return;
         }

         System.out.printf("%-5s %-10s %-12s %s%n", "ID", "Capacity", "Registered", "Name");
         System.out.printf("%-5s %-10s %-12s %s%n", "--", "--------", "----------", "----");
         for (Event event : events) { // Use enhanced for loop
             int registeredCount = registrations.getOrDefault(event.getEventId(), Collections.emptyList()).size();
             System.out.printf("%-5d %-10d %-12d %s%n",
                               event.getEventId(),
                               event.getCapacity(),
                               registeredCount,
                               event.getEventName());
         }
         System.out.println("--------------------------------------------"); 
     }

    public void viewAllEvents() {
        System.out.println("\n--- All Available Events ---");
        if (events.isEmpty()) {
            System.out.println("No events found.");
            return;
        }
        for (Event event : events) {
            System.out.println("--------------------");
            System.out.println(event); // Uses Event's toString()
             List<String> participants = registrations.getOrDefault(event.getEventId(), Collections.emptyList());
             System.out.println("  Registered: " + participants.size() + " / " + event.getCapacity()); // Show count vs capacity
        }
        System.out.println("--------------------");
    }

    // --- Student Actions ---

    public void registerStudentForEvent(Scanner scanner, String studentUsername) {
        System.out.println("\n--- Register for an Event ---");
        viewAllEventsHeaderOnly(); // Show available events 
        if (events.isEmpty()) return;

        System.out.print("Enter the ID of the event to register for: ");
         int eventId;
          try {
             eventId = scanner.nextInt();
         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a number.");
             scanner.next(); 
             return;
         }
        scanner.nextLine(); 

        Optional<Event> eventOpt = findEventById(eventId);
        if (eventOpt.isPresent()) {
            Event event = eventOpt.get();
            List<String> currentRegistrants = registrations.computeIfAbsent(eventId, k -> new ArrayList<>());

            // Check if already registered
            if (currentRegistrants.contains(studentUsername)) {
                System.out.println("You are already registered for this event (ID: " + eventId + ").");
            }
            // Check if event is full
            else if (currentRegistrants.size() >= event.getCapacity()) {
                 System.out.println("Sorry, registration failed. Event '" + event.getEventName() + "' (ID: " + eventId + ") is already full (" + currentRegistrants.size() + "/" + event.getCapacity() + ").");
            }
            // If not registered and not full, proceed
            else {
                currentRegistrants.add(studentUsername);
                saveRegistrations(); // Save the change
                System.out.println("Successfully registered for event: " + event.getEventName() + " (Current registrations: " + currentRegistrants.size() + "/" + event.getCapacity() + ")");
            }
        } else {
            System.out.println("Event with ID " + eventId + " not found.");
        }
    }

    public void viewRegisteredEvents(String studentUsername) {
        System.out.println("\n--- My Registered Events ---");
        List<Event> registeredEventsList = new ArrayList<>();

        // Iterate through the registrations map to find events the user is registered for
        for (Map.Entry<Integer, List<String>> entry : registrations.entrySet()) {
            int eventId = entry.getKey();
            List<String> registrants = entry.getValue();
            if (registrants.contains(studentUsername)) {
                // Find the event details using the ID and add if present
                Optional<Event> eventOpt = findEventById(eventId);
                if (eventOpt.isPresent()) {
                     registeredEventsList.add(eventOpt.get());
                }
               
            }
        }

        if (registeredEventsList.isEmpty()) {
            System.out.println("You have not registered for any events.");
        } else {
            System.out.println("You are registered for the following events:");
             for (Event event : registeredEventsList) {
                 System.out.println("--------------------");
                 System.out.println(event); // Uses Event's toString()
                 // Also show registration count for context
                 int registeredCount = registrations.getOrDefault(event.getEventId(), Collections.emptyList()).size();
                 System.out.println("  Registered: " + registeredCount + " / " + event.getCapacity());
             }
             System.out.println("--------------------");
        }
    }

    public void unregisterStudentFromEvent(Scanner scanner, String studentUsername) {
        System.out.println("\n--- Unregister from an Event ---");
        // Show only events the student is registered 
        viewRegisteredEvents(studentUsername);

        // Get the list of event IDs the student is registered 
        List<Integer> registeredEventIds = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : registrations.entrySet()) {
            if (entry.getValue().contains(studentUsername)) {
                registeredEventIds.add(entry.getKey());
            }
        }

         if (registeredEventIds.isEmpty()) {
            
             if (!events.isEmpty()) { 
                System.out.println("You are not registered for any events to unregister from.");
             }
             return;
         }


        System.out.print("Enter the ID of the event to unregister from: ");
        int eventId;
         try {
             eventId = scanner.nextInt();
         } catch (InputMismatchException e) {
             System.out.println("Invalid input. Please enter a number.");
             scanner.next(); 
             return;
         }
        scanner.nextLine(); 

        // Check if the entered ID is actually one they are registered for
        if (!registeredEventIds.contains(eventId)) {
             System.out.println("You are not registered for event ID " + eventId + ", or it's not an event you can unregister from.");
             return;
         }


        List<String> registrants = registrations.get(eventId);
        // Check if registrants list exists and removal is successful
        if (registrants != null && registrants.remove(studentUsername)) {
            // If the list becomes empty after removal, remove the eventId entry from the map
            if (registrants.isEmpty()) {
                registrations.remove(eventId);
            }
            saveRegistrations(); // Save the change
            // Get event name for confirmation message (handle case where event might have been deleted)
            String eventName = findEventById(eventId)
                                 .map(Event::getEventName) // Get name if event exists
                                 .orElse("(Event Name Not Found)"); // Default if event deleted
            System.out.println("Successfully unregistered from event: " + eventName + " (ID: " + eventId + ")");
        } else {
            System.out.println("Could not unregister. Please ensure you are registered for event ID " + eventId + ".");
        }
    }
}
