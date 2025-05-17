package com.yohan.event_planner.exception;

/**
 * Represents a structured error response sent back to the client
 * when an exception is thrown within the application.
 *
 * This record holds:
 * - HTTP status code
 * - Human-readable error message
 * - Timestamp of when the error occurred
 *
 * It's typically returned from a centralized exception handler.
 */
public record ErrorResponse(int status, String message, long timeStamp) {
}