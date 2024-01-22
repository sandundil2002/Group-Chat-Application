package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientFormController {

    @FXML
    private TextField txtMsg;

    @FXML
    private Label lblName;

    @FXML
    private JFXTextArea txtArea;

    Socket socket;

    DataOutputStream dataOutputStream;

    DataInputStream dataInputStream;

    String message;

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
        System.exit(0);
    }

    @FXML
    private void btnEmojiOnAction(ActionEvent actionEvent) {
    }

    @FXML
    private void btnImageOnAction(ActionEvent actionEvent) {
    }
}
