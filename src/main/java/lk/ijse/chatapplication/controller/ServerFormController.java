package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerFormController {

    @FXML
    private JFXTextArea txtArea;

    @FXML
    private Label lblOnlineCount;

    ServerSocket serverSocket;

    Socket clientSocket;

    DataOutputStream dataOutputStream;

    DataInputStream dataInputStream;

    String message;

    private static final List<Socket> socketList = new ArrayList<>();

    public void initialize(){
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3002);
                txtArea.setText("Server started waiting for client connection...");
                while (true) {
                    clientSocket = serverSocket.accept();
                    txtArea.appendText("\nClient connected " + clientSocket);
                    Thread clientThread = new Thread(() -> {
                        try {
                            dataInputStream = new DataInputStream(clientSocket.getInputStream());
                            socketList.add(clientSocket);
                            setOnlineClients();
                            message = dataInputStream.readUTF();
                        } catch (IOException e) {
                            txtArea.appendText("\n"+"Client disconnected ! ");
                        }
                    });
                    clientThread.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void setOnlineClients(){
        Platform.runLater(() -> lblOnlineCount.setText(String.valueOf(socketList.size())));
    }

    public void btnStopOnAction(ActionEvent actionEvent) {
        System.exit(0);
    }
}
