package org.wespeak.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.conversation.dto.*;
import org.wespeak.conversation.service.SessionService;

@RestController
@RequestMapping("/api/v1/conversations/sessions")
@RequiredArgsConstructor
@Tag(name = "Sessions", description = "Session management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class SessionController {

  private final SessionService sessionService;

  @PostMapping("/join")
  @Operation(summary = "Join session", description = "Join a session for a time slot")
  public ResponseEntity<SessionDto> joinSession(
      @Valid @RequestBody JoinSessionRequest request, Principal principal) {
    String userId = getUserId(principal);
    SessionDto response = sessionService.joinSession(userId, request);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/current")
  @Operation(summary = "Get current session", description = "Get the user's current active session")
  public ResponseEntity<SessionDto> getCurrentSession(Principal principal) {
    String userId = getUserId(principal);
    SessionDto response = sessionService.getCurrentSession(userId);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/current/media")
  @Operation(summary = "Update media state", description = "Update camera/microphone state")
  public ResponseEntity<ParticipantDto> updateMediaState(
      @Valid @RequestBody MediaStateRequest request, Principal principal) {
    String userId = getUserId(principal);
    ParticipantDto response = sessionService.updateMediaState(userId, request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/current/leave")
  @Operation(summary = "Leave session", description = "Leave the current session")
  public ResponseEntity<Void> leaveSession(Principal principal) {
    String userId = getUserId(principal);
    sessionService.leaveSession(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/history")
  @Operation(summary = "Get session history", description = "Get user's session history")
  public ResponseEntity<SessionHistoryResponse> getSessionHistory(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      Principal principal) {
    String userId = getUserId(principal);
    SessionHistoryResponse response = sessionService.getSessionHistory(userId, page, size);
    return ResponseEntity.ok(response);
  }

  private String getUserId(Principal principal) {
    if (principal == null) {
      return "dev-user-001";
    }
    return principal.getName();
  }
}
