module lk.ijse.chatapplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.jfoenix;
    requires com.gluonhq.emoji;
    requires java.desktop;
    requires javafx.swing;

    opens lk.ijse.chatapplication to javafx.fxml;
    exports lk.ijse.chatapplication;
    exports lk.ijse.chatapplication.controller;
    opens lk.ijse.chatapplication.controller to javafx.fxml;
}