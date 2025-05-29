package iium.event.participation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Dashboard for Admin users.
 * Provides functionality for managing events and viewing participants.
 */
public class AdminDashboard extends BaseDashboard {
    private TableView<Event> eventTable;
    private ObservableList<Event> eventData;
    
    public AdminDashboard(Stage primaryStage, User user, UserManager userManager, EventManager eventManager) {
        super(primaryStage, user, userManager, eventManager);
    }
    
    @Override
    protected void setupContent() {
        // Create toolbar
        ToolBar toolBar = new ToolBar();
        
        Button createEventBtn = new Button("Create Event");
        Button editEventBtn = new Button("Edit Event");
        Button deleteEventBtn = new Button("Delete Event");
        Button viewParticipantsBtn = new Button("View Participants");
        Button exportParticipantsBtn = new Button("Export Participants");
        
        toolBar.getItems().addAll(createEventBtn, editEventBtn, deleteEventBtn, viewParticipantsBtn, exportParticipantsBtn);
        
        // Create event table
        eventTable = new TableView<>();
        eventData = FXCollections.observableArrayList(eventManager.getAllEvents());
        
        TableColumn<Event, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        TableColumn<Event, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        TableColumn<Event, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Event, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        
        TableColumn<Event, Integer> capacityCol = new TableColumn<>("Capacity");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        
        eventTable.getColumns().addAll(idCol, titleCol, dateCol, locationCol, capacityCol);
        eventTable.setItems(eventData);
        
        // Add components to layout
        VBox contentBox = new VBox(10);
        contentBox.setPadding(new Insets(10));
        contentBox.getChildren().addAll(toolBar, eventTable);
        
        root.setCenter(contentBox);
        
        // Event handlers
        createEventBtn.setOnAction(e -> showCreateEventDialog());
        editEventBtn.setOnAction(e -> editSelectedEvent());
        deleteEventBtn.setOnAction(e -> deleteSelectedEvent());
        viewParticipantsBtn.setOnAction(e -> viewEventParticipants());
        exportParticipantsBtn.setOnAction(e -> exportParticipantsToFile());
    }
    
    private void showCreateEventDialog() {
        // Create a dialog for event creation
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Create New Event");
        dialog.setHeaderText("Enter event details");
        
        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Create the form
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField();
        titleField.setPromptText("Event Title");
        TextField dateField = new TextField();
        dateField.setPromptText("YYYY-MM-DD HH:MM");
        TextField locationField = new TextField();
        locationField.setPromptText("Location");
        TextField capacityField = new TextField();
        capacityField.setPromptText("Capacity");
        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Event description");
        descriptionArea.setPrefRowCount(3);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Date (YYYY-MM-DD HH:MM):"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(new Label("Capacity:"), 0, 3);
        grid.add(capacityField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result to an event when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    String id = "EVT" + System.currentTimeMillis();
                    String title = titleField.getText();
                    String date = dateField.getText();
                    String location = locationField.getText();
                    int capacity = Integer.parseInt(capacityField.getText());
                    String description = descriptionArea.getText();
                    
                    return new Event(id, title, description, date, location, capacity, currentUser.getUsername());
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter valid numbers for capacity.");
                    return null;
                }
            }
            return null;
        });
        
        Optional<Event> result = dialog.showAndWait();
        result.ifPresent(event -> {
            eventManager.addEvent(event);
            eventData.setAll(eventManager.getAllEvents());
            showSuccess("Success", "Event created successfully!");
        });
    }
    
    private void editSelectedEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select an event to edit.");
            return;
        }
        
        // Similar to create dialog but pre-fill with selected event data
        Dialog<Event> dialog = new Dialog<>();
        dialog.setTitle("Edit Event");
        dialog.setHeaderText("Edit event details");
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField titleField = new TextField(selected.getTitle());
        TextField dateField = new TextField(selected.getDate());
        TextField locationField = new TextField(selected.getLocation());
        TextField capacityField = new TextField(String.valueOf(selected.getCapacity()));
        TextArea descriptionArea = new TextArea(selected.getDescription());
        descriptionArea.setPrefRowCount(3);
        
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleField, 1, 0);
        grid.add(new Label("Date (YYYY-MM-DD HH:MM):"), 0, 1);
        grid.add(dateField, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(new Label("Capacity:"), 0, 3);
        grid.add(capacityField, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    selected.setTitle(titleField.getText());
                    selected.setDate(dateField.getText());
                    selected.setLocation(locationField.getText());
                    selected.setCapacity(Integer.parseInt(capacityField.getText()));
                    selected.setDescription(descriptionArea.getText());
                    return selected;
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter valid numbers for capacity.");
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(event -> {
            eventManager.updateEvent(event);
            eventData.setAll(eventManager.getAllEvents());
            showSuccess("Success", "Event updated successfully!");
        });
    }
    
    private void deleteSelectedEvent() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select an event to delete.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText("Delete Event");
        alert.setContentText("Are you sure you want to delete the event: " + selected.getTitle() + "?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            eventManager.deleteEvent(selected.getId());
            eventData.setAll(eventManager.getAllEvents());
            showSuccess("Success", "Event deleted successfully!");
        }
    }
    
    private void viewEventParticipants() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select an event to view participants.");
            return;
        }
        
        List<String> participantNames = eventManager.getEventParticipants(selected.getId());
        
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(participantNames);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Event Participants");
        alert.setHeaderText("Participants for: " + selected.getTitle());
        alert.getDialogPane().setContent(listView);
        alert.setResizable(true);
        alert.getDialogPane().setPrefSize(300, 400);
        alert.showAndWait();
    }
    
    private void exportParticipantsToFile() {
        Event selected = eventTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Error", "Please select an event to export participants.");
            return;
        }
        
        List<String> participants = eventManager.getEventParticipants(selected.getId());
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Participants List");
        fileChooser.setInitialFileName("participants_" + selected.getId() + "_" + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt");
        
        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("Event: " + selected.getTitle() + "\n");
                writer.write("Date: " + selected.getDate() + "\n");
                writer.write("Location: " + selected.getLocation() + "\n\n");
                writer.write("PARTICIPANTS (" + participants.size() + "):\n\n");
                
                for (String participant : participants) {
                    writer.write("- " + participant + "\n");
                }
                
                showSuccess("Success", "Participants list exported successfully!");
            } catch (IOException e) {
                showError("Error", "Failed to save file: " + e.getMessage());
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
