package com.chatmessage.chat.controller;

import com.chatmessage.chat.model.Room;
import com.chatmessage.chat.service.RoomService;
import com.chatmessage.chat.repository.RoomRepository;
import com.chatmessage.chat.repository.UserRepository;
import com.chatmessage.chat.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import jakarta.persistence.EntityManagerFactory;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@ActiveProfiles("test")
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoomService roomService;

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private MessageRepository messageRepository;

    // Prevent JPA autoconfiguration from requiring a real EntityManagerFactory
    @MockBean(name = "entityManagerFactory")
    private EntityManagerFactory entityManagerFactory;

    private Room testRoom1;
    private Room testRoom2;
    private List<Room> testRooms;

    @BeforeEach
    void setUp() {
        testRoom1 = new Room();
        testRoom1.setRoomId("room-1");
        testRoom1.setRoomName("Test Room 1");
        testRoom1.setMembers(Arrays.asList("user-1", "user-2"));

        testRoom2 = new Room();
        testRoom2.setRoomId("room-2");
        testRoom2.setRoomName("Test Room 2");
        testRoom2.setMembers(Arrays.asList("user-1", "user-3"));

        testRooms = Arrays.asList(testRoom1, testRoom2);
    }

    @Test
    void getUserRooms_ShouldReturnRoomsForUser() throws Exception {
        // Given
        when(roomService.getRoomsByUserId("user-1")).thenReturn(testRooms);

        // When & Then
        mockMvc.perform(get("/api/rooms")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roomId").value("room-1"))
                .andExpect(jsonPath("$[0].roomName").value("Test Room 1"))
                .andExpect(jsonPath("$[1].roomId").value("room-2"))
                .andExpect(jsonPath("$[1].roomName").value("Test Room 2"));
    }

    @Test
    void getUserRooms_ShouldUseDefaultUser_WhenUserIdNotProvided() throws Exception {
        // Given
        when(roomService.getRoomsByUserId("default-user")).thenReturn(Arrays.asList(testRoom1));

        // When & Then
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].roomId").value("room-1"));
    }

    @Test
    void getRoomById_ShouldReturnRoom_WhenUserIsMember() throws Exception {
        // Given
        when(roomService.getRoomById("user-1", "room-1")).thenReturn(testRoom1);

        // When & Then
        mockMvc.perform(get("/api/rooms/room-1")
                .param("userId", "user-1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roomId").value("room-1"))
                .andExpect(jsonPath("$.roomName").value("Test Room 1"))
                .andExpect(jsonPath("$.members").isArray())
                .andExpect(jsonPath("$.members.length()").value(2));
    }

    @Test
    void getRoomById_ShouldReturnNotFound_WhenUserNotMember() throws Exception {
        // Given
        when(roomService.getRoomById("user-1", "room-1"))
                .thenThrow(new IllegalArgumentException("User is not a member of this room"));

        // When & Then
        mockMvc.perform(get("/api/rooms/room-1")
                .param("userId", "user-1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("User is not a member of this room"));
    }

    @Test
    void getRoomById_ShouldReturnNotFound_WhenRoomDoesNotExist() throws Exception {
        // Given
        when(roomService.getRoomById("user-1", "non-existent"))
                .thenThrow(new IllegalArgumentException("Room not found"));

        // When & Then
        mockMvc.perform(get("/api/rooms/non-existent")
                .param("userId", "user-1"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Room not found"));
    }

    @Test
    void getAllRooms_ShouldReturnAllRooms() throws Exception {
        // Given
        when(roomService.getAllRooms()).thenReturn(testRooms);

        // When & Then
        mockMvc.perform(get("/api/rooms/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].roomName").value("Test Room 1"))
                .andExpect(jsonPath("$[1].roomName").value("Test Room 2"));
    }

    @Test
    void getMembershipSummary_ShouldReturnDetailedInfo() throws Exception {
        // Given
        when(roomService.getAllRooms()).thenReturn(testRooms);

        // When & Then
        mockMvc.perform(get("/api/rooms/membership-summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.totalRooms").value(2))
                .andExpect(jsonPath("$.rooms").isArray())
                .andExpect(jsonPath("$.rooms.length()").value(2))
                .andExpect(jsonPath("$.rooms[0].roomId").exists())
                .andExpect(jsonPath("$.rooms[0].roomName").exists())
                .andExpect(jsonPath("$.rooms[0].memberCount").exists())
                .andExpect(jsonPath("$.rooms[0].members").isArray());
    }

    @Test
    void createRoom_ShouldCreateNewRoom() throws Exception {
        // Given
        Room newRoom = new Room();
        newRoom.setRoomId("new-room");
        newRoom.setRoomName("New Room");
        newRoom.setMembers(Arrays.asList("user-1", "user-2"));

        when(roomService.createRoom(eq("New Room"), anyList())).thenReturn(newRoom);

        // When & Then
        mockMvc.perform(post("/api/rooms")
                .param("userId", "user-1")
                .param("roomName", "New Room")
                .param("members", "user-2"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.roomId").value("new-room"))
                .andExpect(jsonPath("$.roomName").value("New Room"));
    }

    @Test
    void createRoom_ShouldReturnBadRequest_WhenRoomNameEmpty() throws Exception {
        // Given
        when(roomService.createRoom(eq(""), anyList()))
                .thenThrow(new IllegalArgumentException("Room name cannot be empty"));

        // When & Then
        mockMvc.perform(post("/api/rooms")
                .param("userId", "user-1")
                .param("roomName", "")
                .param("members", "user-2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Room name cannot be empty"));
    }

    @Test
    void getUserRooms_ShouldReturnInternalServerError_WhenServiceThrowsException() throws Exception {
        // Given
        when(roomService.getRoomsByUserId("user-1"))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        mockMvc.perform(get("/api/rooms")
                .param("userId", "user-1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Database connection error"));
    }
}
