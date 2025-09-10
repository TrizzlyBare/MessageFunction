package com.chatmessage.chat.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.chatmessage.chat.model.Message;

@Repository
public class MessageRepository {

    // In-memory store for messages, replace with database implementation in production
    private final Map<String, Message> messages = new ConcurrentHashMap<>();

    public Message save(Message message) {
        messages.put(message.getMessageId(), message);
        return message;
    }

    public Message findById(String messageId) {
        return messages.get(messageId);
    }

    public List<Message> findByRoomId(String roomId) {
        return messages.values().stream()
                .filter(message -> message.getRoomId().equals(roomId))
                .collect(Collectors.toList());
    }

    public List<Message> findAll() {
        return new ArrayList<>(messages.values());
    }
}
