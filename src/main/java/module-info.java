module com.example.sistem_click {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sistem_click to javafx.fxml;
    exports com.example.sistem_click;
}