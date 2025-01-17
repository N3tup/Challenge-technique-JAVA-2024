package com.chatapp.challenge_technique_drash_bouchez.repository;

import com.chatapp.challenge_technique_drash_bouchez.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
}
