package com.chatmessage.chat.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import com.chatmessage.chat.model.Room;

@Repository
public class RoomRepository {

    // In-memory store for rooms, replace with database implementation in production
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room save(Room room) {
        rooms.put(room.getRoomId(), room);
        return room;
    }

    public Room findById(String roomId) {
        return rooms.get(roomId);
    }

    public List<Room> findByUserId(String userId) {
        List<Room> userRooms = new ArrayList<>();
        System.out.println("Finding rooms for user: " + userId);
        System.out.println("Total rooms in repository: " + rooms.size());
        
        for (Room room : rooms.values()) {
            System.out.println("Checking room: " + room.getRoomId() + " - " + room.getRoomName());
            System.out.println("Room members: " + room.getMembers());
            if (room.isMember(userId)) {
                System.out.println("User " + userId + " is a member of room " + room.getRoomId());
                userRooms.add(room);
            } else {
                System.out.println("User " + userId + " is NOT a member of room " + room.getRoomId());
            }
        }
        System.out.println("Found " + userRooms.size() + " rooms for user " + userId);
        return userRooms;
    }

    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }
}
