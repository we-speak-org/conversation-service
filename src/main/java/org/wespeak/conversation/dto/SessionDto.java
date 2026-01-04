package org.wespeak.conversation.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wespeak.conversation.entity.Session;
import org.wespeak.conversation.entity.TimeSlot;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
  private String id;
  private String timeSlotId;
  private String targetLanguageCode;
  private TimeSlot.Level level;
  private Session.Status status;
  private Instant startedAt;
  private Instant endedAt;
  private Boolean recordingEnabled;
  private List<ParticipantDto> participants;
}
