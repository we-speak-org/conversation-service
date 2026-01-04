package org.wespeak.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaStateRequest {
  private Boolean cameraEnabled;
  private Boolean micEnabled;
}
