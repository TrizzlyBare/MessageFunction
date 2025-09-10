package com.chatmessage.chat.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    private String messageId;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String roomId;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public Message() {
        this.timestamp = LocalDateTime.now();
    }

    public Message(String messageId, String senderId, String roomId, String content, String imageUrl) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.roomId = roomId;
        this.content = content;
        this.imageUrl = imageUrl;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and setters
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
