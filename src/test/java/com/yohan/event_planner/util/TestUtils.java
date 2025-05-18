package com.yohan.event_planner.util;

import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.domain.Day;
import com.yohan.event_planner.domain.Event;
import com.yohan.event_planner.domain.User;
import com.yohan.event_planner.domain.PasswordVO;  // <-- make sure to import PasswordVO

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class TestUtils {

    // ---------- Reflection Helpers ----------

    /**
     * Sets the 'id' field of any object, even if it's private or defined in a superclass.
     *
     * @param obj the object instance
     * @param id  the id value to assign
     */
    public static void setId(Object obj, Long id) {
        setPrivateField(obj, "id", id);
    }

    /**
     * Sets the value of a private field using reflection.
     *
     * @param obj       the object instance
     * @param fieldName the field to set
     * @param value     the value to assign
     */
    public static void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            Field field = getFieldFromHierarchy(obj.getClass(), fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set field '" + fieldName + "' via reflection", e);
        }
    }

    /**
     * Reads the value of a private field using reflection.
     *
     * @param obj       the object instance
     * @param fieldName the name of the private field
     * @param fieldType the expected type of the field
     * @param <T>       the field's return type
     * @return the value of the field, cast to T
     */
    public static <T> T getPrivateField(Object obj, String fieldName, Class<T> fieldType) {
        try {
            Field field = getFieldFromHierarchy(obj.getClass(), fieldName);
            field.setAccessible(true);
            return fieldType.cast(field.get(obj));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to get field '" + fieldName + "' via reflection", e);
        }
    }

    /**
     * Recursively searches for a field in the class hierarchy.
     *
     * @param clazz     the class to start from
     * @param fieldName the field name
     * @return the Field object
     * @throws NoSuchFieldException if the field is not found
     */
    private static Field getFieldFromHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // move up
            }
        }
        throw new NoSuchFieldException("Field '" + fieldName + "' not found in class hierarchy");
    }

    // ---------- Factory Methods ----------

    public static User createUserWithId(Long id) {
        User user = new User();
        setId(user, id);
        return user;
    }

    /**
     * Creates a User instance with a valid PasswordVO set, useful for tests that require a non-null password.
     * Uses a dummy password "dummyPassword123!".
     *
     * @param username the username to set
     * @return a new User instance with PasswordVO set
     */
    public static User createUserWithPassword(String username) {
        PasswordVO passwordVO = new PasswordVO("dummyPassword123!");
        User user = new User(username, passwordVO, username + "@example.com",
                java.time.ZoneId.of("UTC"), "First", "Last");
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
