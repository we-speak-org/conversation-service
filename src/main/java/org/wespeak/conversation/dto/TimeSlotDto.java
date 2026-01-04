package org.wespeak.conversation.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wespeak.conversation.entity.TimeSlot;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeSlotDto {
  private String id;
  private String targetLanguageCode;
  private TimeSlot.Level level;
  private Instant startTime;
  private Instant endTime;
  private Integer durationMinutes;
  private Integer maxParticipants;
  private Integer registeredCount;
  private Integer availableSpots;
  private Boolean isAvailable;
}
