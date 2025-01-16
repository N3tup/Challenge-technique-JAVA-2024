package com.chatapp.discoghd.controller;

import com.chatapp.discoghd.model.Message;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;


import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ChatController {

    private final Set<String> activeUsernames = new HashSet<>();
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Mapping pour afficher la page de chat avec vérification des pseudonymes
    @GetMapping("/")
    public String indexPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "page_connection";
    }

    // acces inscription
    @GetMapping("/signup")
    public String signup(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "signup";
    }

    @PostMapping("/signupPHP")
    public String processSignup(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        // Add logic for saving user data or checking if username/email is already taken

        // Example error handling
        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            model.addAttribute("error", "All fields are required.");
            return "signupPHP"; // Redirect to signup page with error message
        }

        // Logic to register the user, like saving to a database or a dummy list
        // For now, we're just sending a success message (you can replace this with real registration logic)
        model.addAttribute("success", "Account created successfully.");
        return "page_confirmation"; // Redirect to the confirmation page
    }

    // confirmation inscription
    @GetMapping("/confirmation")
    public String confirmationPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "page_confirmation";
    }

    //register.php
    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "register";
    }

    @GetMapping("/chat")
    public String chatPage(@RequestParam String username, Model model) {
        if (username.isBlank() || activeUsernames.contains(username)) {
            String errorMessage = "Ce pseudonyme est déjà utilisé. Choisissez-en un autre.";
            String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            return "redirect:/?error=" + encodedMessage;
        }

        activeUsernames.add(username);
        model.addAttribute("username", username);

        // Notify all users about the new user
        messagingTemplate.convertAndSend("/topic/notifications", username + " a rejoint le chat.");

        // Send the active user list to everyone
        messagingTemplate.convertAndSend("/topic/activeUsers", activeUsernames);

        // Send the active user list specifically to the new user
        messagingTemplate.convertAndSendToUser(username, "/queue/activeUsers", activeUsernames);

        return "page_chat";
    }

    // Gestion de l'envoi des messages via WebSocket avec protection contre XSS
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        message.setContent(message.getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }

    //
    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Ensure session attributes are initialized
        if (headerAccessor.getSessionAttributes() == null) {
            headerAccessor.setSessionAttributes(new HashMap<>());
        }

        // Assign username to session if not already set
        String username = (String) headerAccessor.getSessionAttributes().get("username");

        // Add to active users and broadcast update
        synchronized (activeUsernames) {
            activeUsernames.add(username);
        }
        updateActiveUserList();
    }

    // Handle WebSocket disconnection
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Retrieve username from session attributes
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            synchronized (activeUsernames) {
                activeUsernames.remove(username);
            }
            updateActiveUserList();
        }
    }

    // Send the updated list of active users to all clients
    private void updateActiveUserList() {
        messagingTemplate.convertAndSend("/topic/activeUsers", activeUsernames);
    }

    // Redirection vers la page de reconnexion
    @GetMapping("/reconnectPage")
    public String reconnectPage() {
        return "reconnectPage";
    }

    //
    @SendTo("/topic/activeUsers")
    public Set<String> getActiveUsers() {
        return activeUsernames;
    }

    @GetMapping("/logout")
    public String logout(@RequestParam String username) {
        if (username != null && activeUsernames.contains(username)) {
            activeUsernames.remove(username);
            messagingTemplate.convertAndSend("/topic/notifications", username + " a quitté le chat.");
            messagingTemplate.convertAndSend("/topic/activeUsers", activeUsernames);
        }
        return "redirect:/?message=Vous êtes déconnecté.";
    }
}