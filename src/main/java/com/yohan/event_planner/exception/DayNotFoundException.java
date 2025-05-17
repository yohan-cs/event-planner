package com.yohan.event_planner.exception;

import java.time.LocalDate;

/**
 * Exception thrown when a Day entity is not found by ID or date.
 */
public class DayNotFoundException extends ResourceNotFoundException {

    /**
     * Constructs exception when Day not found by its ID.
     * @param id the Day ID that was not found
     */
    public DayNotFoundException(Long id) {
        super("Day with ID " + id + " not found");
    }

    /**
     * Constructs exception when Day not found by a specific date.
     * @param date the LocalDate representing the Day not found
     */
    public DayNotFoundException(LocalDate date) {
        super("Day with date " + date + " not found");
    }
}
