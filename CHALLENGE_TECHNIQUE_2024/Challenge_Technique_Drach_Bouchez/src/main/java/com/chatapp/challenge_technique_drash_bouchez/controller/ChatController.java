package com.chatapp.challenge_technique_drash_bouchez.controller;

import com.chatapp.challenge_technique_drash_bouchez.model.Message;
import com.chatapp.challenge_technique_drash_bouchez.model.PrivateMessage;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.HashMap;

@Controller
public class ChatController {
    private final ActiveUserController activeUserController;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ActiveUserController activeUserController, SimpMessagingTemplate messagingTemplate) {
        this.activeUserController = activeUserController;
        this.messagingTemplate = messagingTemplate;
    }

    // Landing page with login
    @GetMapping("/")
    public String indexPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "page_connection";
    }

    // Signup page
    @GetMapping("/signup")
    public String signup(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "signup";
    }

    // Process signup
    @PostMapping("/signupPHP")
    public String processSignup(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        if (username.isBlank() || email.isBlank() || password.isBlank()) {
            model.addAttribute("error", "All fields are required.");
            return "signupPHP";
        }

        // Add your user registration logic here

        model.addAttribute("success", "Account created successfully.");
        return "page_confirmation";
    }

    // Confirmation page
    @GetMapping("/confirmation")
    public String confirmationPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "page_confirmation";
    }

    // Register page
    @GetMapping("/register")
    public String registerPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "register";
    }

    // Main chat page
    @GetMapping("/chat")
    public String chatPage(@RequestParam String username, Model model) {
        if (username.isBlank() || activeUserController.isUserActive(username)) {
            String errorMessage = "Ce pseudonyme est déjà utilisé. Choisissez-en un autre.";
            String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
            return "redirect:/?error=" + encodedMessage;
        }

        activeUserController.addUser(username);
        model.addAttribute("username", username);
        activeUserController.notifyUserJoined(username);

        return "page_chat";
    }

    // WebSocket message handling
    @MessageMapping("/sendMessage")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        // Prevent XSS attacks by escaping HTML characters
        message.setContent(message.getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));
        message.setTimestamp(System.currentTimeMillis());
        return message;
    }

    // WebSocket connection event
    @EventListener
    public void handleWebSocketConnect(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionAttributes() == null) {
            headerAccessor.setSessionAttributes(new HashMap<>());
        }

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            activeUserController.addUser(username);
        }
    }

    // WebSocket disconnection event
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            activeUserController.removeUser(username);
        }
    }

    // Reconnection page
    @GetMapping("/reconnectPage")
    public String reconnectPage() {
        return "reconnectPage";
    }

    // Logout handling
    @GetMapping("/logout")
    public String logout(@RequestParam String username) {
        if (username != null && activeUserController.isUserActive(username)) {
            activeUserController.removeUser(username);
            activeUserController.notifyUserLeft(username);
        }
        return "redirect:/?message=Vous êtes déconnecté.";
    }

    @MessageMapping("/private-message")
    public void handlePrivateMessage(PrivateMessage message) {
        message.setTimestamp(System.currentTimeMillis());
        // Prevent XSS attacks by escaping HTML characters
        message.setContent(message.getContent().replaceAll("<", "&lt;").replaceAll(">", "&gt;"));

        // Send to recipient
        messagingTemplate.convertAndSendToUser(
                message.getRecipient(),             //qui recoit le message
                "/queue/private-messages",          // la destination du message
                message
        );
        // Send to sender
        messagingTemplate.convertAndSendToUser(
                message.getSender(),                //renvoie le message à l'envoyeur
                "/queue/private-messages",
                message
        );
    }
}