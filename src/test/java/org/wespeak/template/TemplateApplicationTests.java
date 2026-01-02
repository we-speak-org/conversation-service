package org.wespeak.template;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TemplateApplicationTests {

  @Test
  void contextLoads() {
    // Verify Spring context loads successfully without external dependencies
    // Kafka listeners are disabled via application-test.properties
  }
}
