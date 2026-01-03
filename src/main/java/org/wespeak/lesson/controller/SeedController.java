package org.wespeak.lesson.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wespeak.lesson.service.ProgressService;
import org.wespeak.lesson.service.SeedService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/seed")
@RequiredArgsConstructor
@Tag(name = "Seed", description = "Database seeding endpoints (dev only)")
public class SeedController {

    private final SeedService seedService;
    private final ProgressService progressService;

    @PostMapping
    @Operation(summary = "Seed database", description = "Clear and seed the database with test data")
    public ResponseEntity<Map<String, Object>> seedDatabase() {
        log.info("Seed database request received");
        Map<String, Object> result = seedService.seedDatabase();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/progress/{userId}")
    @Operation(summary = "Initialize progress", description = "Initialize progress for a user and language")
    public ResponseEntity<Map<String, Object>> initializeProgress(
            @PathVariable String userId,
            @RequestParam String language) {
        log.info("Initialize progress for userId: {}, language: {}", userId, language);
        progressService.initializeProgress(userId, language);
        return ResponseEntity.ok(Map.of(
            "status", "success",
            "userId", userId,
            "language", language
        ));
    }
}
