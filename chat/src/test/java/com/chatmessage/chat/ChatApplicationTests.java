package com.chatmessage.chat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ChatApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
        // The @Profile("!test") on DataInitializer will prevent it from running
    }
}
