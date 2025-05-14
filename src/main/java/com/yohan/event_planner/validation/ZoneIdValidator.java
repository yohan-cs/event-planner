package com.yohan.event_planner.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.ZoneId;

/**
 * Custom validator to check if a given ZoneId is valid.
 */
public class ZoneIdValidator implements ConstraintValidator<com.yohan.event_planner.validation.ValidZoneId, ZoneId> {

    @Override
    public void initialize(com.yohan.event_planner.validation.ValidZoneId constraintAnnotation) {
        // Initialization logic, if needed
    }

    @Override
    public boolean isValid(ZoneId zoneId, ConstraintValidatorContext context) {
        if (zoneId == null) {
            return true; // Assume null is allowed, handle it with @NotNull separately
        }

        try {
            // Check if the ZoneId is valid
            ZoneId.of(zoneId.getId());
            return true; // Valid ZoneId
        } catch (Exception e) {
            return false; // Invalid ZoneId
        }
    }
}