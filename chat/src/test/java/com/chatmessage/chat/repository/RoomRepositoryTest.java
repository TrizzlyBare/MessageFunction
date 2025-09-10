package com.chatmessage.chat.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.chatmessage.chat.model.Room;

@DataJpaTest
@ActiveProfiles("test")
class RoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoomRepository roomRepository;

    private Room testRoom1;
    private Room testRoom2;
    private Room testRoom3;

    @BeforeEach
    void setUp() {
        // Create test rooms with different member combinations
        testRoom1 = new Room();
        testRoom1.setRoomId("room-1");
        testRoom1.setRoomName("Test Room 1");
        testRoom1.setMembers(Arrays.asList("user-1", "user-2", "user-3"));

        testRoom2 = new Room();
        testRoom2.setRoomId("room-2");
        testRoom2.setRoomName("Test Room 2");
        testRoom2.setMembers(Arrays.asList("user-1", "user-4"));

        testRoom3 = new Room();
        testRoom3.setRoomId("room-3");
        testRoom3.setRoomName("Test Room 3");
        testRoom3.setMembers(Arrays.asList("user-2", "user-4", "user-5"));

        // Persist test data
        entityManager.persistAndFlush(testRoom1);
        entityManager.persistAndFlush(testRoom2);
        entityManager.persistAndFlush(testRoom3);
    }

    @Test
    void findByMembersContaining_ShouldReturnRoomsForUser1() {
        // When
        List<Room> rooms = roomRepository.findByMembersContaining("user-1");

        // Then
        assertThat(rooms).hasSize(2);
        assertThat(rooms).extracting("roomName")
                .containsExactlyInAnyOrder("Test Room 1", "Test Room 2");
    }

    @Test
    void findByMembersContaining_ShouldReturnRoomsForUser2() {
        // When
        List<Room> rooms = roomRepository.findByMembersContaining("user-2");

        // Then
        assertThat(rooms).hasSize(2);
        assertThat(rooms).extracting("roomName")
                .containsExactlyInAnyOrder("Test Room 1", "Test Room 3");
    }

    @Test
    void findByMembersContaining_ShouldReturnRoomsForUser4() {
        // When
        List<Room> rooms = roomRepository.findByMembersContaining("user-4");

        // Then
        assertThat(rooms).hasSize(2);
        assertThat(rooms).extracting("roomName")
                .containsExactlyInAnyOrder("Test Room 2", "Test Room 3");
    }

    @Test
    void findByMembersContaining_ShouldReturnEmptyListForNonMember() {
        // When
        List<Room> rooms = roomRepository.findByMembersContaining("non-member");

        // Then
        assertThat(rooms).isEmpty();
    }

    @Test
    void findById_ShouldReturnRoom_WhenRoomExists() {
        // When
        Optional<Room> found = roomRepository.findById("room-1");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getRoomName()).isEqualTo("Test Room 1");
        assertThat(found.get().getMembers()).containsExactlyInAnyOrder("user-1", "user-2", "user-3");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenRoomDoesNotExist() {
        // When
        Optional<Room> found = roomRepository.findById("non-existent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistRoom() {
        // Given
        Room newRoom = new Room();
        newRoom.setRoomId("new-room");
        newRoom.setRoomName("New Test Room");
        newRoom.setMembers(Arrays.asList("user-1", "user-2"));

        // When
        Room saved = roomRepository.save(newRoom);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getRoomId()).isEqualTo("new-room");
        assertThat(saved.getRoomName()).isEqualTo("New Test Room");
        assertThat(saved.getMembers()).containsExactlyInAnyOrder("user-1", "user-2");

        // Verify it's actually persisted
        Optional<Room> found = roomRepository.findById("new-room");
        assertThat(found).isPresent();
    }

    @Test
    void findAll_ShouldReturnAllRooms() {
        // When
        List<Room> rooms = roomRepository.findAll();

        // Then
        assertThat(rooms).hasSize(3);
        assertThat(rooms).extracting("roomName")
                .containsExactlyInAnyOrder("Test Room 1", "Test Room 2", "Test Room 3");
    }

    @Test
    void isMember_ShouldReturnTrueForMember() {
        // When
        Optional<Room> room = roomRepository.findById("room-1");

        // Then
        assertThat(room).isPresent();
        assertThat(room.get().isMember("user-1")).isTrue();
        assertThat(room.get().isMember("user-2")).isTrue();
        assertThat(room.get().isMember("user-3")).isTrue();
    }

    @Test
    void isMember_ShouldReturnFalseForNonMember() {
        // When
        Optional<Room> room = roomRepository.findById("room-1");

        // Then
        assertThat(room).isPresent();
        assertThat(room.get().isMember("user-4")).isFalse();
        assertThat(room.get().isMember("non-existent")).isFalse();
    }
}
