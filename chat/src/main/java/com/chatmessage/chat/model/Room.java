package com.chatmessage.chat.model;

import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String roomId;
    private String roomName;
    
    @ElementCollection
    private List<String> members;

    public Room() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room(String roomId, String roomName, List<String> members) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.members = members;
    }

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
        this.members = members;
    }

    public boolean isMember(String userId) {
        System.out.println("Checking if user " + userId + " is a member of room " + roomId);
        if (members == null) {
            System.out.println("Room has no members list");
            return false;
        }
        
        boolean isMember = members.contains(userId);
        System.out.println("User " + userId + " is member of room " + roomId + ": " + isMember);
        return isMember;
    }
}
