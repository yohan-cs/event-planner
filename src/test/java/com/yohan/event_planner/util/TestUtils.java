package com.yohan.event_planner.util;

import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.domain.Day;
import com.yohan.event_planner.domain.Event;
import com.yohan.event_planner.domain.User;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TestUtils {

    public static void setId(Object obj, Long id) {
        try {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(obj, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set id via reflection", e);
        }
    }

    // ---------- Factory Methods ----------

    public static User createUserWithId(Long id) {
        User user = new User();
        setId(user, id);
        return user;
    }

    public static Event createEventWithId(Long id, String name, ZonedDateTime start, ZonedDateTime end, User creator) {
        Event event = new Event(name, start, end, creator);
        setId(event, id);
        return event;
    }

    public static Day createDayWithDate(LocalDate date, User user) {
        return new Day(date, user);
    }

    public static ZonedDateTime zdt(int year, int month, int day, int hour, int minute) {
        return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneOffset.UTC);
    }

    public static EventCreateDTO createEventCreateDTO(String name, ZonedDateTime start, ZonedDateTime end, String description, String timezone) {
        return new EventCreateDTO(name, start, end, description);
    }

    public static EventUpdateDTO createEventUpdateDTO(String name, String description, ZonedDateTime start, ZonedDateTime end) {
        return new EventUpdateDTO(name, description, start, end);
    }

    // ---------- Relationship Helpers ----------

    public static void linkEventAndDay(Event event, Day day) {
        event.addDay(day);
    }
}
