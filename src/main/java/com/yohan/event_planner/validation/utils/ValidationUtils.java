package com.yohan.event_planner.validation.utils;

import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * Utility class providing common validation methods
 * to be used across the application for input sanity checks.
 */
public final class ValidationUtils {

    private ValidationUtils() {
        // Prevent instantiation
    }

    /**
     * Validates that the given ID is not null and greater than zero.
     * @param id the ID to validate
     * @param name the name of the ID parameter (for error messages)
     * @throws IllegalArgumentException if the ID is null or <= 0
     */
    public static void requireValidId(Long id, String name) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(name + " must be a positive, non-null ID");
        }
    }


    /**
     * Validates that the given object is not null.
     * @param obj the object to check
     * @param name the name of the parameter for error messages
     * @param <T> the type of the object
     * @return the non-null object
     * @throws IllegalArgumentException if obj is null
     */
    public static <T> T requireNonNull(T obj, String name) {
        if (obj == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
        return obj;
    }

    /**
     * Validates that the given ZonedDateTime is not null.
     * This method is a specialized null check for ZonedDateTime objects,
     * providing clearer intent and error messages compared to a generic null check.
     *
     * @param dateTime the ZonedDateTime to check
     * @param name the name of the parameter for error messages
     * @throws IllegalArgumentException if dateTime is null
     */
    public static void requireNonNullDateTime(ZonedDateTime dateTime, String name) {
        if (dateTime == null) {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }

    /**
     * Validates that the given collection is not null or empty.
     * @param collection the collection to check
     * @param name the name of the parameter for error messages
     * @param <T> type of elements in collection
     * @throws IllegalArgumentException if collection is null or empty
     */
    public static <T> void requireNonEmptyCollection(Collection<T> collection, String name) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be null or empty");
        }
    }
}
