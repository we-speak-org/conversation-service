package org.wespeak.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.conversation.dto.*;
import org.wespeak.conversation.entity.TimeSlot;
import org.wespeak.conversation.service.TimeSlotService;

@RestController
@RequestMapping("/api/v1/conversations/timeslots")
@RequiredArgsConstructor
@Tag(name = "TimeSlots", description = "Time slot management endpoints")
public class TimeSlotController {

  private final TimeSlotService timeSlotService;

  @GetMapping
  @Operation(summary = "List time slots", description = "Get available time slots with filters")
  public ResponseEntity<TimeSlotsResponse> getTimeSlots(
      @Parameter(description = "Language code filter") @RequestParam(required = false)
          String language,
      @Parameter(description = "CEFR level filter") @RequestParam(required = false)
          TimeSlot.Level level,
      @Parameter(description = "Start date filter") @RequestParam(required = false)
          Instant fromDate,
      @Parameter(description = "End date filter") @RequestParam(required = false) Instant toDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    TimeSlotsResponse response =
        timeSlotService.findTimeSlots(language, level, fromDate, toDate, page, size);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get time slot", description = "Get a time slot by ID")
  public ResponseEntity<TimeSlotDto> getTimeSlot(@PathVariable String id) {
    TimeSlotDto response = timeSlotService.findById(id);
    return ResponseEntity.ok(response);
  }

  @PostMapping
  @Operation(summary = "Create time slot", description = "Create a new time slot (admin only)")
  public ResponseEntity<TimeSlotDto> createTimeSlot(
      @Valid @RequestBody CreateTimeSlotRequest request) {
    TimeSlotDto response = timeSlotService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Update time slot", description = "Update a time slot (admin only)")
  public ResponseEntity<TimeSlotDto> updateTimeSlot(
      @PathVariable String id, @Valid @RequestBody CreateTimeSlotRequest request) {
    TimeSlotDto response = timeSlotService.update(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete time slot",
      description = "Delete (deactivate) a time slot (admin only)")
  public ResponseEntity<Void> deleteTimeSlot(@PathVariable String id) {
    timeSlotService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
