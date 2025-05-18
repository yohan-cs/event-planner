package com.yohan.event_planner.exception;

import java.time.LocalDate;

/**
 * Exception thrown when a Day entity is not found by ID or date.
 * Associates the error with {@link ErrorCode#DAY_NOT_FOUND}.
 */
public class DayNotFoundException extends ResourceNotFoundException implements HasErrorCode {

    private final ErrorCode errorCode;

    /**
     * Constructs a new {@code DayNotFoundException} for a missing Day by its ID.
     *
     * @param id the Day ID that was not found
     */
    public DayNotFoundException(Long id) {
        super("Day with ID " + id + " not found");
        this.errorCode = ErrorCode.DAY_NOT_FOUND;
    }

    /**
     * Constructs a new {@code DayNotFoundException} for a missing Day by its date.
     *
     * @param date the {@link LocalDate} representing the Day that was not found
     */
    public DayNotFoundException(LocalDate date) {
        super("Day with date " + date + " not found");
        this.errorCode = ErrorCode.DAY_NOT_FOUND;
    }

    /**
     * Returns the {@link ErrorCode} associated with this exception.
     *
     * @return the {@code DAY_NOT_FOUND} error code
     */
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
