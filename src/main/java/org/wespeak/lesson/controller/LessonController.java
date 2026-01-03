package org.wespeak.lesson.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.lesson.dto.*;
import org.wespeak.lesson.service.ExerciseService;
import org.wespeak.lesson.service.LessonService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/lessons")
@RequiredArgsConstructor
@Tag(name = "Lessons", description = "Lesson management endpoints")
public class LessonController {

    private final LessonService lessonService;
    private final ExerciseService exerciseService;

    @GetMapping("/{lessonId}")
    @Operation(summary = "Get lesson details", description = "Get a lesson with its exercises (without answers)")
    public ResponseEntity<LessonDetailDto> getLesson(
            @PathVariable String lessonId,
            Principal principal) {
        
        String userId = principal != null ? principal.getName() : null;
        LessonDetailDto response = lessonService.findLessonById(lessonId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{lessonId}/start")
    @Operation(summary = "Start a lesson", description = "Start a lesson session")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<LessonSessionDto> startLesson(
            @PathVariable String lessonId,
            Principal principal) {
        
        String userId = getUserId(principal);
        
        // Reset exercise attempts for this lesson
        exerciseService.resetAttempts(userId, lessonId);
        
        LessonSessionDto response = lessonService.startLesson(lessonId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{lessonId}/complete")
    @Operation(summary = "Complete a lesson", description = "Complete a lesson and record the score")
    @SecurityRequirement(name = "bearer-jwt")
    public ResponseEntity<LessonCompletionResultDto> completeLesson(
            @PathVariable String lessonId,
            @Valid @RequestBody CompleteLessonRequest request,
            Principal principal) {
        
        String userId = getUserId(principal);
        LessonCompletionResultDto response = lessonService.completeLesson(lessonId, userId, request);
        return ResponseEntity.ok(response);
    }

    private String getUserId(Principal principal) {
        // In dev mode without security, use a default user ID
        if (principal == null) {
            return "dev-user-001";
        }
        return principal.getName();
    }
}
