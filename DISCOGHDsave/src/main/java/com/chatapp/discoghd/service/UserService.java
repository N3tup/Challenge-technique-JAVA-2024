package com.chatapp.discoghd.service;

import com.chatapp.discoghd.model.User;
import com.chatapp.discoghd.repository.UserRepository;
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
