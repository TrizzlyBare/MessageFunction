package com.chatmessage.chat.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chatmessage.chat.model.Message;
import com.chatmessage.chat.service.MessageService;

@RestController
@RequestMapping("/api")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Endpoint to send a new message with optional image attachment
     * 
     * @param userId The ID of the user sending the message
     * @param roomId The ID of the room where the message will be sent
     * @param content The text content of the message
     * @param image Optional image file to attach to the message
     * @return The created message with status 201
     */
    @PostMapping(value = "/messages", consumes = { "multipart/form-data" })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "multipart/form-data"))
    @io.swagger.v3.oas.annotations.Operation(summary = "Send a new message with optional image attachment")
    public ResponseEntity<?> sendMessage(
            @RequestParam(value = "userId", defaultValue = "default-user") String userId,
            @RequestParam("roomId") String roomId,
            @RequestParam("content") String content,
            @io.swagger.v3.oas.annotations.Parameter(description = "Image file to upload", content = @io.swagger.v3.oas.annotations.media.Content(mediaType = "multipart/form-data"))
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            logger.info("Sending message from user {} to room {}", userId, roomId);

            Message message = messageService.sendMessage(userId, roomId, content, image);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to process image"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to retrieve all messages for a specific room
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getRoomMessages(
            @RequestParam(value = "userId", defaultValue = "default-user") String userId,
            @PathVariable("roomId") String roomId) {

        try {
            logger.info("Getting messages for room {} for user {}", roomId, userId);

            List<Message> messages = messageService.getMessagesByRoomId(userId, roomId);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
