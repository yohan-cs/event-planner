package com.yohan.event_planner.util;

import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.model.User;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public final class TestConstants {

    private TestConstants() {
        // Utility class, prevent instantiation
    }

    // ---------- User IDs ----------
    public static final Long USER_ID_1 = 1L;
    public static final Long USER_ID_2 = 2L;
    public static final Long USER_ID_3 = 3L;

    // ---------- Event IDs ----------
    public static final Long EVENT_ID_1 = 100L;
    public static final Long EVENT_ID_2 = 101L;

    // ---------- Event Names ----------
    public static final String EVENT_WORKOUT = "Workout";
    public static final String EVENT_STUDY = "Study";
    public static final String EVENT_UPDATED = "Updated Study";
    public static final String EVENT_GENERIC = "Anything";

    // ---------- Dates/Times ----------
    public static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    public static final ZonedDateTime MAY_20_2025_9AM = ZonedDateTime.of(
            2025, 5, 20, 9, 0, 0, 0, UTC_ZONE);

    public static final ZonedDateTime MAY_20_2025_11AM = ZonedDateTime.of(
            2025, 5, 20, 11, 0, 0, 0, UTC_ZONE);

    public static final ZonedDateTime MAY_20_2025_1PM = ZonedDateTime.of(
            2025, 5, 20, 13, 0, 0, 0, UTC_ZONE);

    public static final ZonedDateTime MAY_20_2025_2PM = ZonedDateTime.of(
            2025, 5, 20, 14, 0, 0, 0, UTC_ZONE);

    public static final ZonedDateTime MAY_20_2025_10AM = ZonedDateTime.of(
            2025, 5, 20, 10, 0, 0, 0, UTC_ZONE);

    public static final ZonedDateTime MAY_20_2025_NOON = ZonedDateTime.of(
            2025, 5, 20, 12, 0, 0, 0, UTC_ZONE);

    // ---------- Event DTOs ----------

    public static final EventCreateDTO VALID_EVENT_CREATE_DTO = new EventCreateDTO(
            EVENT_WORKOUT,
            MAY_20_2025_9AM,
            MAY_20_2025_11AM,
            "Morning workout session"
    );

    public static final EventUpdateDTO VALID_EVENT_UPDATE_DTO = new EventUpdateDTO(
            EVENT_UPDATED,
            "Updated description",
            MAY_20_2025_1PM,
            MAY_20_2025_2PM
    );

    public static final User TEST_USER = new User(
            "dummy", "dummypassword", "dummy@email.com",
            ZoneId.of("UTC"), "Dummy", "Smith"
    );



}
