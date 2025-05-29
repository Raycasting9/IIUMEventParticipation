package iium.event.participation;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main application class for the Event Participation System.
 * Handles the main menu and user interactions.
 */
public class Main extends Application {
    private UserManager userManager;
    private EventManager eventManager;

    @Override
    public void start(Stage primaryStage) {
        try {
            userManager = UserManager.getInstance();
            eventManager = EventManager.getInstance();
            
            // Initialize the login screen
            EventAppFX app = new EventAppFX(primaryStage, userManager, eventManager);
            primaryStage.setTitle("IIUM Event Participation System");
            primaryStage.setScene(app.createLoginScene());
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to start application: " + e.getMessage());
        }
    }

    private static void showError(String message) {
        System.err.println("ERROR: " + message);
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Application Error");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        try {
            // Check if JavaFX is properly installed
            Class.forName("javafx.application.Application");
            launch(args);
        } catch (ClassNotFoundException e) {
            System.err.println("ERROR: JavaFX not found. Please ensure JavaFX is properly installed and configured.");
            System.err.println("Run the application with the following VM arguments:");
            System.err.println("--module-path \"C:/javafx-sdk-24.0.1/lib\" --add-modules javafx.controls,javafx.fxml");
            System.exit(1);
        } catch (Exception e) {
            showError("Failed to launch application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
