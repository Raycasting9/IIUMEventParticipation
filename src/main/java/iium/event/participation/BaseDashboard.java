package iium.event.participation;

import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Base class for all dashboards.
 * Provides common functionality and layout for both Admin and Student dashboards.
 */
public class BaseDashboard {
    protected final Stage primaryStage;
    protected final User currentUser;
    protected final UserManager userManager;
    protected final EventManager eventManager;
    protected BorderPane root;
    protected MenuBar menuBar;

    public BaseDashboard(Stage primaryStage, User user, UserManager userManager, EventManager eventManager) {
        this.primaryStage = primaryStage;
        this.currentUser = user;
        this.userManager = userManager;
        this.eventManager = eventManager;
        this.root = new BorderPane();
        setupUI();
    }

    /**
     * Get the root node of this dashboard.
     * @return The root BorderPane
     */
    public BorderPane getRoot() {
        return root;
    }
    
    protected final void setupUI() {
        // Initialize root layout
        root = new BorderPane();
        
        // Setup menu bar
        setupMenuBar();
        root.setTop(menuBar);
        
        // Setup content
        setupContent();
    }
    
    protected void setupMenuBar() {
        // Create menu bar
        menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem logoutItem = new MenuItem("Logout");
        MenuItem exitItem = new MenuItem("Exit");
        fileMenu.getItems().addAll(logoutItem, exitItem);
        
        // User menu
        Menu userMenu = new Menu("Profile");
        MenuItem viewProfileItem = new MenuItem("View Profile");
        MenuItem changePasswordItem = new MenuItem("Change Password");
        userMenu.getItems().addAll(viewProfileItem, changePasswordItem);
        
        menuBar.getMenus().addAll(fileMenu, userMenu);
        
        // Event handlers
        logoutItem.setOnAction(e -> logout());
        exitItem.setOnAction(e -> System.exit(0));
        viewProfileItem.setOnAction(e -> showProfile());
        changePasswordItem.setOnAction(e -> showChangePassword());
        
        // Add menu bar to top of border pane
        root.setTop(menuBar);
        
        // Set up the main content
        setupContent();
    }
    
    protected void setupContent() {
        // Default empty implementation
        root.setCenter(new Label("Welcome to the Dashboard"));
    }
    
    protected void showProfile() {
        // Show user profile information
        // This can be overridden by subclasses for custom profile views
        // For now, just show a simple dialog with user info
        String profileInfo = String.format(
            "Name: %s\n" +
            "Username: %s\n" +
            "Email: %s\n" +
            "Phone: %s\n" +
            "Gender: %s\n" +
            "Role: %s",
            currentUser.getName(),
            currentUser.getUsername(),
            currentUser.getEmail(),
            currentUser.getPhoneNumber(),
            currentUser.getGender(),
            currentUser.getRole()
        );
        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("User Profile");
        alert.setHeaderText("Profile Information");
        alert.setContentText(profileInfo);
        alert.showAndWait();
    }
    
    protected void showChangePassword() {
        // Show change password dialog
        // This can be overridden by subclasses for custom password change logic
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter your new password");
        dialog.setContentText("New Password:");
        
        dialog.showAndWait().ifPresent(newPassword -> {
            if (!newPassword.trim().isEmpty()) {
                currentUser.setPassword(newPassword);
                userManager.saveUsers();
                
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Password changed successfully!");
                alert.showAndWait();
            }
        });
    }
    
    protected void logout() {
        // Show login screen again
        primaryStage.close();
        
        Stage newStage = new Stage();
        EventAppFX app = new EventAppFX(newStage, userManager, eventManager);
        newStage.setScene(app.createLoginScene());
        newStage.setTitle("IIUM Event Participation System");
        newStage.show();
    }
    
    // Scene creation handled by the application class
}
