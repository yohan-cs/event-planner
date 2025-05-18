package com.yohan.event_planner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.ZoneId;

/**
 * Validator implementation for the {@link ValidZoneId} annotation.
 * <p>
 * This validator checks whether a given string represents a valid {@link ZoneId}.
 * It returns {@code true} if the input is {@code null} (null-checks should be handled separately),
 * or if the string can be parsed as a valid time zone ID.
 * Otherwise, it returns {@code false}, indicating a validation failure.
 */
public class ZoneIdValidator implements ConstraintValidator<ValidZoneId, String> {

    /**
     * Initializes the validator in preparation for {@link #isValid(String, ConstraintValidatorContext)} calls.
     * Not used in this implementation.
     *
     * @param constraintAnnotation the annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidZoneId constraintAnnotation) {
        // No initialization needed
    }

    /**
     * Checks if the provided string is a valid {@link ZoneId}.
     *
     * @param zoneId  the string to validate, may be {@code null}
     * @param context context in which the constraint is evaluated (not used here)
     * @return {@code true} if {@code zoneId} is {@code null} or a valid {@link ZoneId}, {@code false} otherwise
     */
    @Override
    public boolean isValid(String zoneId, ConstraintValidatorContext context) {
        if (zoneId == null) {
            // Null values are considered valid; use @NotNull to enforce non-null if needed
            return true;
        }

        try {
            // Attempt to parse the zoneId string as a valid ZoneId
            ZoneId.of(zoneId);
            return true;
        } catch (Exception e) {
            // Parsing failed, so the zoneId is invalid
            return false;
        }
    }
}
