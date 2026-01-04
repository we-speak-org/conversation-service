package org.wespeak.conversation.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wespeak.conversation.entity.Participant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantDto {
  private String id;
  private String userId;
  private String displayName;
  private Participant.Status status;
  private Boolean cameraEnabled;
  private Boolean micEnabled;
  private Boolean recordingConsent;
  private Instant joinedAt;
}
