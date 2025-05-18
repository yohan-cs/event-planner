package com.yohan.event_planner.exception;

/**
 * Exception thrown when a username is already taken.
 */
public class DuplicateUsernameException extends RuntimeException {

    /**
     * Constructs a new DuplicateUsernameException with a detailed message.
     * @param username the duplicate username causing the exception
     */
    public DuplicateUsernameException(String username) {
        super("User with username " + username + " already exists");
    }
}
