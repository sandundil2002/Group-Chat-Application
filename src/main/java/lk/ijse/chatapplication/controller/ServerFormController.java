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

    private ServerSocket serverSocket;

    private static final List<Socket> socketList = new ArrayList<>();

    private static final List<DataOutputStream> clients = new ArrayList<>();

    public void initialize() {
        try {
            serverSocket = new ServerSocket(3002);
            txtArea.setText("Server started waiting for client connection...");

            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        txtArea.appendText("\nClient connected ");
                        DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
                        clients.add(dataOutputStream);
                        socketList.add(clientSocket);
                        setOnlineClients();
                        new Thread(() -> handleClient(clientSocket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());

            while (true) {
                String message = inputStream.readUTF();

                // Broadcast the message to all connected clients
                for (DataOutputStream client : clients) {
                    client.writeUTF(message);
                    client.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setOnlineClients() {
        Platform.runLater(() -> lblOnlineCount.setText(String.valueOf(socketList.size())));
    }

    @FXML
    private void btnStopOnAction(ActionEvent actionEvent) {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
