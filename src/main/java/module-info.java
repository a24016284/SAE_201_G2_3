module bomberman {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens bomberman to javafx.fxml;
    exports bomberman;
}