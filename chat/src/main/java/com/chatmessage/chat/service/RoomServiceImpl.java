package com.chatmessage.chat.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chatmessage.chat.model.Room;
import com.chatmessage.chat.repository.RoomRepository;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public Room createRoom(String roomName, List<String> members) {
        if (roomName == null || roomName.trim().isEmpty()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }

        if (members == null || members.isEmpty()) {
            throw new IllegalArgumentException("Room must have at least one member");
        }

        Room room = new Room();
        room.setRoomId(UUID.randomUUID().toString());
        room.setRoomName(roomName);
        room.setMembers(members);

        return roomRepository.save(room);
    }

    @Override
    public Room getRoomById(String userId, String roomId) {
        Room room = roomRepository.findById(roomId);

        if (room == null) {
            throw new IllegalArgumentException("Room not found");
        }

        if (!isUserInRoom(userId, roomId)) {
            throw new IllegalArgumentException("User is not a member of this room");
        }

        return room;
    }

    @Override
    public List<Room> getRoomsByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }

        return roomRepository.findByUserId(userId);
    }

    @Override
    public boolean isUserInRoom(String userId, String roomId) {
        Room room = roomRepository.findById(roomId);
        return room != null && room.isMember(userId);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
