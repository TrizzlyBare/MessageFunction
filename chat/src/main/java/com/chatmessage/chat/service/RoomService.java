package com.chatmessage.chat.service;

import java.util.List;

import com.chatmessage.chat.model.Room;

public interface RoomService {

    Room createRoom(String roomName, List<String> members);

    Room getRoomById(String userId, String roomId);

    List<Room> getRoomsByUserId(String userId);

    boolean isUserInRoom(String userId, String roomId);

    List<Room> getAllRooms(); // Added method to get all rooms
}
