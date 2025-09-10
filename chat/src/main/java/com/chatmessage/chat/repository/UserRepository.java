package com.chatmessage.chat.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.chatmessage.chat.model.User;

@Repository
public class UserRepository {

    // In-memory store for users, replace with database implementation in production
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public User save(User user) {
        users.put(user.getUserId(), user);
        return user;
    }

    public User findById(String userId) {
        return users.get(userId);
    }

    public User findByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
