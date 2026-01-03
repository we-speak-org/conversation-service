package org.wespeak.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.conversation.dto.RegistrationDto;
import org.wespeak.conversation.dto.RegistrationsResponse;
import org.wespeak.conversation.service.RegistrationService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
@Tag(name = "Registrations", description = "Registration management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/timeslots/{timeSlotId}/register")
    @Operation(summary = "Register for time slot", description = "Register the current user for a time slot")
    public ResponseEntity<RegistrationDto> register(
            @PathVariable String timeSlotId,
            Principal principal) {
        String userId = getUserId(principal);
        RegistrationDto response = registrationService.register(timeSlotId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/timeslots/{timeSlotId}/register")
    @Operation(summary = "Cancel registration", description = "Cancel the current user's registration")
    public ResponseEntity<Void> unregister(
            @PathVariable String timeSlotId,
            Principal principal) {
        String userId = getUserId(principal);
        registrationService.unregister(timeSlotId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/registrations")
    @Operation(summary = "Get my registrations", description = "Get all registrations for the current user")
    public ResponseEntity<RegistrationsResponse> getMyRegistrations(Principal principal) {
        String userId = getUserId(principal);
        RegistrationsResponse response = registrationService.getUserRegistrations(userId);
        return ResponseEntity.ok(response);
    }

    private String getUserId(Principal principal) {
        if (principal == null) {
            return "dev-user-001";
        }
        return principal.getName();
    }
}
