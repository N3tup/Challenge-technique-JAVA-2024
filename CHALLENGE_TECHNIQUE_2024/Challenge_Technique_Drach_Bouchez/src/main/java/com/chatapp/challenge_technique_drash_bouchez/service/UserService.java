package com.chatapp.challenge_technique_drash_bouchez.service;

import com.chatapp.challenge_technique_drash_bouchez.model.User;
import com.chatapp.challenge_technique_drash_bouchez.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean isUsernameTaken(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
