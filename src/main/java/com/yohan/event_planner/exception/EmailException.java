package com.yohan.event_planner.exception;

/**
 * Exception thrown for email-related validation errors such as duplicates,
 * invalid format, or invalid length.
 * Implements {@link HasErrorCode} to associate an error code with the exception.
 */
public class EmailException extends RuntimeException implements HasErrorCode {

    private final ErrorCode errorCode;

    /**
     * Constructs a new EmailException with a specific error code and the
     * email string related to the error.
     *
     * @param errorCode the specific {@link ErrorCode} representing the email error
     * @param email the email string related to the error, used to build the message
     * @throws IllegalArgumentException if email is null
     */
    public EmailException(ErrorCode errorCode, String email) {
        super(buildMessage(errorCode, email));
        if (email == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        this.errorCode = errorCode;
    }

    /**
     * Builds a consistent error message based on the error code and email.
     *
     * @param errorCode the error code
     * @param email the email string to include in the message
     * @return the constructed error message
     */
    private static String buildMessage(ErrorCode errorCode, String email) {
        return switch (errorCode) {
            case DUPLICATE_EMAIL -> "The email '" + email + "' is already registered.";
            case INVALID_EMAIL_LENGTH -> "The email '" + email + "' does not meet the length requirements.";
            case INVALID_EMAIL_FORMAT -> "The email '" + email + "' has an invalid format.";
            default -> "Email error with code: " + errorCode.name();
        };
    }

    /**
     * Returns the {@link ErrorCode} associated with this email exception.
     *
     * @return the error code indicating the email error type
     */
    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
