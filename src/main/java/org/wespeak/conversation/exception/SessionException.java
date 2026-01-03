package org.wespeak.conversation.exception;

import lombok.Getter;

@Getter
public class SessionException extends RuntimeException {
    private final String code;

    public SessionException(String code, String message) {
        super(message);
        this.code = code;
    }

    public static SessionException notRegistered() {
        return new SessionException("NOT_REGISTERED", "You are not registered for this time slot");
    }

    public static SessionException alreadyInSession() {
        return new SessionException("ALREADY_IN_SESSION", "You are already in an active session");
    }

    public static SessionException sessionNotActive() {
        return new SessionException("SESSION_NOT_ACTIVE", "This session is not active");
    }

    public static SessionException sessionEnded() {
        return new SessionException("SESSION_ENDED", "This session has already ended");
    }

    public static SessionException sessionFull() {
        return new SessionException("SESSION_FULL", "This session is already full");
    }

    public static SessionException joinWindowClosed() {
        return new SessionException("JOIN_WINDOW_CLOSED", 
            "The join window has closed (more than 5 minutes after start)");
    }

    public static SessionException noActiveSession() {
        return new SessionException("NO_ACTIVE_SESSION", "You don't have an active session");
    }
}
