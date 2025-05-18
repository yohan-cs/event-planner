package com.yohan.event_planner.exception;

/**
 * Exception thrown for password-related validation errors such as weakness,
 * invalid format, or length violations.
 * Implements {@link HasErrorCode} to associate an error code with the exception.
 */
public class PasswordException extends RuntimeException implements HasErrorCode {

    private final ErrorCode errorCode;

    /**
     * Constructs a new PasswordException with a specific error code and the
     * raw password string related to the error.
     *
     * @param errorCode the specific {@link ErrorCode} representing the password error
     * @throws IllegalArgumentException if password is null
     */
    public PasswordException(ErrorCode errorCode) {
        super(buildMessage(errorCode));
        this.errorCode = errorCode;
    }

    /**
     * Builds a consistent error message based on the error code and password.
     *
     * @param errorCode the error code
     * @return the constructed error message
     */
    private static String buildMessage(ErrorCode errorCode) {
        return switch (errorCode) {
            case WEAK_PASSWORD -> "The password does not meet strength requirements.";
            case INVALID_PASSWORD_LENGTH -> "The password must meet length requirements.";
            case NULL_PASSWORD -> "Password is required.";
            default -> "Password error with code: " + errorCode.name();
        };
    }

    /**
     * Returns the {@link ErrorCode} associated with this password exception.
     *
     * @return the error code indicating the password error type
     */
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
