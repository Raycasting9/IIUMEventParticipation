module iium.event.participation {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens iium.event.participation to javafx.fxml;
    exports iium.event.participation;
}
