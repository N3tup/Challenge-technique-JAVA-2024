package org.esiea.bouchez_drach.services;

import org.esiea.bouchez_drach.models.Message;
import org.esiea.bouchez_drach.repositories.MessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message saveMessage(Message message) {
        System.out.println("Saving Message: " + message.getUsername() + " - " + message.getContent());
        return messageRepository.save(message);
    }

}
