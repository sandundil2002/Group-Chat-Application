package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class ClientFormController {

    @FXML
    private VBox txtVbox;

    @FXML
    private TextField txtMsg;

    @FXML
    private Label lblName;

    @FXML
    private JFXButton btnBack;

    Socket socket;

    DataOutputStream dataOutputStream;

    DataInputStream dataInputStream;

    static String clientName;

    static String message;

    public void initialize() {
        lblName.setText(HomeFormController.name);
        clientName = lblName.getText();

        new Thread(() -> {
            try {
                socket = new Socket("localhost", 3002);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    message = dataInputStream.readUTF();

                    Platform.runLater(() -> {
                        Text textNode = new Text(message);
                        txtVbox.getChildren().add(textNode);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void btnSendOnAction(ActionEvent actionEvent) {
        if (validateMsg()) {
            try {
                String message = lblName.getText() + " : " + txtMsg.getText();
                dataOutputStream.writeUTF(message);
                dataOutputStream.flush();
                txtMsg.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
        try {
            dataOutputStream.flush();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnEmojiOnAction(ActionEvent actionEvent) {
    }

    @FXML
    private void btnImageOnAction(ActionEvent actionEvent) {
    }

    private boolean validateMsg(){
        if (txtMsg.getText().trim().isEmpty()) {
            txtMsg.requestFocus();
            txtMsg.setStyle("-fx-border-color:#ff0000;");
            return false;
        } else {
            txtMsg.setStyle("-fx-border-color:black;");
            return true;
        }
    }
}
