package org.wespeak.template.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Health", description = "Health and status endpoints")
public class HealthController {

  private final Optional<BuildProperties> buildProperties;

  @GetMapping("/health")
  @Operation(summary = "Get service health status")
  public ResponseEntity<Map<String, Object>> health() {
    return ResponseEntity.ok(
        Map.of(
            "status", "UP",
            "service", buildProperties.map(BuildProperties::getName).orElse("template-service"),
            "version", buildProperties.map(BuildProperties::getVersion).orElse("dev"),
            "timestamp", Instant.now()));
  }

  @GetMapping("/info")
  @Operation(summary = "Get service information")
  public ResponseEntity<Map<String, Object>> info() {
    return ResponseEntity.ok(
        Map.of(
            "name", buildProperties.map(BuildProperties::getName).orElse("template-service"),
            "version", buildProperties.map(BuildProperties::getVersion).orElse("dev"),
            "buildTime", buildProperties.map(BuildProperties::getTime).orElse(Instant.now()),
            "description", "WeSpeak Microservice Template"));
  }
}
