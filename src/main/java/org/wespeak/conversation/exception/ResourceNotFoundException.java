package org.wespeak.conversation.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException {
  private final String code;
  private final String resourceType;
  private final String resourceId;

  public ResourceNotFoundException(String code, String resourceType, String resourceId) {
    super(String.format("%s not found: %s", resourceType, resourceId));
    this.code = code;
    this.resourceType = resourceType;
    this.resourceId = resourceId;
  }

  public static ResourceNotFoundException timeSlotNotFound(String id) {
    return new ResourceNotFoundException("TIMESLOT_NOT_FOUND", "TimeSlot", id);
  }

  public static ResourceNotFoundException sessionNotFound(String id) {
    return new ResourceNotFoundException("SESSION_NOT_FOUND", "Session", id);
  }

  public static ResourceNotFoundException registrationNotFound(String id) {
    return new ResourceNotFoundException("REGISTRATION_NOT_FOUND", "Registration", id);
  }
}
