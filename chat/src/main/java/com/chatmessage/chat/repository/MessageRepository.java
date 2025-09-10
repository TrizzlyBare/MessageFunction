package com.chatmessage.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chatmessage.chat.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    @Query("SELECT m FROM Message m WHERE m.roomId = :roomId ORDER BY m.timestamp ASC")
    List<Message> findByRoomIdOrderByTimestamp(@Param("roomId") String roomId);

    List<Message> findByRoomId(String roomId);
}
