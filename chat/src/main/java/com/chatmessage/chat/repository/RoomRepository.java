package com.chatmessage.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chatmessage.chat.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, String> {

    @Query("SELECT r FROM Room r JOIN r.members m WHERE m = :userId")
    List<Room> findByMembersContaining(@Param("userId") String userId);
}
