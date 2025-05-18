package com.yohan.event_planner.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Centralized handler for exceptions thrown in REST controllers.
 * Converts exceptions into consistent {@link ErrorResponse} objects,
 * using HTTP status codes appropriate to the error,
 * and extracting error codes from exceptions implementing {@link HasErrorCode}.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles InvalidTimeException, typically thrown when event time validation fails.
     */
    @ExceptionHandler(InvalidTimeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTimeException(InvalidTimeException ex) {
        logger.warn("InvalidTimeException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    /**
     * Handles ConflictException, for scheduling conflicts such as overlapping events.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        logger.warn("ConflictException: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex);
    }

    /**
     * Handles username-related exceptions (duplicates, invalid length, etc.).
     * All username issues are represented by {@link UsernameException}.
     */
    @ExceptionHandler(UsernameException.class)
    public ResponseEntity<ErrorResponse> handleUsernameException(UsernameException ex) {
        logger.warn("UsernameException [{}]: {}", ex.getErrorCode(), ex.getMessage());
        // Map all username exceptions to HTTP 409 Conflict for duplicates, 400 Bad Request for others
        HttpStatus status = switch (ex.getErrorCode()) {
            case DUPLICATE_USERNAME -> HttpStatus.CONFLICT;
            case INVALID_USERNAME_LENGTH -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
        return buildErrorResponse(status, ex);
    }

    /**
     * Handles email-related exceptions (duplicates, invalid format, etc.).
     * All email issues are represented by {@link EmailException}.
     */
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponse> handleEmailException(EmailException ex) {
        logger.warn("EmailException [{}]: {}", ex.getErrorCode(), ex.getMessage());
        // Map all email exceptions to HTTP 409 Conflict for duplicates, 400 Bad Request for others
        HttpStatus status = switch (ex.getErrorCode()) {
            case DUPLICATE_EMAIL -> HttpStatus.CONFLICT;
            case INVALID_EMAIL_FORMAT -> HttpStatus.BAD_REQUEST;
            default -> HttpStatus.BAD_REQUEST;
        };
        return buildErrorResponse(status, ex);
    }

    /**
     * Handles all resource not found exceptions such as EventNotFoundException, UserNotFoundException, etc.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("ResourceNotFoundException [{}]: {}", (ex instanceof HasErrorCode h ? h.getErrorCode() : "NONE"), ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex);
    }

    /**
     * Handles validation errors from @Valid method arguments,
     * collecting all field error messages into a single concatenated string.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessages = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        logger.warn("Validation failed: {}", errorMessages);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessages, null);
    }

    /**
     * Catch-all handler for any unexpected, unhandled exceptions.
     * Logs the full stack trace and returns a generic error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled exception caught: ", ex);
        String message = "An unexpected error occurred. Please try again later.";
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    /**
     * Utility method to construct an {@link ErrorResponse} from exceptions implementing {@link HasErrorCode}.
     * Extracts the message and error code automatically.
     *
     * @param status the HTTP status to return
     * @param ex the exception thrown
     * @return a ResponseEntity containing the {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, Exception ex) {
        String message = ex.getMessage();
        String errorCode = (ex instanceof HasErrorCode codeEx) ? codeEx.getErrorCode().name() : null;
        return buildErrorResponse(status, message, errorCode);
    }

    /**
     * Utility method to construct an {@link ErrorResponse} from a plain message without an error code.
     *
     * @param status the HTTP status to return
     * @param message the error message to include
     * @return a ResponseEntity containing the {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message) {
        return buildErrorResponse(status, message, null);
    }

    /**
     * Core method to build the {@link ErrorResponse} with status code, message, optional error code, and timestamp.
     *
     * @param status the HTTP status to return
     * @param message the error message to include
     * @param errorCode the optional error code string
     * @return a ResponseEntity containing the {@link ErrorResponse}
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, String errorCode) {
        return new ResponseEntity<>(
                new ErrorResponse(status.value(), message, errorCode, System.currentTimeMillis()),
                status
        );
    }
}
