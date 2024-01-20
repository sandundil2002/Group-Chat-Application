package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {

    @FXML
    private JFXTextArea txtArea;

    @FXML
    private Label lblOnlineCount;

    ServerSocket serverSocket;

    Socket socket;

    DataOutputStream dataOutputStream;

    DataInputStream dataInputStream;

    String message;

    public void initialize(){
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3002);
                txtArea.setText("Server started waiting for client connection...");
                socket = serverSocket.accept();
                txtArea.appendText("\nClient connected");
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                message = dataInputStream.readUTF();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void btnStopOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }
}
