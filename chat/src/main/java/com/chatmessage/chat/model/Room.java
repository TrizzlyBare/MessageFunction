package com.chatmessage.chat.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    private String roomId;

    @Column(nullable = false)
    private String roomName;

    @ElementCollection
    @CollectionTable(name = "room_members", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "user_id")
    private List<String> members = new ArrayList<>();

    public Room() {
    }

    public Room(String roomId, String roomName, List<String> members) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.members = members != null ? new ArrayList<>(members) : new ArrayList<>();
    }

    public boolean isMember(String userId) {
        return members != null && members.contains(userId);
    }

    // Getters and setters
    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members != null ? new ArrayList<>(members) : new ArrayList<>();
    }
}
