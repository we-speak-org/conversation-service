package org.wespeak.template.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wespeak.template.CreateExampleRequest;
import org.wespeak.template.ExampleResponse;
import org.wespeak.template.service.ExampleService;

@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
@Tag(name = "Examples", description = "Example resource management (Replace with your domain)")
public class ExampleController {

  private final ExampleService service;

  @GetMapping
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Get all examples", description = "Retrieve list of all examples")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved list")
  public ResponseEntity<List<ExampleResponse>> findAll() {
    return ResponseEntity.ok(service.findAll());
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Get example by ID", description = "Retrieve a specific example by its ID")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    @ApiResponse(responseCode = "404", description = "Example not found")
  })
  public ResponseEntity<ExampleResponse> getById(@PathVariable String id) {
    return ResponseEntity.ok(service.getById(id));
  }

  @GetMapping("/code/{code}")
  @PreAuthorize("hasRole('USER')")
  @Operation(
      summary = "Get example by code",
      description = "Retrieve a specific example by its unique code")
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
    @ApiResponse(responseCode = "404", description = "Example not found")
  })
  public ResponseEntity<ExampleResponse> getByCode(@PathVariable String code) {
    return ResponseEntity.ok(service.getByCode(code));
  }

  @PostMapping
  @PreAuthorize("hasRole('USER')")
  @Operation(summary = "Create new example", description = "Create a new example resource")
  @ApiResponses({
    @ApiResponse(responseCode = "201", description = "Successfully created"),
    @ApiResponse(responseCode = "400", description = "Invalid request"),
    @ApiResponse(responseCode = "409", description = "Example already exists")
  })
  public ResponseEntity<ExampleResponse> create(@Valid @RequestBody CreateExampleRequest request) {
    ExampleResponse response = service.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(
      summary = "Delete example",
      description = "Delete an example by ID (requires ADMIN role)")
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "Successfully deleted"),
    @ApiResponse(responseCode = "404", description = "Example not found"),
    @ApiResponse(responseCode = "403", description = "Access denied")
  })
  public ResponseEntity<Void> delete(@PathVariable String id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
