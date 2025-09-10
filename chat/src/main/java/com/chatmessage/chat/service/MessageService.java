package com.chatmessage.chat.service;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.chatmessage.chat.model.Message;
import com.chatmessage.chat.model.Room;
import com.chatmessage.chat.repository.MessageRepository;
import com.chatmessage.chat.repository.RoomRepository;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final StorageService storageService;

    public MessageService(MessageRepository messageRepository, RoomRepository roomRepository, StorageService storageService) {
        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.storageService = storageService;
    }

    /**
     * Send a message with optional text content and/or image
     *
     * @param senderId The ID of the user sending the message
     * @param roomId The ID of the room where the message is sent
     * @param content The text content (can be empty if image is provided)
     * @param image The image file (can be null if content is provided)
     * @return The saved Message object
     * @throws IOException If there is an error processing the image
     * @throws IllegalArgumentException If the room doesn't exist or user is not
     * a member
     */
    public Message sendMessage(String senderId, String roomId, String content, MultipartFile image) throws IOException {
        // Validate if user is a member of the room
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (!room.isMember(senderId)) {
            throw new IllegalArgumentException("User is not a member of this room");
        }

        // Validate that either content or image is provided
        if ((content == null || content.trim().isEmpty()) && (image == null || image.isEmpty())) {
            throw new IllegalArgumentException("Either message content or image must be provided");
        }

        // Upload image if provided
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = storageService.uploadImage(image);
        }

        // Create and save message
        Message message = new Message();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSenderId(senderId);
        message.setRoomId(roomId);
        message.setContent(content != null ? content : "");
        message.setImageUrl(imageUrl);

        return messageRepository.save(message);
    }

    public List<Message> getMessagesByRoomId(String userId, String roomId) {
        // Validate if user is a member of the room
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        if (!room.isMember(userId)) {
            throw new IllegalArgumentException("User is not a member of this room");
        }

        return messageRepository.findByRoomIdOrderByTimestamp(roomId);
    }

    public Message getMessageById(String messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }
}
