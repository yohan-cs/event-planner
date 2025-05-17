package com.yohan.event_planner.exception;

/**
 * Exception thrown when an email is already registered in the system.
 */
public class DuplicateEmailException extends RuntimeException {

    /**
     * Constructs a new DuplicateEmailException with a detailed message.
     * @param email the duplicate email address causing the exception
     */
    public DuplicateEmailException(String email) {
        super("The email " + email + " is already registered");
    }
}
