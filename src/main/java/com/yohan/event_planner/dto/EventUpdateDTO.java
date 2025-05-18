package com.yohan.event_planner.dto;

import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;

/**
 * Data Transfer Object for partial updates (PATCH) to an existing {@link com.yohan.event_planner.model.Event}.
 *
 * All fields are optional; only non-null fields will be applied to update the target event.
 *
 * Validation constraints apply only if the respective fields are provided:
 *
 *     name: must be between 1 and 50 characters if present
 *     description: must be less than 255 characters if present
 *
 *
 * Timezone information is included within {@link ZonedDateTime} fields {@code startTime} and {@code endTime}.
 * The effective timezone of the event is managed internally based on the updated {@code startTime}.
 *
 * @param name        optional new name for the event
 * @param description optional new description for the event
 * @param startTime   optional new start time with timezone info
 * @param endTime     optional new end time with timezone info
 */
public record EventUpdateDTO(

        @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
        String name,

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description,

        ZonedDateTime startTime,

        ZonedDateTime endTime
) {}
