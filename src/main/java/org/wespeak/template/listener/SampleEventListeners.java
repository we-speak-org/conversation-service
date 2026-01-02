package org.wespeak.template.listener;

import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.wespeak.template.service.SampleService;
import org.wespeak.template.service.SampleService.SampleEventPayload;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SampleEventListeners {

  private final SampleService sampleService;

  /**
   * Sample listener for "sample.event" topic Configuration in application.properties: -
   * spring.cloud.stream.bindings.sampleEventListener-in-0.destination=sample.events -
   * spring.cloud.stream.bindings.sampleEventListener-in-0.group=template-service
   */
  @Bean
  public Consumer<CloudEvent<SampleEventPayload>> sampleEventListener() {
    return event -> {
      log.info(
          "Received Sample event: id={}, type={}, source={}",
          event.id(),
          event.type(),
          event.source());

      // Delegate to service layer (no business logic here)
      sampleService.handleSampleEvent(event.data());
    };
  }
}
