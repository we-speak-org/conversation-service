package org.wespeak.conversation.exception;

import lombok.Getter;

@Getter
public class RegistrationException extends RuntimeException {
    private final String code;

    public RegistrationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static RegistrationException slotFull() {
        return new RegistrationException("SLOT_FULL", "This time slot is already full");
    }

    public static RegistrationException maxRegistrations(int max) {
        return new RegistrationException("MAX_REGISTRATIONS", 
            String.format("You can only have %d active registrations at a time", max));
    }

    public static RegistrationException alreadyRegistered() {
        return new RegistrationException("ALREADY_REGISTERED", "You are already registered for this time slot");
    }

    public static RegistrationException registrationClosed() {
        return new RegistrationException("REGISTRATION_CLOSED", 
            "Registration is closed for this time slot (starts in less than 5 minutes)");
    }

    public static RegistrationException cancellationDeadlinePassed() {
        return new RegistrationException("CANCELLATION_DEADLINE_PASSED", 
            "You cannot cancel your registration less than 15 minutes before the start");
    }

    public static RegistrationException notRegistered() {
        return new RegistrationException("NOT_REGISTERED", "You are not registered for this time slot");
    }
}
