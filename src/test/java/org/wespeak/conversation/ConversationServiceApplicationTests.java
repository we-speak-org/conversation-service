package org.wespeak.conversation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
class ConversationServiceApplicationTests {

    @Test
    void contextLoads() {
        // Test that the application context loads with Testcontainers MongoDB
    }
}
