package com.chatmessage.chat.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.chatmessage.chat.model.Message;
import com.chatmessage.chat.service.MessageService;

@RestController
@RequestMapping("/api")
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Endpoint to send a new message with optional image attachment
     *
     * @param userId The ID of the user sending the message
     * @param roomId The ID of the room where the message will be sent
     * @param content The text content of the message (optional if image is
     * provided)
     * @param image Optional image file to attach to the message (required if
     * content is empty)
     * @return The created message with status 201
     */
    @PostMapping(value = "/messages", consumes = {"multipart/form-data"})
    @io.swagger.v3.oas.annotations.Operation(
            summary = "Send a new message with text, image, or both",
            description = "Sends a message to a chat room. Either text content, image, or both must be provided."
    )
    public ResponseEntity<?> sendMessage(
            @io.swagger.v3.oas.annotations.Parameter(description = "User ID sending the message")
            @RequestParam(value = "userId", defaultValue = "default-user") String userId,
            @io.swagger.v3.oas.annotations.Parameter(description = "Room ID where the message will be sent", required = true)
            @RequestParam("roomId") String roomId,
            @io.swagger.v3.oas.annotations.Parameter(description = "Text content of the message (optional if image is provided)")
            @RequestParam(value = "content", required = false) String content,
            @io.swagger.v3.oas.annotations.Parameter(
                    description = "Image file to upload (required if content is empty)",
                    schema = @io.swagger.v3.oas.annotations.media.Schema(type = "string", format = "binary")
            )
            @RequestPart(value = "image", required = false) MultipartFile image) {

        try {
            logger.info("Received message request - userId: {}, roomId: {}, content: {}, image: {}",
                    userId, roomId, content, (image != null ? image.getOriginalFilename() : "null"));

            if ((content == null || content.trim().isEmpty()) && (image == null || image.isEmpty())) {
                logger.warn("Both content and image are empty");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error",
                        "Either message content or image must be provided"));
            }

            // Use empty string if content is null
            String messageContent = (content != null) ? content : "";

            logger.info("Sending message from user {} to room {}", userId, roomId);

            Message message = messageService.sendMessage(userId, roomId, messageContent, image);

            // Broadcast the new message to all subscribers of this room via WebSocket
            messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
            logger.info("Message broadcasted to WebSocket subscribers for room {}", roomId);

            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            logger.error("Bad request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            logger.error("Failed to process image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to process image"));
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
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
