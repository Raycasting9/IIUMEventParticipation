# IIUM Event Participation System

A JavaFX application for managing event participation at IIUM. This is a course project for the Object-Oriented Programming subject at IIUM. It started as a terminal-based program and was later enhanced with a JavaFX GUI to improve user experience.

## 🚀 Features

- User authentication (Login/Register)
- Different user roles (Admin/Student)
- Event management (Create, Read, Update, Delete)
- Event registration
- User management (for admins)
- File-based data persistence

## 📋 Prerequisites

- Java 17 or later
- Maven 3.6.3 or later
- JavaFX SDK (included via Maven)

## 🛠️ Building the Project

1. Clone the repository
2. Open a terminal in the project root directory
3. Run the following command to build the project:
   ```bash
   mvn clean install
   ```

## 🚀 Running the Application

### Using Apache NetBeans:
1. **Install Prerequisites**
   - Download and install [Apache NetBeans](https://netbeans.apache.org/download/index.html) (make sure to select the Java SE bundle)
   - Ensure you have JDK 17 or later installed
   - Download [JavaFX SDK](https://gluonhq.com/products/javafx/) and extract it to a known location (e.g., `C:\\javafx-sdk-24.0.1`)

2. **Import the Project**
   - Launch NetBeans
   - Select `File` > `Open Project`
   - Navigate to the project folder and select it
   - Click `Open Project`

3. **Configure JavaFX**
   - Right-click on the project in the Projects window
   - Select `Properties`
   - In the `Libraries` category, click `Manage Platforms...`
   - Add the JavaFX SDK path if not already present
   - Go to `Run` category
   - In the `VM Options` field, add:
     ```
     --module-path "C:\\javafx-sdk-24.0.1\\lib" --add-modules=javafx.controls,javafx.fxml
     ```
   - Click `OK` to save changes

4. **Running the Application**
   - Right-click on the project
   - Select `Run` or press F6
   - The application should start with the login screen

### Using VS Code:
1. **Install Prerequisites**
   - Install [VS Code](https://code.visualstudio.com/)
   - Install the [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
   - Install [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
   - Download [JavaFX SDK](https://gluonhq.com/products/javafx/) and extract it to a known location (e.g., `C:\\javafx-sdk-24.0.1`)

2. **Configure JavaFX in VS Code**
   - Open Command Palette (Ctrl+Shift+P)
   - Type "Java: Configure Java Runtime"
   - Add the JavaFX SDK path to `java.configuration.runtimes` in your VS Code settings (`.vscode/settings.json`):
     ```json
     "java.configuration.runtimes": [
       {
         "name": "JavaSE-17",
         "path": "path/to/your/jdk-17",
         "default": true
       }
     ]
     ```
   - Add these VM arguments to your launch configuration (`.vscode/launch.json`):
     ```json
     "vmArgs": "--module-path \"C:\\\\javafx-sdk-24.0.1\\\\lib\" --add-modules=javafx.controls,javafx.fxml"
     ```

3. **Running the Application**
   - Open the project folder in VS Code
   - Open `Main.java`
   - Click the "Run" button in the top-right corner or press F5

### Using Maven (Command Line):
```bash
mvn javafx:run
```

### Using Java directly (Command Line):
```bash
java --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml -cp "target/classes" iium.event.participation.Main
```

## 🔑 Default Admin Account

- **Username:** admin
- **Password:** admin123

## 🗂️ Project Structure

```
src/main/java/iium/event/participation/
├── Main.java                 # Application entry point
├── EventAppFX.java           # Main JavaFX application class
├── UserManager.java          # Manages user accounts
├── EventManager.java         # Manages events and registrations
├── User.java                 # Base user class
├── Admin.java                # Admin user class
├── Student.java              # Student user class
└── Event.java                # Event class
```

## 🖥️ Usage

### Login Screen
A graphical login screen where users can:
- Select their role (Admin/Student)
- Enter credentials
- Navigate to their respective dashboards

### Admin Dashboard
- Add new events
- Edit existing events
- Delete events
- View all participation records
- Manage user accounts

### Student Dashboard
- View available events
- Register for events
- View registered events
- Cancel event registration

## 💾 Data Storage

- User data is stored in `users.txt`
- Event data is stored in `events.txt`
- Registration data is stored in `registrations.txt`

## 🤝 Contributing

Pull requests are welcome! If you want to contribute:
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 🙌 Credits

Developed by:
1. Muhammad Muaz bin Syahmi
1. Muhammad Iqmal bin Mohd Suradi
1. Muhammad Lutfi Nadzmi bin Husaini
1. Abdulazeez Umar
