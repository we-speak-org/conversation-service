package org.wespeak.conversation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinSessionRequest {
    @NotNull
    private String timeSlotId;

    @Builder.Default
    private Boolean recordingConsent = false;

    private String displayName;
}
