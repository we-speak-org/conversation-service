package org.wespeak.conversation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wespeak.conversation.service.SeedService;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/conversations/seed")
@RequiredArgsConstructor
@Tag(name = "Seed", description = "Database seeding endpoints (dev only)")
public class SeedController {

    private final SeedService seedService;

    @PostMapping
    @Operation(summary = "Seed database", description = "Clear and seed the database with test data")
    public ResponseEntity<Map<String, Object>> seedDatabase() {
        log.info("Seed database request received");
        Map<String, Object> result = seedService.seedDatabase();
        return ResponseEntity.ok(result);
    }
}
