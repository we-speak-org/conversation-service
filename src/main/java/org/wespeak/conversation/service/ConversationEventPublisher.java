package org.wespeak.conversation.service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wespeak.conversation.entity.Participant;
import org.wespeak.conversation.entity.Session;

/**
 * Publisher for conversation events. Currently logs events - will be integrated with Kafka when
 * Spring Cloud Stream becomes compatible with Spring Boot 4.
 */
@Slf4j
@Service
public class ConversationEventPublisher {

  private static final String SOURCE = "conversation-service";

  public void publishSessionStarted(Session session, List<Participant> participants) {
    Map<String, Object> payload =
        Map.of(
            "sessionId", session.getId(),
            "timeSlotId", session.getTimeSlotId(),
            "targetLanguageCode", session.getTargetLanguageCode(),
            "level", session.getLevel() != null ? session.getLevel().name() : "",
            "participantIds",
                participants.stream().map(Participant::getUserId).collect(Collectors.toList()),
            "recordingEnabled", session.getRecordingEnabled());

    logEvent("session.started", payload, session.getId());
    log.info("Published session.started event for sessionId: {}", session.getId());
  }

  public void publishSessionEnded(Session session, List<Participant> participants) {
    List<Map<String, Object>> participantData =
        participants.stream()
            .map(
                p ->
                    Map.<String, Object>of(
                        "userId",
                        p.getUserId(),
                        "joinedAt",
                        p.getJoinedAt() != null ? p.getJoinedAt().toString() : "",
                        "leftAt",
                        p.getLeftAt() != null ? p.getLeftAt().toString() : "",
                        "recordingConsent",
                        p.getRecordingConsent()))
            .collect(Collectors.toList());

    long durationSeconds = 0;
    if (session.getStartedAt() != null && session.getEndedAt() != null) {
      durationSeconds = Duration.between(session.getStartedAt(), session.getEndedAt()).getSeconds();
    }

    Map<String, Object> payload =
        Map.of(
            "sessionId", session.getId(),
            "participants", participantData,
            "durationSeconds", durationSeconds);

    logEvent("session.ended", payload, session.getId());
    log.info("Published session.ended event for sessionId: {}", session.getId());
  }

  public void publishSessionRecorded(
      Session session, String recordingUrl, List<Participant> consentedParticipants) {
    List<Map<String, Object>> participantData =
        consentedParticipants.stream()
            .map(
                p ->
                    Map.<String, Object>of(
                        "userId",
                        p.getUserId(),
                        "displayName",
                        p.getDisplayName() != null ? p.getDisplayName() : ""))
            .collect(Collectors.toList());

    long durationSeconds = 0;
    if (session.getStartedAt() != null && session.getEndedAt() != null) {
      durationSeconds = Duration.between(session.getStartedAt(), session.getEndedAt()).getSeconds();
    }

    Map<String, Object> payload =
        Map.of(
            "sessionId",
            session.getId(),
            "recordingUrl",
            recordingUrl,
            "targetLanguageCode",
            session.getTargetLanguageCode(),
            "level",
            session.getLevel() != null ? session.getLevel().name() : "",
            "durationSeconds",
            durationSeconds,
            "participantsWithConsent",
            participantData);

    logEvent("session.recorded", payload, session.getId());
    log.info(
        "Published session.recorded event for sessionId: {}, url: {}",
        session.getId(),
        recordingUrl);
  }

  private void logEvent(String eventType, Map<String, Object> payload, String partitionKey) {
    Map<String, Object> event =
        Map.of(
            "eventType",
            eventType,
            "version",
            "1.0",
            "timestamp",
            Instant.now().toString(),
            "payload",
            payload,
            "metadata",
            Map.of("correlationId", UUID.randomUUID().toString(), "source", SOURCE));

    // TODO: Integrate with Kafka when Spring Cloud Stream supports Spring Boot 4
    log.debug("Event [{}]: {}", eventType, event);
  }
}
