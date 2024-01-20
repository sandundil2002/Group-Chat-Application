package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.*;
import java.net.*;

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

    public void initialize(){
        new Thread(() -> {
            try {
                socket = new Socket("localhost",3002);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                message = dataInputStream.readUTF();
                txtArea.appendText("\nServer: "+message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @FXML
    private void btnBackOnAction(ActionEvent actionEvent) {
    }

    @FXML
    private void btnSendOnAction(ActionEvent actionEvent) {
        try {
            dataOutputStream.writeUTF(txtMsg.getText().trim());
            txtArea.appendText("\nMe: "+txtMsg.getText());
            dataOutputStream.flush();
            txtMsg.setText("");
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
