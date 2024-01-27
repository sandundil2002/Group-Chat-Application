package lk.ijse.chatapplication.controller;

import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lk.ijse.chatapplication.util.TimeUtil;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ServerFormController {

    @FXML
    private Label lblTime;

    @FXML
    private Label lblDate;

    @FXML
    private JFXTextArea txtArea;

    @FXML
    private Label lblOnlineCount;

    private ServerSocket serverSocket;

    private DataOutputStream dataOutputStream;

    private static final List<Socket> socketList = new ArrayList<>();

    private static final List<DataOutputStream> clients = new ArrayList<>();

    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh : mm a");

    public void initialize() {
        lblOnlineCount.setText("0");
        TimeUtil.updateRealTimeServer(lblTime);
        lblDate.setText(LocalDate.now().toString());
        try {
            serverSocket = new ServerSocket(3002);
            txtArea.setText(LocalTime.now().format(timeFormatter) + " - Server started waiting for client connection...");

            new Thread(() -> {
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        txtArea.appendText("\n" + LocalTime.now().format(timeFormatter) + " - "+HomeFormController.name + " connected... " + clientSocket);
                        dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
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
            DataInputStream datainputStream = new DataInputStream(clientSocket.getInputStream());

            while (true) {
                String message = datainputStream.readUTF();

                if (!message.contains(":") && !message.contains("-")){
                    socketList.remove(clientSocket);
                    setOnlineClients();
                    txtArea.appendText("\n" + LocalTime.now().format(timeFormatter) + " - " + message + " disconnected... " + clientSocket);
                }

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
    private void btnStopOnAction() {
        System.exit(0);
    }
}
