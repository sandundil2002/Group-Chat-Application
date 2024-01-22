package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class ClientFormController {

    @FXML
    private TextField txtMsg;

    @FXML
    private Label lblName;

    @FXML
    private JFXTextArea txtArea;

    @FXML
    private JFXButton btnId;

    Socket socket;

    DataOutputStream dataOutputStream;

    DataInputStream dataInputStream;

    static String clientName;

    public void initialize() {
        lblName.setText(HomeFormController.name);
        clientName = lblName.getText();

        new Thread(() -> {
            try {
                socket = new Socket("localhost", 3002);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    String message = dataInputStream.readUTF();
                    txtArea.appendText("\n" + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void btnSendOnAction(ActionEvent actionEvent) {
        try {
            String message = clientName + ": " + txtMsg.getText().trim();
            dataOutputStream.writeUTF(message);
            dataOutputStream.flush();
            txtMsg.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
        try {
            dataOutputStream.flush();
            Stage stage = (Stage) btnId.getScene().getWindow();
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
}
