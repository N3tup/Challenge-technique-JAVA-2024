package com.chatapp.discoghd.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final ActiveUserController activeUserController;

    public WebSocketController(ActiveUserController activeUserController) {
        this.activeUserController = activeUserController;
    }

    @MessageMapping("/user.connect")
    public void handleUserConnect(@Payload UserConnectionMessage message) {
        activeUserController.addUser(message.getUsername());
    }

    @MessageMapping("/user.disconnect")
    public void handleUserDisconnect(@Payload UserConnectionMessage message) {
        activeUserController.removeUser(message.getUsername());
    }
}

// Classe pour représenter le message de connexion/déconnexion
class UserConnectionMessage {
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}