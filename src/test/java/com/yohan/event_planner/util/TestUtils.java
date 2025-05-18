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
            Field idField = getFieldFromHierarchy(obj.getClass(), "id");
            idField.setAccessible(true);
            idField.set(obj, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set id via reflection", e);
        }
    }

    /**
     * Generic method to get a private field's value using reflection,
     * even if it's declared in a superclass.
     *
     * @param obj       the object instance
     * @param fieldName the name of the private field
     * @param fieldType the expected type of the field
     * @param <T>       generic type of the field
     * @return the field's value casted to the given type
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
     * Searches for a field in the class hierarchy.
     *
     * @param clazz     the class to start the search from
     * @param fieldName the name of the field
     * @return the Field object
     * @throws NoSuchFieldException if not found in any class in the hierarchy
     */
    private static Field getFieldFromHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        while (clazz != null) {
            try {
                return clazz.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass(); // move up the class hierarchy
            }
        }
        throw new NoSuchFieldException(fieldName);
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
