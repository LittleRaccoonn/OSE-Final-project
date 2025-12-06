module com.example.demoproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;


    opens com.example.demoproject to javafx.fxml;
    exports com.example.demoproject;
    exports com.example.demoproject.model;
    opens com.example.demoproject.model to com.fasterxml.jackson.databind, javafx.base, javafx.fxml;
}