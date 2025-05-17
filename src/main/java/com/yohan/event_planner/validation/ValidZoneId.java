package com.yohan.event_planner.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom validation annotation to ensure a field or parameter represents a valid {@link java.time.ZoneId}.
 * <p>
 * This annotation can be applied to fields or method parameters to enforce that
 * the value corresponds to a valid time zone identifier.
 * <p>
 * It is backed by the {@link ZoneIdValidator} class which contains the validation logic.
 */
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ZoneIdValidator.class) // Specifies the validator implementation
public @interface ValidZoneId {

    /**
     * The default error message returned when validation fails.
     *
     * @return the error message string
     */
    String message() default "Invalid ZoneId";

    /**
     * Allows the specification of validation groups, to selectively apply validation constraints.
     *
     * @return array of validation group classes
     */
    Class<?>[] groups() default {};

    /**
     * Can be used by clients of the Bean Validation API to assign custom payload objects to a constraint.
     *
     * @return array of payload classes
     */
    Class<? extends Payload>[] payload() default {};
}
