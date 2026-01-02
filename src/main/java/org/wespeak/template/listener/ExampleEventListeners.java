package org.wespeak.template.listener;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wespeak.template.service.ExampleService;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExampleEventListeners {

  private final ExampleService exampleService;

  /** Payload for ExampleCreated event */
  public record ExampleCreatedPayload(String id, String code, String name) {}

  /**
   * Example listener for "example.created" events Configuration in application.properties: -
   * spring.cloud.stream.bindings.exampleCreatedListener-in-0.destination=example.events -
   * spring.cloud.stream.bindings.exampleCreatedListener-in-0.group=template-service
   */
  @Bean
  public Consumer<CloudEvent<ExampleCreatedPayload>> exampleCreatedListener() {
    return event -> {
      log.info(
          "Received ExampleCreated event: id={}, type={}, source={}",
          event.id(),
          event.type(),
          event.source());

      ExampleCreatedPayload payload = event.data();
      log.info("Processing example: code={}, name={}", payload.code(), payload.name());

      // Delegate to service layer
      // exampleService.handleExampleCreated(payload);
    };
  }
}
