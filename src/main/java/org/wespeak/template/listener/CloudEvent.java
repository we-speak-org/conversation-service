package org.wespeak.template.listener;

import java.time.Instant;

/**
 * CloudEvent wrapper for incoming Kafka messages following CloudEvents specification. This is a
 * generic DTO used by all event listeners.
 *
 * @param <T> The type of the event payload
 */
public record CloudEvent<T>(String id, String source, String type, Instant time, T data) {}
