module com.example.messengerclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.messengerclient to javafx.fxml;
    exports com.example.messengerclient;
}