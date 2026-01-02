package org.wespeak.template.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Sample service that handles business logic for events. Listeners should delegate to this layer
 * instead of implementing logic directly.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SampleService {

  /** Sample event payload record */
  public record SampleEventPayload(String id, String message) {}

  /**
   * Handles sample event processing. This is where business logic should be implemented.
   *
   * @param payload The event payload
   */
  public void handleSampleEvent(SampleEventPayload payload) {
    log.info("Processing sample event: {}", payload);

    // TODO: Implement business logic here
    // Example: save to database, call other services, etc.

    log.info("Sample event processed successfully: {}", payload.id());
  }
}
