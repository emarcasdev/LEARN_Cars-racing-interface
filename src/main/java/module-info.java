module org.example.carsracinginterface {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.carsracinginterface to javafx.fxml;
    exports org.example.carsracinginterface;
}