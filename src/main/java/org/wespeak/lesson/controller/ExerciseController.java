package org.wespeak.lesson.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.lesson.dto.ExerciseSubmissionResultDto;
import org.wespeak.lesson.dto.SubmitAnswerRequest;
import org.wespeak.lesson.service.ExerciseService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Exercise submission endpoints")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping("/lessons/{lessonId}/exercises/{exerciseId}/submit")
    @Operation(summary = "Submit answer", description = "Submit an answer for an exercise")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ExerciseSubmissionResultDto> submitAnswer(
            @PathVariable String lessonId,
            @PathVariable String exerciseId,
            @Valid @RequestBody SubmitAnswerRequest request,
            Principal principal) {
        
        String userId = getUserId(principal);
        ExerciseSubmissionResultDto response = exerciseService.submitAnswer(
            exerciseId, lessonId, userId, request);
        return ResponseEntity.ok(response);
    }

    // Alternative route: POST /exercises/{id}/submit (for compatibility)
    @PostMapping("/exercises/{exerciseId}/submit")
    @Operation(summary = "Submit answer (alternative)", description = "Submit an answer for an exercise")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<ExerciseSubmissionResultDto> submitAnswerAlt(
            @PathVariable String exerciseId,
            @RequestParam String lessonId,
            @Valid @RequestBody SubmitAnswerRequest request,
            Principal principal) {
        
        String userId = getUserId(principal);
        ExerciseSubmissionResultDto response = exerciseService.submitAnswer(
            exerciseId, lessonId, userId, request);
        return ResponseEntity.ok(response);
    }

    private String getUserId(Principal principal) {
        if (principal == null) {
            return "dev-user-001";
        }
        return principal.getName();
    }
}
