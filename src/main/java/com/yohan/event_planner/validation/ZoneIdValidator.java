package com.yohan.event_planner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.ZoneId;

/**
 * Validator implementation for the {@link ValidZoneId} annotation.
 *
 * This validator checks whether a given {@link String} represents a valid time zone ID
 * as defined by {@link java.time.ZoneId}.
 *
 * <p>
 * Validation logic:
 * <ul>
 *   <li>If the input string is {@code null} or blank, this validator returns {@code true},
 *       allowing other annotations like {@link jakarta.validation.constraints.NotBlank}
 *       to handle null or empty checks separately.</li>
 *   <li>If the input string is non-null and non-blank, it attempts to parse the string
 *       to a {@link ZoneId} using {@link ZoneId#of(String)}. If parsing succeeds,
 *       the value is considered valid.</li>
 *   <li>If parsing fails, the value is invalid.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This validator is intended to be used on {@code String} fields or parameters
 * representing time zone IDs.
 * </p>
 */
public class ZoneIdValidator implements ConstraintValidator<ValidZoneId, String> {

    /**
     * Initializes the validator in preparation for {@link #isValid} calls.
     * This implementation performs no initialization.
     *
     * @param constraintAnnotation the annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidZoneId constraintAnnotation) {
        // No initialization needed
    }

    /**
     * Validates that the provided string is a valid time zone ID.
     *
     * @param zoneIdStr the time zone ID string to validate; may be {@code null} or blank
     * @param context the context in which the constraint is evaluated (not used here)
     * @return {@code true} if the input is {@code null} or blank,
     *         or if it is a valid time zone ID; {@code false} otherwise
     */
    @Override
    public boolean isValid(String zoneIdStr, ConstraintValidatorContext context) {
        if (zoneIdStr == null || zoneIdStr.isBlank()) {
            // Null or blank is considered valid here; use @NotBlank or @NotNull to enforce non-null
            return true;
        }

        try {
            ZoneId.of(zoneIdStr);
            return true;
        } catch (Exception e) {
            // Invalid time zone ID string
            return false;
        }
    }
}
