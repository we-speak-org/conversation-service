package org.wespeak.lesson.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.lesson.dto.ProgressHistoryDto;
import org.wespeak.lesson.dto.UnlockedLessonsDto;
import org.wespeak.lesson.dto.UserProgressDto;
import org.wespeak.lesson.service.ProgressService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Tag(name = "Progress", description = "User progress endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class ProgressController {

    private final ProgressService progressService;

    @GetMapping
    @Operation(summary = "Get progress", description = "Get user progress for a language")
    public ResponseEntity<UserProgressDto> getProgress(
            @Parameter(description = "Language code", required = true)
            @RequestParam String language,
            Principal principal) {
        
        String userId = getUserId(principal);
        UserProgressDto response = progressService.getProgress(userId, language);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    @Operation(summary = "Get history", description = "Get completion history for a user")
    public ResponseEntity<ProgressHistoryDto> getHistory(
            @Parameter(description = "Language code", required = true)
            @RequestParam String language,
            @Parameter(description = "Limit (max 50)")
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "Offset for pagination")
            @RequestParam(defaultValue = "0") int offset,
            Principal principal) {
        
        String userId = getUserId(principal);
        limit = Math.min(limit, 50); // Max 50
        ProgressHistoryDto response = progressService.getHistory(userId, language, limit, offset);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unlocked-lessons")
    @Operation(summary = "Get unlocked lessons", description = "Get all unlocked lessons for a user")
    public ResponseEntity<UnlockedLessonsDto> getUnlockedLessons(
            @Parameter(description = "Language code", required = true)
            @RequestParam String language,
            Principal principal) {
        
        String userId = getUserId(principal);
        UnlockedLessonsDto response = progressService.getUnlockedLessons(userId, language);
        return ResponseEntity.ok(response);
    }

    private String getUserId(Principal principal) {
        if (principal == null) {
            return "dev-user-001";
        }
        return principal.getName();
    }
}
