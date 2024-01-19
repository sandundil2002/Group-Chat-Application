package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFormController {
    public JFXTextArea txtArea;
    public Label lblOnlineCount;
    ServerSocket serverSocket;
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String message = "";

    public void initialize(){
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3002);
                txtArea.setText("Server started");
                socket = serverSocket.accept();
                txtArea.setText("Client connected");
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (!message.equals("end")){
                    message = dataInputStream.readUTF();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void btnStopOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }
}
