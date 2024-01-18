package lk.ijse.chatapplication.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    ServerSocket serverSocket;
    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String message = "";
    public void initialize(){
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(3002);
                System.out.println("Server started");
                socket = serverSocket.accept();
                System.out.println("Client connected");
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
}
