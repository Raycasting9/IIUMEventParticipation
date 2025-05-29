package iium.event.participation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class EventAppFX extends Application {
    private Stage primaryStage;
    private UserManager userManager;
    private EventManager eventManager;
    private StackPane root;
    private GridPane loginForm;
    private GridPane registrationForm;
    // No unused fields

    public EventAppFX(Stage primaryStage, UserManager userManager, EventManager eventManager) {
        this.primaryStage = primaryStage;
        this.userManager = userManager;
        this.eventManager = eventManager;
    }


    public Scene createLoginScene() {
        root = new StackPane();
        
        // Create forms
        loginForm = createLoginForm();
        registrationForm = createRegistrationForm();
        
        // Initially show login form
        root.getChildren().add(loginForm);
        
        Scene scene = new Scene(root, 400, 400);
        return scene;
    }

    private GridPane createLoginForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Username
        Label userName = new Label("Username:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        // Password
        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        // Login button
        Button loginBtn = new Button("Login");
        grid.add(loginBtn, 1, 3);
        GridPane.setHalignment(loginBtn, HPos.RIGHT);

        // Register button
        Button registerBtn = new Button("Register");
        grid.add(registerBtn, 1, 4);
        GridPane.setHalignment(registerBtn, HPos.RIGHT);

        // Status label
        Label statusLabel = new Label();
        grid.add(statusLabel, 1, 5);
        GridPane.setHalignment(statusLabel, HPos.RIGHT);

        // Event handlers
        loginBtn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();
            
            if (username.isEmpty() || password.isEmpty()) {
                statusLabel.setText("Please enter both username and password");
                return;
            }
            
            User user = userManager.loginUser(username, password);
            if (user != null) {
                statusLabel.setText("Login successful!");
                
                // Clear fields
                userTextField.clear();
                pwBox.clear();
                
                // Show appropriate dashboard based on user type
                showDashboard(user);
            } else {
                statusLabel.setText("Invalid username or password!");
            }
        });

        registerBtn.setOnAction(e -> {
            root.getChildren().remove(loginForm);
            root.getChildren().add(registrationForm);
        });

        return grid;
    }

    private GridPane createRegistrationForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        // Title
        Label titleLabel = new Label("Registration Form");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        grid.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, HPos.CENTER);

        // Full Name
        Label nameLabel = new Label("Full Name:");
        grid.add(nameLabel, 0, 1);
        TextField nameField = new TextField();
        grid.add(nameField, 1, 1);

        // Username
        Label userName = new Label("Username:");
        grid.add(userName, 0, 2);
        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 2);

        // Password
        Label pw = new Label("Password:");
        grid.add(pw, 0, 3);
        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 3);

        // Phone Number
        Label phoneLabel = new Label("Phone Number:");
        grid.add(phoneLabel, 0, 4);
        TextField phoneField = new TextField();
        grid.add(phoneField, 1, 4);

        // Gender
        Label genderLabel = new Label("Gender:");
        grid.add(genderLabel, 0, 5);
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female", "Other");
        genderCombo.setValue("Male");
        grid.add(genderCombo, 1, 5);

        // User Type
        Label userTypeLabel = new Label("Register as:");
        grid.add(userTypeLabel, 0, 6);
        ComboBox<String> userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll("Student", "Admin");
        userTypeCombo.setValue("Student");
        grid.add(userTypeCombo, 1, 6);

        // Add some spacing
        grid.setVgap(8);

        // Register button
        Button registerBtn = new Button("Register");
        registerBtn.setDefaultButton(true);
        registerBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        grid.add(registerBtn, 1, 8);
        GridPane.setHalignment(registerBtn, HPos.RIGHT);

        // Back to login button
        Button backBtn = new Button("Back to Login");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        grid.add(backBtn, 0, 8);
        GridPane.setHalignment(backBtn, HPos.LEFT);

        // Status label
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: red;");
        grid.add(statusLabel, 0, 9, 2, 1);
        GridPane.setHalignment(statusLabel, HPos.CENTER);

        // Event handlers
        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String username = userTextField.getText().trim();
            String password = pwBox.getText();
            String phone = phoneField.getText().trim();
            String gender = genderCombo.getValue();
            String userType = userTypeCombo.getValue();
            
            // Input validation
            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                statusLabel.setText("All fields are required!");
                return;
            }
            
            if (username.length() < 4) {
                statusLabel.setText("Username must be at least 4 characters!");
                return;
            }
            
            if (password.length() < 6) {
                statusLabel.setText("Password must be at least 6 characters!");
                return;
            }
            
            // Register user with additional details
            User newUser = userManager.registerUser(username, password, userType, name, phone, gender);
            if (newUser != null) {
                statusLabel.setText("Registration successful!");
                // Clear fields
                nameField.clear();
                userTextField.clear();
                pwBox.clear();
                phoneField.clear();
                // Auto switch to login after 2 seconds
                new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            javafx.application.Platform.runLater(() -> {
                                root.getChildren().remove(registrationForm);
                                root.getChildren().add(loginForm);
                                statusLabel.setText("");
                            });
                        }
                    },
                    2000
                );
            } else {
                statusLabel.setText("Registration failed. Username may be taken.");
            }
        });

        backBtn.setOnAction(e -> {
            root.getChildren().remove(registrationForm);
            root.getChildren().add(loginForm);
        });
        
        return grid;
    }
    
    private void showDashboard(User user) {
        // Clear the current content
        root.getChildren().clear();
        
        // Create the appropriate dashboard
        BaseDashboard dashboard;
        if (user.getRole().equals("Admin")) {
            dashboard = new AdminDashboard(primaryStage, user, userManager, eventManager);
        } else {
            dashboard = new StudentDashboard(primaryStage, user, userManager, eventManager);
        }
        
        // Add the dashboard to our root pane
        root.getChildren().add(dashboard.getRoot());
        
        // Update the window title
        primaryStage.setTitle("IIUM Event Participation - " + user.getRole() + " Dashboard");
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.centerOnScreen();
    }
    
    private void showRegistrationScreen() {
        root.getChildren().clear();
        if (registrationForm == null) {
            registrationForm = createRegistrationForm();
        }
        if (root.getChildren().contains(loginForm)) {
            root.getChildren().remove(loginForm);
        }
        if (!root.getChildren().contains(registrationForm)) {
            root.getChildren().add(registrationForm);
        }
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;
            this.primaryStage.setTitle("IIUM Event Participation System");

            // Initialize managers
            this.userManager = UserManager.getInstance();
            this.eventManager = EventManager.getInstance();

            // Show login screen
            showLoginScreen();

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLoginScreen() {
        root.getChildren().clear();
        if (loginForm == null) {
            GridPane grid = new GridPane();
            grid.setAlignment(Pos.CENTER);
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(25, 25, 25, 25));

            // Username field
            Label userName = new Label("Username:");
            grid.add(userName, 0, 1);

            TextField userTextField = new TextField();
            grid.add(userTextField, 1, 1);
            
            // Password field
            Label pw = new Label("Password:");
            grid.add(pw, 0, 2);
            
            PasswordField pwBox = new PasswordField();
            grid.add(pwBox, 1, 2);
            
            // Login button
            Button loginBtn = new Button("Login");
            Button registerBtn = new Button("Register");
            HBox hbBtn = new HBox(10);
            hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
            hbBtn.getChildren().addAll(loginBtn, registerBtn);
            grid.add(hbBtn, 1, 4);
            
            // Status message
            final Text actiontarget = new Text();
            grid.add(actiontarget, 1, 6);
            
            // Set action for login button
            loginBtn.setOnAction(e -> {
                String username = userTextField.getText();
                String password = pwBox.getText();
                
                if (username.isEmpty() || password.isEmpty()) {
                    actiontarget.setText("Please enter both username and password");
                    return;
                }
                
                User user = userManager.loginUser(username, password);
                if (user != null) {
                    showDashboard(user);
                } else {
                    actiontarget.setText("Invalid username or password");
                }
            });
            
            // Set action for register button
            registerBtn.setOnAction(e -> showRegistrationScreen());
            
            loginForm = grid;
        }
        
        // Add the login form to the root
        root.getChildren().add(loginForm);
    }
}
