package model;

import jakarta.persistence.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ZonedDateTime date;  // Store date as ZonedDateTime in UTC

    private boolean archived;  // Flag for archiving the day (if needed)

    @ManyToMany(mappedBy = "days", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private Set<Event> events = new HashSet<>();

    public Day() {
    }

    public Day(ZonedDateTime date) {
        // Convert the date to UTC for consistent storage
        this.date = date.withZoneSameInstant(ZoneId.of("UTC"));
        this.archived = false;  // By default, days are not archived
    }

    public Long getId() {
        return id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date.withZoneSameInstant(ZoneId.of("UTC"));  // Ensure UTC time zone
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Set<Event> getEvents() {
        return events;
    }

    public void setEvents(Set<Event> events) {
        this.events = events;
    }

    // Utility method to add an event to the day
    public void addEvent(Event event) {
        events.add(event);
        event.getDays().add(this);
    }

    // Utility method to remove an event from the day
    public void removeEvent(Event event) {
        events.remove(event);
        event.getDays().remove(this);
    }
}
