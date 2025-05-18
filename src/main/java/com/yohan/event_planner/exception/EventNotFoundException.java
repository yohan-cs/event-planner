package com.yohan.event_planner.exception;

/**
 * Exception thrown when an Event with a specified ID is not found in the system.
 * Extends {@link ResourceNotFoundException}.
 */
public class EventNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs a new EventNotFoundException with a detailed message including the missing event ID.
     *
     * @param id the ID of the event that was not found
     */
    public EventNotFoundException(Long id) {
        super("Event with ID " + id + " not found");
    }
}
