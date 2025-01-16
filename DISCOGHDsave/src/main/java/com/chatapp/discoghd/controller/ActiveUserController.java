package com.chatapp.discoghd.controller;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class ActiveUserController {
    private final Set<String> activeUsers = ConcurrentHashMap.newKeySet();
    private final SimpMessagingTemplate messagingTemplate;

    public ActiveUserController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/api/active-users")
    @ResponseBody
    public Set<String> getActiveUsers() {
        return new HashSet<>(activeUsers);
    }

    @SubscribeMapping("/user/queue/activeUsers")
    public Set<String> sendInitialUserList() {
        return new HashSet<>(activeUsers);
    }

    public void addUser(String username) {
        if (username != null && !username.isBlank()) {
            activeUsers.add(username);
            broadcastUserList();
            notifyUserJoined(username);
        }
    }

    public void removeUser(String username) {
        if (username != null) {
            activeUsers.remove(username);
            broadcastUserList();
            notifyUserLeft(username);
        }
    }

    public boolean isUserActive(String username) {
        return activeUsers.contains(username);
    }

    private void broadcastUserList() {
        Set<String> userList = new HashSet<>(activeUsers);
        messagingTemplate.convertAndSend("/topic/activeUsers", userList);
    }

    public void notifyUserJoined(String username) {
        messagingTemplate.convertAndSend("/topic/notifications", username + " a rejoint le chat.");
    }

    public void notifyUserLeft(String username) {
        messagingTemplate.convertAndSend("/topic/notifications", username + " a quitté le chat.");
    }
}