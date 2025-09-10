package com.chatmessage.chat.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.chatmessage.chat.model.User;
import com.chatmessage.chat.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }

        // Check if username is already taken
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(username);

        return userRepository.save(user);
    }

    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            user.setUserId(UUID.randomUUID().toString());
        }
        return userRepository.save(user);
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
