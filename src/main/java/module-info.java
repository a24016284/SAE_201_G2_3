module bomberman {
    requires javafx.controls;
    requires javafx.fxml;


    opens bomberman to javafx.fxml;
    exports bomberman;
}