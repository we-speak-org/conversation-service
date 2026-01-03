package org.wespeak.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.wespeak.conversation.entity.Registration;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {
    private String id;
    private String timeSlotId;
    private TimeSlotDto timeSlot;
    private Registration.Status status;
    private Instant registeredAt;
}
