package org.wespeak.conversation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CreateTimeSlotRequest {
  @NotNull private String targetLanguageCode;

  @NotNull private TimeSlot.Level level;

  @NotNull private Instant startTime;

  @NotNull
  @Min(15)
  @Max(45)
  private Integer durationMinutes;

  @Min(2)
  @Max(8)
  private Integer maxParticipants;

  private TimeSlot.Recurrence recurrence;
}
