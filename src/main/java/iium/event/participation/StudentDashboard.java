package iium.event.participation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;
import java.util.Optional;

/**
 * Dashboard for Student users.
 * Provides functionality for viewing and registering for events.
 */
public class StudentDashboard extends BaseDashboard {
    private TableView<Event> eventTable;
    private TableView<Event> registeredEventsTable;
    private ObservableList<Event> eventData;
    private ObservableList<Event> registeredEventData;
    private Student currentStudent;
    
    public StudentDashboard(Stage primaryStage, User user, UserManager userManager, EventManager eventManager) {
        super(primaryStage, user, userManager, eventManager);
        if (user instanceof Student) {
            this.currentStudent = (Student) user;
        }
    }
    
    @Override
    protected void setupContent() {
        // Don't call super.setupContent() since we're replacing the content
        // Create tab pane for different views
        TabPane tabPane = new TabPane();
        
        // Tab 1: Available Events
        Tab availableEventsTab = new Tab("Available Events");
        availableEventsTab.setClosable(false);
        availableEventsTab.setContent(createAvailableEventsView());
        
        // Tab 2: My Registered Events
        Tab myEventsTab = new Tab("My Events");
        myEventsTab.setClosable(false);
        myEventsTab.setContent(createMyEventsView());
        
        tabPane.getTabs().addAll(availableEventsTab, myEventsTab);
        
        // Add tab pane to layout
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.getChildren().add(tabPane);
        
        // Add to root layout
        root.setCenter(contentBox);
        
        root.setCenter(contentBox);
        
        // Load events
        refreshEvents();
    }
    
    private VBox createAvailableEventsView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(10));
        
        // Toolbar
        ToolBar toolBar = new ToolBar();
        Button registerBtn = new Button("Register for Selected Event");
        Button refreshBtn = new Button("Refresh");
        toolBar.getItems().addAll(registerBtn, refreshBtn);
        
        // Event table
        eventTable = new TableView<>();
        eventData = FXCollections.observableArrayList();
        
        TableColumn<Event, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Event, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        TableColumn<Event, Integer> availableCol = new TableColumn<>("Available");
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availableSpaces"));
        
        eventTable.getColumns().addAll(idCol, titleCol, dateCol, locationCol, availableCol);
        eventTable.setItems(eventData);
        
        // Event details area
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(4);
        
        // Show event details when selected
        eventTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                detailsArea.setText(
                    "Description: " + newSelection.getDescription() + "\n\n" +
                    "Capacity: " + newSelection.getAvailableSpaces() + "/" + newSelection.getCapacity() + " participants\n" +
                    "Organizer: " + newSelection.getOrganizerId()
                );
            } else {
                detailsArea.clear();
            }
        });
        
        // Set up event handlers
        registerBtn.setOnAction(e -> registerForSelectedEvent());
        refreshBtn.setOnAction(e -> refreshEvents());
        
        // Add components to view
        view.getChildren().addAll(toolBar, eventTable, new Label("Event Details:"), detailsArea);
        VBox.setVgrow(eventTable, Priority.ALWAYS);
        
        return view;
    }
    
    private VBox createMyEventsView() {
        VBox view = new VBox(10);
        view.setPadding(new Insets(10));
        
        // Toolbar
        ToolBar toolBar = new ToolBar();
        Button unregisterBtn = new Button("Unregister from Event");
        Button refreshBtn = new Button("Refresh");
        toolBar.getItems().addAll(unregisterBtn, refreshBtn);
        
        // Registered events table
        registeredEventsTable = new TableView<>();
        registeredEventData = FXCollections.observableArrayList();
        
        TableColumn<Event, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Event, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        registeredEventsTable.getColumns().addAll(idCol, titleCol, dateCol, locationCol);
        registeredEventsTable.setItems(registeredEventData);
        
        // Event details area
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefRowCount(4);
        
        // Show event details when selected
        registeredEventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                detailsArea.setText(
                    "Description: " + newSelection.getDescription() + "\n\n" +
                    "Capacity: " + newSelection.getAvailableSpaces() + "/" + newSelection.getCapacity() + " participants\n" +
                    "Organizer: " + newSelection.getOrganizerId()
                );
            } else {
                detailsArea.clear();
            }
        });
        
        // Set up event handlers
        unregisterBtn.setOnAction(e -> unregisterFromSelectedEvent());
        refreshBtn.setOnAction(e -> refreshEvents());
        
        // Add components to view
        view.getChildren().addAll(toolBar, registeredEventsTable, new Label("Event Details:"), detailsArea);
        VBox.setVgrow(registeredEventsTable, Priority.ALWAYS);
        
        // Load events
        refreshEvents();
        
        return view;
    }
    
    private void refreshEvents() {
        loadEvents();
        refreshRegisteredEvents();
    }
    
    private void loadEvents() {
        if (currentStudent == null) return;
        
        List<Event> allEvents = eventManager.getAllEvents();
        // Filter out events the student is already registered for
        allEvents.removeIf(event -> currentStudent.isRegisteredForEvent(event.getId()));
        eventData.setAll(allEvents);
    }
    
    private void refreshRegisteredEvents() {
        if (currentStudent == null) return;
        
        registeredEventData.clear();
        for (String eventId : currentStudent.getRegisteredEvents()) {
            eventManager.getEventById(eventId).ifPresent(registeredEventData::add);
        }
    }
    
    private void registerForSelectedEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select an event to register.");
            return;
        }
        
        if (selected.getAvailableSpaces() <= 0) {
            showError("Error", "This event is already full.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Registration");
        confirm.setHeaderText("Register for Event");
        confirm.setContentText("Are you sure you want to register for: " + selected.getTitle() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (eventManager.registerParticipant(selected.getId(), currentUser.getUsername()) &&
                currentStudent.registerForEvent(selected.getId())) {
                
                userManager.saveUsers();
                refreshEvents();
                showSuccess("Success", "Successfully registered for the event!");
            } else {
                showError("Error", "Failed to register for the event. Please try again.");
            }
        }
    }
    
    private void unregisterFromSelectedEvent() {
        Event selected = registeredEventsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select an event to unregister from.");
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Unregistration");
        confirm.setHeaderText("Unregister from Event");
        confirm.setContentText("Are you sure you want to unregister from: " + selected.getTitle() + "?");
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (eventManager.unregisterParticipant(selected.getId(), currentUser.getUsername()) &&
                currentStudent.unregisterFromEvent(selected.getId())) {
                
                userManager.saveUsers();
                refreshEvents();
                showSuccess("Success", "Successfully unregistered from the event!");
            } else {
                showError("Error", "Failed to unregister from the event. Please try again.");
            }
        }
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
