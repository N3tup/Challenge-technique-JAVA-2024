package org.esiea.bouchez_drach.controllers;

import org.esiea.bouchez_drach.models.Message;
import org.esiea.bouchez_drach.services.MessageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public String postMessage(@RequestParam String username, @RequestParam String content) {
        System.out.println("POST /messages called");
        System.out.println("Username: " + username);
        System.out.println("Content: " + content);

        Message message = new Message();
        message.setUsername(username);
        message.setContent(content);
        messageService.saveMessage(message);

        return "redirect:/messages";
    }
}