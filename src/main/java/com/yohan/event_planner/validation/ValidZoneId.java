package com.yohan.event_planner.validation;

import com.yohan.event_planner.validation.ZoneIdValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to validate a valid ZoneId.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ZoneIdValidator.class) // The validator class to use
public @interface ValidZoneId {

    // Default error message when validation fails
    String message() default "Invalid ZoneId";

    // Allows specifying validation groups
    Class<?>[] groups() default {};

    // Additional data that can be attached to the annotation, used for custom error handling
    Class<? extends Payload>[] payload() default {};
}