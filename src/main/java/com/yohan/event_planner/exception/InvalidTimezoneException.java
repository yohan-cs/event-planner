package com.yohan.event_planner.exception;

import java.time.ZoneId;

public class InvalidTimezoneException extends RuntimeException {

    public InvalidTimezoneException(ZoneId startZone, ZoneId endZone) {
        super("Start time and end time must have the same timezone: " +
                "Start timezone (" + startZone + ") and end timezone (" + endZone + ").");
    }
}
