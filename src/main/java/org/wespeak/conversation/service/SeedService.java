package org.wespeak.conversation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.wespeak.conversation.entity.*;
import org.wespeak.conversation.repository.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Service to seed the database with test data.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SeedService {

    private final TimeSlotRepository timeSlotRepository;
    private final RegistrationRepository registrationRepository;
    private final SessionRepository sessionRepository;
    private final ParticipantRepository participantRepository;

    /**
     * Clear all data and seed fresh test data.
     */
    public Map<String, Object> seedDatabase() {
        log.info("Starting database seed...");

        // Clear existing data
        participantRepository.deleteAll();
        sessionRepository.deleteAll();
        registrationRepository.deleteAll();
        timeSlotRepository.deleteAll();

        // Create time slots for the next 7 days
        List<TimeSlot> slots = createTimeSlots();

        log.info("Database seeded successfully: {} time slots created", slots.size());

        return Map.of(
            "status", "success",
            "timeSlotsCreated", slots.size()
        );
    }

    private List<TimeSlot> createTimeSlots() {
        List<TimeSlot> slots = new ArrayList<>();
        Instant now = Instant.now().truncatedTo(ChronoUnit.HOURS).plus(1, ChronoUnit.HOURS);

        // Create slots for English at various levels
        slots.addAll(createSlotsForLanguage("en", now));

        // Create slots for French
        slots.addAll(createSlotsForLanguage("fr", now));

        // Create slots for Spanish
        slots.addAll(createSlotsForLanguage("es", now));

        return slots;
    }

    private List<TimeSlot> createSlotsForLanguage(String language, Instant baseTime) {
        List<TimeSlot> slots = new ArrayList<>();
        TimeSlot.Level[] levels = {TimeSlot.Level.A1, TimeSlot.Level.A2, TimeSlot.Level.B1, TimeSlot.Level.B2};
        int[] durations = {15, 30, 45};

        for (int day = 0; day < 7; day++) {
            Instant dayStart = baseTime.plus(day, ChronoUnit.DAYS);

            // Morning sessions (9 AM, 10 AM, 11 AM)
            for (int hour = 9; hour <= 11; hour++) {
                Instant slotTime = dayStart.plus(hour - baseTime.atZone(java.time.ZoneOffset.UTC).getHour(), ChronoUnit.HOURS);
                TimeSlot.Level level = levels[hour % levels.length];
                int duration = durations[(day + hour) % durations.length];

                TimeSlot slot = TimeSlot.builder()
                    .targetLanguageCode(language)
                    .level(level)
                    .startTime(slotTime)
                    .durationMinutes(duration)
                    .maxParticipants(8)
                    .minParticipants(2)
                    .recurrence(TimeSlot.Recurrence.once)
                    .isActive(true)
                    .build();

                slots.add(timeSlotRepository.save(slot));
            }

            // Evening sessions (6 PM, 7 PM, 8 PM)
            for (int hour = 18; hour <= 20; hour++) {
                Instant slotTime = dayStart.plus(hour - baseTime.atZone(java.time.ZoneOffset.UTC).getHour(), ChronoUnit.HOURS);
                TimeSlot.Level level = levels[(hour + 1) % levels.length];
                int duration = durations[(day + hour) % durations.length];

                TimeSlot slot = TimeSlot.builder()
                    .targetLanguageCode(language)
                    .level(level)
                    .startTime(slotTime)
                    .durationMinutes(duration)
                    .maxParticipants(6)
                    .minParticipants(2)
                    .recurrence(TimeSlot.Recurrence.once)
                    .isActive(true)
                    .build();

                slots.add(timeSlotRepository.save(slot));
            }
        }

        return slots;
    }
}
