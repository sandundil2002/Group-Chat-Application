package lk.ijse.chatapplication.controller;

import lk.ijse.chatapplication.util.ChatServer;

public class ClientFormController {

    public void initialize(){
        ChatServer chatServer = new ChatServer();
        chatServer.initialize();
    }
}
