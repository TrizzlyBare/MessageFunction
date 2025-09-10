package com.chatmessage.chat.integration;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.chatmessage.chat.model.Message;
import com.chatmessage.chat.model.Room;
import com.chatmessage.chat.model.User;
import com.chatmessage.chat.repository.MessageRepository;
import com.chatmessage.chat.repository.RoomRepository;
import com.chatmessage.chat.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ChatApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MessageRepository messageRepository;

    private User testUser1;
    private User testUser2;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        // Clean up database
        messageRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();

        // Create test users
        testUser1 = new User();
        testUser1.setUserId("integration-user-1");
        testUser1.setUsername("integrationuser1");
        testUser1 = userRepository.save(testUser1);

        testUser2 = new User();
        testUser2.setUserId("integration-user-2");
        testUser2.setUsername("integrationuser2");
        testUser2 = userRepository.save(testUser2);

        // Create test room
        testRoom = new Room();
        testRoom.setRoomId("integration-room-1");
        testRoom.setRoomName("Integration Test Room");
        testRoom.setMembers(Arrays.asList(testUser1.getUserId(), testUser2.getUserId()));
        testRoom = roomRepository.save(testRoom);
    }

    @Test
    void fullWorkflow_CreateUserRoomAndSendMessages() throws Exception {
        // 1. Get all users
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").exists());

        // 2. Get rooms for user 1
        mockMvc.perform(get("/api/rooms")
                .param("userId", testUser1.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].roomName").value("Integration Test Room"));

        // 3. Send a text message
        mockMvc.perform(multipart("/api/messages")
                .param("userId", testUser1.getUserId())
                .param("roomId", testRoom.getRoomId())
                .param("content", "Hello integration test!"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Hello integration test!"))
                .andExpect(jsonPath("$.senderId").value(testUser1.getUserId()));

        // 4. Send an image message
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(multipart("/api/messages")
                .file(imageFile)
                .param("userId", testUser2.getUserId())
                .param("roomId", testRoom.getRoomId())
                .param("content", "Message with image"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Message with image"))
                .andExpect(jsonPath("$.senderId").value(testUser2.getUserId()))
                .andExpect(jsonPath("$.imageUrl").exists());

        // 5. Get messages from the room
        mockMvc.perform(get("/api/rooms/" + testRoom.getRoomId() + "/messages")
                .param("userId", testUser1.getUserId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("Hello integration test!"))
                .andExpect(jsonPath("$[1].content").value("Message with image"))
                .andExpect(jsonPath("$[1].imageUrl").exists());

        // 6. Verify data is actually in database
        List<Message> messages = messageRepository.findByRoomIdOrderByTimestamp(testRoom.getRoomId());
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0).getContent()).isEqualTo("Hello integration test!");
        assertThat(messages.get(1).getContent()).isEqualTo("Message with image");
        assertThat(messages.get(1).getImageUrl()).isNotNull();
    }

    @Test
    void unauthorizedAccess_ShouldReturnErrors() throws Exception {
        // Create a user not in the room
        User outsideUser = new User();
        outsideUser.setUserId("outside-user");
        outsideUser.setUsername("outsideuser");
        outsideUser = userRepository.save(outsideUser);

        // Try to send message to room user is not a member of
        mockMvc.perform(multipart("/api/messages")
                .param("userId", outsideUser.getUserId())
                .param("roomId", testRoom.getRoomId())
                .param("content", "Unauthorized message"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User is not a member of this room"));

        // Try to get messages from room user is not a member of
        mockMvc.perform(get("/api/rooms/" + testRoom.getRoomId() + "/messages")
                .param("userId", outsideUser.getUserId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("User is not a member of this room"));

        // Try to get room details user is not a member of
        mockMvc.perform(get("/api/rooms/" + testRoom.getRoomId())
                .param("userId", outsideUser.getUserId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User is not a member of this room"));
    }

    @Test
    void nonExistentResources_ShouldReturnErrors() throws Exception {
        // Try to send message to non-existent room
        mockMvc.perform(multipart("/api/messages")
                .param("userId", testUser1.getUserId())
                .param("roomId", "non-existent-room")
                .param("content", "Message to nowhere"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Room not found"));

        // Try to get messages from non-existent room
        mockMvc.perform(get("/api/rooms/non-existent-room/messages")
                .param("userId", testUser1.getUserId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Room not found"));

        // Try to get non-existent room details
        mockMvc.perform(get("/api/rooms/non-existent-room")
                .param("userId", testUser1.getUserId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Room not found"));
    }

    @Test
    void messageValidation_ShouldEnforceRules() throws Exception {
        // Try to send message with no content and no image
        mockMvc.perform(multipart("/api/messages")
                .param("userId", testUser1.getUserId())
                .param("roomId", testRoom.getRoomId()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Either message content or image must be provided"));

        // Try to send message with empty content and no image
        mockMvc.perform(multipart("/api/messages")
                .param("userId", testUser1.getUserId())
                .param("roomId", testRoom.getRoomId())
                .param("content", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Either message content or image must be provided"));

        // Send image-only message (should work)
        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockMvc.perform(multipart("/api/messages")
                .file(imageFile)
                .param("userId", testUser1.getUserId())
                .param("roomId", testRoom.getRoomId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.imageUrl").exists());
    }

    @Test
    void databaseOperations_ShouldPersistCorrectly() throws Exception {
        // Initial state - no messages
        assertThat(messageRepository.findAll()).isEmpty();

        // Send a message via API
        mockMvc.perform(multipart("/api/messages")
                .param("userId", testUser1.getUserId())
                .param("roomId", testRoom.getRoomId())
                .param("content", "Database test message"))
                .andExpect(status().isCreated());

        // Verify it's in database
        List<Message> messages = messageRepository.findAll();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("Database test message");
        assertThat(messages.get(0).getSenderId()).isEqualTo(testUser1.getUserId());
        assertThat(messages.get(0).getRoomId()).isEqualTo(testRoom.getRoomId());
        assertThat(messages.get(0).getTimestamp()).isNotNull();

        // Send another message
        mockMvc.perform(multipart("/api/messages")
                .param("userId", testUser2.getUserId())
                .param("roomId", testRoom.getRoomId())
                .param("content", "Second database test message"))
                .andExpect(status().isCreated());

        // Verify both messages are in database and ordered correctly
        List<Message> orderedMessages = messageRepository.findByRoomIdOrderByTimestamp(testRoom.getRoomId());
        assertThat(orderedMessages).hasSize(2);
        assertThat(orderedMessages.get(0).getContent()).isEqualTo("Database test message");
        assertThat(orderedMessages.get(1).getContent()).isEqualTo("Second database test message");
    }
}
