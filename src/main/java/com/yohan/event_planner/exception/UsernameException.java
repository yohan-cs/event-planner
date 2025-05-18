package com.yohan.event_planner.exception;

/**
 * Exception thrown for username-related errors such as duplicates or invalid length.
 * Associates each error with a specific {@link ErrorCode} and generates a meaningful error message.
 */
public class UsernameException extends RuntimeException implements HasErrorCode {

    private final ErrorCode errorCode;

    /**
     * Constructs a new {@code UsernameException} with a specific error code and username context.
     *
     * @param errorCode the specific {@link ErrorCode} representing the username error
     * @param username the username involved in the error condition
     */
    public UsernameException(ErrorCode errorCode, String username) {
        super(buildMessage(errorCode, username));
        this.errorCode = errorCode;
    }

    /**
     * Returns the {@link ErrorCode} associated with this username exception.
     *
     * @return the error code indicating the specific username-related error
     */
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }

    /**
     * Builds a detailed error message based on the provided error code and username context.
     *
     * @param errorCode the {@link ErrorCode} describing the error
     * @param username the username involved in the error condition
     * @return a human-readable message describing the username error
     */
    private static String buildMessage(ErrorCode errorCode, String username) {
        return switch (errorCode) {
            case DUPLICATE_USERNAME -> "User with username '" + username + "' already exists";
            case INVALID_USERNAME_LENGTH -> "The username '" + username + "' does not meet the length requirements.";
            default -> "An unknown username error occurred";
        };
    }
}
