package org.wespeak.conversation.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    log.warn("Resource not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(RegistrationException.class)
  public ResponseEntity<ErrorResponse> handleRegistration(RegistrationException ex) {
    log.warn("Registration error: {}", ex.getMessage());
    HttpStatus status =
        switch (ex.getCode()) {
          case "SLOT_FULL", "MAX_REGISTRATIONS" -> HttpStatus.CONFLICT;
          case "ALREADY_REGISTERED" -> HttpStatus.CONFLICT;
          case "REGISTRATION_CLOSED", "CANCELLATION_DEADLINE_PASSED" -> HttpStatus.FORBIDDEN;
          case "NOT_REGISTERED" -> HttpStatus.NOT_FOUND;
          default -> HttpStatus.BAD_REQUEST;
        };
    return ResponseEntity.status(status).body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(SessionException.class)
  public ResponseEntity<ErrorResponse> handleSession(SessionException ex) {
    log.warn("Session error: {}", ex.getMessage());
    HttpStatus status =
        switch (ex.getCode()) {
          case "NOT_REGISTERED" -> HttpStatus.FORBIDDEN;
          case "ALREADY_IN_SESSION" -> HttpStatus.CONFLICT;
          case "SESSION_NOT_ACTIVE", "SESSION_ENDED" -> HttpStatus.GONE;
          case "SESSION_FULL", "JOIN_WINDOW_CLOSED" -> HttpStatus.FORBIDDEN;
          case "NO_ACTIVE_SESSION" -> HttpStatus.NOT_FOUND;
          default -> HttpStatus.BAD_REQUEST;
        };
    return ResponseEntity.status(status).body(ErrorResponse.of(ex.getCode(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    Map<String, Object> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of("VALIDATION_ERROR", "Invalid request body", errors));
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ErrorResponse> handleMissingParam(
      MissingServletRequestParameterException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ErrorResponse.of("MISSING_PARAMETER", ex.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
    log.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred"));
  }
}
