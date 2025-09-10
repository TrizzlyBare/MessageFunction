package com.chatmessage.chat.controller;

import java.util.HashMap;
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

import com.chatmessage.chat.model.Room;
import com.chatmessage.chat.service.RoomService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(RoomController.class);
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    /**
     * Endpoint to create a new room
     */
    @PostMapping
    public ResponseEntity<?> createRoom(
            @RequestParam(value = "userId", defaultValue = "default-user") String userId,
            @RequestParam("roomName") String roomName,
            @RequestParam("members") List<String> members) {

        try {
            // Add the creator to the members list if not already included
            if (!members.contains(userId)) {
                members.add(userId);
            }

            Room room = roomService.createRoom(roomName, members);
            return ResponseEntity.status(HttpStatus.CREATED).body(room);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to get a specific room by ID
     */
    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(
            @RequestParam(value = "userId", defaultValue = "default-user") String userId,
            @PathVariable("roomId") String roomId) {

        try {
            Room room = roomService.getRoomById(userId, roomId);
            return ResponseEntity.ok(room);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Endpoint to get all rooms for a user
     */
    @GetMapping
    public ResponseEntity<?> getUserRooms(
            @RequestParam(value = "userId", defaultValue = "default-user") String userId) {

        logger.info("Received request to get user rooms for userId: {}", userId);

        try {
            List<Room> rooms = roomService.getRoomsByUserId(userId);
            logger.info("Found {} rooms for user {}", rooms.size(), userId);
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            logger.error("Error getting rooms for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Debug endpoint to get all rooms in the system
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllRooms() {
        logger.info("Received request to get all rooms");

        try {
            List<Room> rooms = roomService.getAllRooms();
            logger.info("Found {} total rooms in the system", rooms.size());
            return ResponseEntity.ok(rooms);
        } catch (Exception e) {
            logger.error("Error getting all rooms: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Debug endpoint to get membership summary
     */
    @GetMapping("/membership-summary")
    public ResponseEntity<?> getMembershipSummary() {
        logger.info("Received request to get membership summary");

        try {
            List<Room> rooms = roomService.getAllRooms();
            Map<String, Object> summary = new HashMap<>();

            // Create room summary
            List<Map<String, Object>> roomSummary = new java.util.ArrayList<>();
            for (Room room : rooms) {
                Map<String, Object> roomInfo = new HashMap<>();
                roomInfo.put("roomId", room.getRoomId());
                roomInfo.put("roomName", room.getRoomName());
                roomInfo.put("memberCount", room.getMembers().size());
                roomInfo.put("members", room.getMembers());
                roomSummary.add(roomInfo);
            }

            summary.put("rooms", roomSummary);
            summary.put("totalRooms", rooms.size());

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            logger.error("Error getting membership summary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
