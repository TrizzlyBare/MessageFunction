package com.chatmessage.chat.repository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.chatmessage.chat.model.User;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser1;
    private User testUser2;

    @BeforeEach
    void setUp() {
        // Create test users
        testUser1 = new User();
        testUser1.setUserId("user-1");
        testUser1.setUsername("testuser1");

        testUser2 = new User();
        testUser2.setUserId("user-2");
        testUser2.setUsername("testuser2");

        // Persist test data
        entityManager.persistAndFlush(testUser1);
        entityManager.persistAndFlush(testUser2);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUsernameExists() {
        // When
        Optional<User> found = userRepository.findByUsername("testuser1");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo("user-1");
        assertThat(found.get().getUsername()).isEqualTo("testuser1");
    }

    @Test
    void findByUsername_ShouldReturnEmpty_WhenUsernameDoesNotExist() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void findById_ShouldReturnUser_WhenUserIdExists() {
        // When
        Optional<User> found = userRepository.findById("user-1");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser1");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserIdDoesNotExist() {
        // When
        Optional<User> found = userRepository.findById("nonexistent-id");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistUser() {
        // Given
        User newUser = new User();
        newUser.setUserId("new-user-id");
        newUser.setUsername("newuser");

        // When
        User saved = userRepository.save(newUser);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getUserId()).isEqualTo("new-user-id");
        assertThat(saved.getUsername()).isEqualTo("newuser");

        // Verify it's actually persisted
        Optional<User> found = userRepository.findById("new-user-id");
        assertThat(found).isPresent();
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        // When
        var users = userRepository.findAll();

        // Then
        assertThat(users).hasSize(2);
        assertThat(users).extracting("username")
                .containsExactlyInAnyOrder("testuser1", "testuser2");
    }

    @Test
    void deleteById_ShouldRemoveUser() {
        // Given
        assertThat(userRepository.findById("user-1")).isPresent();

        // When
        userRepository.deleteById("user-1");

        // Then
        assertThat(userRepository.findById("user-1")).isEmpty();
        assertThat(userRepository.findAll()).hasSize(1);
    }
}
