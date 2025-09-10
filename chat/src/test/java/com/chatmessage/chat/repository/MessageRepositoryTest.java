package com.chatmessage.chat.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.chatmessage.chat.model.Message;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MessageRepository messageRepository;

    private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        // Create test messages for different rooms
        message1 = new Message();
        message1.setMessageId("msg-1");
        message1.setSenderId("user-1");
        message1.setRoomId("room-1");
        message1.setContent("First message in room 1");
        message1.setTimestamp(now.minusMinutes(10));

        message2 = new Message();
        message2.setMessageId("msg-2");
        message2.setSenderId("user-2");
        message2.setRoomId("room-1");
        message2.setContent("Second message in room 1");
        message2.setImageUrl("http://example.com/image1.jpg");
        message2.setTimestamp(now.minusMinutes(5));

        message3 = new Message();
        message3.setMessageId("msg-3");
        message3.setSenderId("user-1");
        message3.setRoomId("room-2");
        message3.setContent("First message in room 2");
        message3.setTimestamp(now.minusMinutes(3));

        message4 = new Message();
        message4.setMessageId("msg-4");
        message4.setSenderId("user-3");
        message4.setRoomId("room-1");
        message4.setContent("Third message in room 1");
        message4.setTimestamp(now);

        // Persist test data
        entityManager.persistAndFlush(message1);
        entityManager.persistAndFlush(message2);
        entityManager.persistAndFlush(message3);
        entityManager.persistAndFlush(message4);
    }

    @Test
    void findByRoomIdOrderByTimestamp_ShouldReturnMessagesInChronologicalOrder() {
        // When
        List<Message> messages = messageRepository.findByRoomIdOrderByTimestamp("room-1");

        // Then
        assertThat(messages).hasSize(3);
        assertThat(messages).extracting("content")
                .containsExactly(
                        "First message in room 1",
                        "Second message in room 1",
                        "Third message in room 1"
                );
        assertThat(messages).extracting("messageId")
                .containsExactly("msg-1", "msg-2", "msg-4");
    }

    @Test
    void findByRoomIdOrderByTimestamp_ShouldReturnEmptyListForNonExistentRoom() {
        // When
        List<Message> messages = messageRepository.findByRoomIdOrderByTimestamp("non-existent-room");

        // Then
        assertThat(messages).isEmpty();
    }

    @Test
    void findByRoomId_ShouldReturnAllMessagesForRoom() {
        // When
        List<Message> messages = messageRepository.findByRoomId("room-1");

        // Then
        assertThat(messages).hasSize(3);
        assertThat(messages).extracting("senderId")
                .containsExactlyInAnyOrder("user-1", "user-2", "user-3");
    }

    @Test
    void findByRoomId_ShouldReturnSingleMessageForRoom2() {
        // When
        List<Message> messages = messageRepository.findByRoomId("room-2");

        // Then
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getContent()).isEqualTo("First message in room 2");
        assertThat(messages.get(0).getSenderId()).isEqualTo("user-1");
    }

    @Test
    void findById_ShouldReturnMessage_WhenMessageExists() {
        // When
        Optional<Message> found = messageRepository.findById("msg-1");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getContent()).isEqualTo("First message in room 1");
        assertThat(found.get().getSenderId()).isEqualTo("user-1");
        assertThat(found.get().getRoomId()).isEqualTo("room-1");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenMessageDoesNotExist() {
        // When
        Optional<Message> found = messageRepository.findById("non-existent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistMessage() {
        // Given
        Message newMessage = new Message();
        newMessage.setMessageId("new-msg");
        newMessage.setSenderId("user-4");
        newMessage.setRoomId("room-1");
        newMessage.setContent("New test message");
        newMessage.setTimestamp(LocalDateTime.now());

        // When
        Message saved = messageRepository.save(newMessage);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getMessageId()).isEqualTo("new-msg");
        assertThat(saved.getContent()).isEqualTo("New test message");

        // Verify it's actually persisted
        Optional<Message> found = messageRepository.findById("new-msg");
        assertThat(found).isPresent();
    }

    @Test
    void save_ShouldPersistMessageWithImage() {
        // Given
        Message imageMessage = new Message();
        imageMessage.setMessageId("img-msg");
        imageMessage.setSenderId("user-1");
        imageMessage.setRoomId("room-2");
        imageMessage.setContent("Message with image");
        imageMessage.setImageUrl("http://example.com/test-image.jpg");
        imageMessage.setTimestamp(LocalDateTime.now());

        // When
        Message saved = messageRepository.save(imageMessage);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getImageUrl()).isEqualTo("http://example.com/test-image.jpg");
        assertThat(saved.getContent()).isEqualTo("Message with image");

        // Verify it's persisted correctly
        Optional<Message> found = messageRepository.findById("img-msg");
        assertThat(found).isPresent();
        assertThat(found.get().getImageUrl()).isEqualTo("http://example.com/test-image.jpg");
    }

    @Test
    void findAll_ShouldReturnAllMessages() {
        // When
        List<Message> messages = messageRepository.findAll();

        // Then
        assertThat(messages).hasSize(4);
        assertThat(messages).extracting("messageId")
                .containsExactlyInAnyOrder("msg-1", "msg-2", "msg-3", "msg-4");
    }

    @Test
    void deleteById_ShouldRemoveMessage() {
        // Given
        assertThat(messageRepository.findById("msg-1")).isPresent();

        // When
        messageRepository.deleteById("msg-1");

        // Then
        assertThat(messageRepository.findById("msg-1")).isEmpty();
        assertThat(messageRepository.findAll()).hasSize(3);
    }
}
