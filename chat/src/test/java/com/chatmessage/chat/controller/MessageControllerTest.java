package com.chatmessage.chat.controller;

import com.chatmessage.chat.model.Message;
import com.chatmessage.chat.service.MessageService;
import com.chatmessage.chat.repository.RoomRepository;
import com.chatmessage.chat.repository.UserRepository;
import com.chatmessage.chat.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManagerFactory;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@ActiveProfiles("test")
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MessageRepository messageRepository;

    // Prevent JPA autoconfiguration from requiring a real EntityManagerFactory
    @MockBean(name = "entityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    private Message testMessage1;
    private Message testMessage2;
    private List<Message> testMessages;

    @BeforeEach
    void setUp() {
        testMessage1 = new Message();
        testMessage1.setMessageId("msg-1");
        testMessage1.setSenderId("user-1");
        testMessage1.setRoomId("room-1");
        testMessage1.setContent("Test message 1");
        testMessage1.setTimestamp(LocalDateTime.now().minusMinutes(5));

        testMessage2 = new Message();
        testMessage2.setMessageId("msg-2");
        testMessage2.setSenderId("user-2");
        testMessage2.setRoomId("room-1");
        testMessage2.setContent("Test message 2");
        testMessage2.setImageUrl("http://example.com/image.jpg");
        testMessage2.setTimestamp(LocalDateTime.now());

        testMessages = Arrays.asList(testMessage1, testMessage2);
    }

    @Test
    void sendMessage_ShouldCreateMessage_WithTextOnly() throws Exception {
        // Given
        when(messageService.sendMessage(eq("user-1"), eq("room-1"), eq("Hello World"), isNull()))
                .thenReturn(testMessage1);

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .param("userId", "user-1")
                .param("roomId", "room-1")
                .param("content", "Hello World"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messageId").value("msg-1"))
                .andExpect(jsonPath("$.senderId").value("user-1"))
                .andExpect(jsonPath("$.roomId").value("room-1"))
                .andExpect(jsonPath("$.content").value("Test message 1"));
    }

    @Test
    void sendMessage_ShouldCreateMessage_WithImageOnly() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        Message imageMessage = new Message();
        imageMessage.setMessageId("img-msg");
        imageMessage.setSenderId("user-1");
        imageMessage.setRoomId("room-1");
        imageMessage.setContent("");
        imageMessage.setImageUrl("http://example.com/uploaded-image.jpg");

        when(messageService.sendMessage(eq("user-1"), eq("room-1"), eq(""), any()))
                .thenReturn(imageMessage);

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .file(imageFile)
                .param("userId", "user-1")
                .param("roomId", "room-1"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messageId").value("img-msg"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/uploaded-image.jpg"))
                .andExpect(jsonPath("$.content").value(""));
    }

    @Test
    void sendMessage_ShouldCreateMessage_WithTextAndImage() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(messageService.sendMessage(eq("user-1"), eq("room-1"), eq("Message with image"), any()))
                .thenReturn(testMessage2);

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .file(imageFile)
                .param("userId", "user-1")
                .param("roomId", "room-1")
                .param("content", "Message with image"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messageId").value("msg-2"))
                .andExpect(jsonPath("$.content").value("Test message 2"))
                .andExpect(jsonPath("$.imageUrl").value("http://example.com/image.jpg"));
    }

    @Test
    void sendMessage_ShouldReturnBadRequest_WhenNoContentOrImage() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .param("userId", "user-1")
                .param("roomId", "room-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Either message content or image must be provided"));
    }

    @Test
    void sendMessage_ShouldReturnBadRequest_WhenEmptyContentAndNoImage() throws Exception {
        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .param("userId", "user-1")
                .param("roomId", "room-1")
                .param("content", ""))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Either message content or image must be provided"));
    }

    @Test
    void sendMessage_ShouldReturnBadRequest_WhenUserNotInRoom() throws Exception {
        // Given
        when(messageService.sendMessage(eq("user-1"), eq("room-1"), eq("Hello"), isNull()))
                .thenThrow(new IllegalArgumentException("User is not a member of this room"));

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .param("userId", "user-1")
                .param("roomId", "room-1")
                .param("content", "Hello"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User is not a member of this room"));
    }

    @Test
    void sendMessage_ShouldReturnBadRequest_WhenRoomNotFound() throws Exception {
        // Given
        when(messageService.sendMessage(eq("user-1"), eq("non-existent"), eq("Hello"), isNull()))
                .thenThrow(new IllegalArgumentException("Room not found"));

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .param("userId", "user-1")
                .param("roomId", "non-existent")
                .param("content", "Hello"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Room not found"));
    }

    @Test
    void sendMessage_ShouldUseDefaultUser_WhenUserIdNotProvided() throws Exception {
        // Given
        when(messageService.sendMessage(eq("default-user"), eq("room-1"), eq("Hello"), isNull()))
                .thenReturn(testMessage1);

        // When & Then
        mockMvc.perform(multipart("/api/messages")
                .param("roomId", "room-1")
                .param("content", "Hello"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.messageId").value("msg-1"));
    }

    @Test
    void getRoomMessages_ShouldReturnMessages() throws Exception {
        // Given
        when(messageService.getMessagesByRoomId("user-1", "room-1"))
                .thenReturn(testMessages);

        // When & Then
        mockMvc.perform(get("/api/rooms/room-1/messages")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].messageId").value("msg-1"))
                .andExpect(jsonPath("$[1].messageId").value("msg-2"))
                .andExpect(jsonPath("$[1].imageUrl").value("http://example.com/image.jpg"));
    }

    @Test
    void getRoomMessages_ShouldReturnBadRequest_WhenUserNotInRoom() throws Exception {
        // Given
        when(messageService.getMessagesByRoomId("user-1", "room-1"))
                .thenThrow(new IllegalArgumentException("User is not a member of this room"));

        // When & Then
        mockMvc.perform(get("/api/rooms/room-1/messages")
                .param("userId", "user-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User is not a member of this room"));
    }

    @Test
    void getRoomMessages_ShouldReturnBadRequest_WhenRoomNotFound() throws Exception {
        // Given
        when(messageService.getMessagesByRoomId("user-1", "non-existent"))
                .thenThrow(new IllegalArgumentException("Room not found"));

        // When & Then
        mockMvc.perform(get("/api/rooms/non-existent/messages")
                .param("userId", "user-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Room not found"));
    }

    @Test
    void getRoomMessages_ShouldUseDefaultUser_WhenUserIdNotProvided() throws Exception {
        // Given
        when(messageService.getMessagesByRoomId("default-user", "room-1"))
                .thenReturn(Arrays.asList(testMessage1));

        // When & Then
        mockMvc.perform(get("/api/rooms/room-1/messages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].messageId").value("msg-1"));
    }
}
