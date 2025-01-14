package com.chatapp.discoghd.controller;

import com.chatapp.discoghd.model.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

@Controller
public class ChatController {

    private final Set<String> activeUsernames = new HashSet<>();

    // Mapping pour afficher la page de chat avec vérification des pseudonymes
    @GetMapping("/")
    public String indexPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "page_connection";
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

    // Suppression du pseudonyme lorsqu'un utilisateur quitte
    @GetMapping("/logout")
    @ResponseBody
    public String logout(@RequestParam String username) {
        activeUsernames.remove(username);
        return "Utilisateur déconnecté : " + username;
    }
}