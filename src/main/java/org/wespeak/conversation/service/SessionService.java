package org.wespeak.conversation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.wespeak.conversation.dto.*;
import org.wespeak.conversation.entity.*;
import org.wespeak.conversation.exception.ResourceNotFoundException;
import org.wespeak.conversation.exception.SessionException;
import org.wespeak.conversation.repository.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final ParticipantRepository participantRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final RegistrationService registrationService;
    private final ConversationEventPublisher eventPublisher;

    @Value("${app.conversation.grace-period-minutes:5}")
    private int gracePeriodMinutes;

    @Value("${app.conversation.max-participants:8}")
    private int maxParticipants;

    @Value("${app.conversation.min-participants:2}")
    private int minParticipants;

    /**
     * Join a session.
     */
    public SessionDto joinSession(String userId, JoinSessionRequest request) {
        String timeSlotId = request.getTimeSlotId();

        // Check if user is registered
        if (!registrationService.isUserRegistered(timeSlotId, userId)) {
            throw SessionException.notRegistered();
        }

        // Check if user is already in another active session
        Optional<Participant> existingParticipant = participantRepository.findByUserIdAndStatusNot(
                userId, Participant.Status.disconnected);
        if (existingParticipant.isPresent()) {
            throw SessionException.alreadyInSession();
        }

        // Get or create session for this time slot
        Session session = getOrCreateSession(timeSlotId);

        // Check join window
        TimeSlot slot = timeSlotRepository.findById(timeSlotId).orElseThrow();
        Instant joinDeadline = slot.getStartTime().plus(gracePeriodMinutes, ChronoUnit.MINUTES);
        if (Instant.now().isAfter(joinDeadline)) {
            throw SessionException.joinWindowClosed();
        }

        // Check session status
        if (session.getStatus() == Session.Status.ended) {
            throw SessionException.sessionEnded();
        }

        // Check capacity
        long activeParticipants = participantRepository.countBySessionIdAndStatus(
                session.getId(), Participant.Status.connected);
        if (activeParticipants >= maxParticipants) {
            throw SessionException.sessionFull();
        }

        // Create or update participant
        Participant participant = participantRepository.findBySessionIdAndUserId(session.getId(), userId)
                .orElseGet(() -> Participant.builder()
                        .sessionId(session.getId())
                        .userId(userId)
                        .displayName(request.getDisplayName() != null ? request.getDisplayName() : "User")
                        .build());

        participant.setStatus(Participant.Status.connected);
        participant.setJoinedAt(Instant.now());
        participant.setRecordingConsent(request.getRecordingConsent());
        participantRepository.save(participant);

        // Update recording status if this participant consented
        if (request.getRecordingConsent()) {
            session.setRecordingEnabled(true);
            sessionRepository.save(session);
        }

        // Check if session should become active
        activateSessionIfReady(session);

        // Mark registration as attended
        registrationService.markAttended(timeSlotId, userId);

        log.info("User {} joined session {}", userId, session.getId());
        return toSessionDto(session);
    }

    /**
     * Get current session for a user.
     */
    public SessionDto getCurrentSession(String userId) {
        Participant participant = participantRepository.findByUserIdAndStatusNot(
                userId, Participant.Status.disconnected)
                .orElseThrow(SessionException::noActiveSession);

        Session session = sessionRepository.findById(participant.getSessionId())
                .orElseThrow(() -> ResourceNotFoundException.sessionNotFound(participant.getSessionId()));

        return toSessionDto(session);
    }

    /**
     * Update media state (camera/mic).
     */
    public ParticipantDto updateMediaState(String userId, MediaStateRequest request) {
        Participant participant = participantRepository.findByUserIdAndStatusNot(
                userId, Participant.Status.disconnected)
                .orElseThrow(SessionException::noActiveSession);

        if (request.getCameraEnabled() != null) {
            participant.setCameraEnabled(request.getCameraEnabled());
        }
        if (request.getMicEnabled() != null) {
            participant.setMicEnabled(request.getMicEnabled());
        }

        participant = participantRepository.save(participant);
        log.debug("User {} updated media state: camera={}, mic={}", 
                userId, participant.getCameraEnabled(), participant.getMicEnabled());

        return toParticipantDto(participant);
    }

    /**
     * Leave the current session.
     */
    public void leaveSession(String userId) {
        Participant participant = participantRepository.findByUserIdAndStatusNot(
                userId, Participant.Status.disconnected)
                .orElseThrow(SessionException::noActiveSession);

        participant.setStatus(Participant.Status.disconnected);
        participant.setLeftAt(Instant.now());
        participantRepository.save(participant);

        // Check if session should end
        Session session = sessionRepository.findById(participant.getSessionId()).orElse(null);
        if (session != null) {
            checkAndEndSession(session);
        }

        log.info("User {} left session {}", userId, participant.getSessionId());
    }

    /**
     * Get session history for a user.
     */
    public SessionHistoryResponse getSessionHistory(String userId, int page, int size) {
        Page<Participant> participantPage = participantRepository.findByUserIdOrderByJoinedAtDesc(
                userId, PageRequest.of(page, size));

        List<SessionHistoryResponse.SessionHistoryItem> items = participantPage.getContent().stream()
                .map(p -> {
                    Session session = sessionRepository.findById(p.getSessionId()).orElse(null);
                    int participantCount = session != null 
                            ? (int) participantRepository.countBySessionIdAndStatus(session.getId(), Participant.Status.connected)
                            : 0;
                    return SessionHistoryResponse.SessionHistoryItem.builder()
                            .sessionId(p.getSessionId())
                            .targetLanguageCode(session != null ? session.getTargetLanguageCode() : null)
                            .level(session != null && session.getLevel() != null ? session.getLevel().name() : null)
                            .joinedAt(p.getJoinedAt())
                            .leftAt(p.getLeftAt())
                            .participantCount(participantCount)
                            .build();
                })
                .collect(Collectors.toList());

        return SessionHistoryResponse.builder()
                .sessions(items)
                .total(participantPage.getTotalElements())
                .hasMore(participantPage.hasNext())
                .build();
    }

    /**
     * Scheduled job to create sessions for upcoming time slots.
     * Runs every minute.
     */
    @Scheduled(fixedRate = 60000)
    public void createSessionsForUpcomingSlots() {
        Instant now = Instant.now();
        Instant threshold = now.plus(1, ChronoUnit.MINUTES);

        List<TimeSlot> upcomingSlots = timeSlotRepository.findByStartTimeBetweenAndIsActive(now, threshold, true);

        for (TimeSlot slot : upcomingSlots) {
            if (sessionRepository.findByTimeSlotId(slot.getId()).isEmpty()) {
                createSessionForTimeSlot(slot);
            }
        }
    }

    /**
     * Scheduled job to end sessions that have passed their end time.
     * Runs every minute.
     */
    @Scheduled(fixedRate = 60000)
    public void endExpiredSessions() {
        List<Session> activeSessions = sessionRepository.findByStatusOrderByCreatedAtDesc(
                Session.Status.active, PageRequest.of(0, 100)).getContent();

        Instant now = Instant.now();
        for (Session session : activeSessions) {
            TimeSlot slot = timeSlotRepository.findById(session.getTimeSlotId()).orElse(null);
            if (slot != null && now.isAfter(slot.getEndTime())) {
                endSession(session);
            }
        }
    }

    private Session getOrCreateSession(String timeSlotId) {
        return sessionRepository.findByTimeSlotIdAndStatusIn(
                timeSlotId, Session.Status.waiting, Session.Status.active)
                .orElseGet(() -> {
                    TimeSlot slot = timeSlotRepository.findById(timeSlotId).orElseThrow();
                    return createSessionForTimeSlot(slot);
                });
    }

    private Session createSessionForTimeSlot(TimeSlot slot) {
        Session session = Session.builder()
                .timeSlotId(slot.getId())
                .targetLanguageCode(slot.getTargetLanguageCode())
                .level(slot.getLevel())
                .status(Session.Status.waiting)
                .recordingEnabled(false)
                .build();

        session = sessionRepository.save(session);
        log.info("Created session {} for time slot {}", session.getId(), slot.getId());
        return session;
    }

    private void activateSessionIfReady(Session session) {
        long connectedCount = participantRepository.countBySessionIdAndStatus(
                session.getId(), Participant.Status.connected);

        if (connectedCount >= minParticipants && session.getStatus() == Session.Status.waiting) {
            session.setStatus(Session.Status.active);
            session.setStartedAt(Instant.now());
            sessionRepository.save(session);

            // Publish session started event
            List<Participant> participants = participantRepository.findBySessionIdAndStatusNot(
                    session.getId(), Participant.Status.disconnected);
            eventPublisher.publishSessionStarted(session, participants);

            log.info("Session {} activated with {} participants", session.getId(), connectedCount);
        }
    }

    private void checkAndEndSession(Session session) {
        long connectedCount = participantRepository.countBySessionIdAndStatus(
                session.getId(), Participant.Status.connected);

        if (connectedCount == 0 && session.getStatus() == Session.Status.active) {
            endSession(session);
        }
    }

    private void endSession(Session session) {
        session.setStatus(Session.Status.ended);
        session.setEndedAt(Instant.now());
        sessionRepository.save(session);

        // Disconnect all remaining participants
        List<Participant> participants = participantRepository.findBySessionId(session.getId());
        for (Participant p : participants) {
            if (p.getStatus() != Participant.Status.disconnected) {
                p.setStatus(Participant.Status.disconnected);
                p.setLeftAt(Instant.now());
                participantRepository.save(p);
            }
        }

        // Mark no-shows
        TimeSlot slot = timeSlotRepository.findById(session.getTimeSlotId()).orElse(null);
        if (slot != null) {
            List<Registration> registrations = registrationService.getRegisteredUsers(slot.getId());
            for (Registration reg : registrations) {
                if (reg.getStatus() == Registration.Status.registered) {
                    registrationService.markNoShow(slot.getId(), reg.getUserId());
                }
            }
        }

        // Publish session ended event
        eventPublisher.publishSessionEnded(session, participants);

        log.info("Session {} ended", session.getId());
    }

    private SessionDto toSessionDto(Session session) {
        List<Participant> participants = participantRepository.findBySessionIdAndStatusNot(
                session.getId(), Participant.Status.disconnected);

        return SessionDto.builder()
                .id(session.getId())
                .timeSlotId(session.getTimeSlotId())
                .targetLanguageCode(session.getTargetLanguageCode())
                .level(session.getLevel())
                .status(session.getStatus())
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .recordingEnabled(session.getRecordingEnabled())
                .participants(participants.stream().map(this::toParticipantDto).collect(Collectors.toList()))
                .build();
    }

    private ParticipantDto toParticipantDto(Participant participant) {
        return ParticipantDto.builder()
                .id(participant.getId())
                .userId(participant.getUserId())
                .displayName(participant.getDisplayName())
                .status(participant.getStatus())
                .cameraEnabled(participant.getCameraEnabled())
                .micEnabled(participant.getMicEnabled())
                .recordingConsent(participant.getRecordingConsent())
                .joinedAt(participant.getJoinedAt())
                .build();
    }
}
