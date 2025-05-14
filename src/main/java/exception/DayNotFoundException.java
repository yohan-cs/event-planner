package exception;

import java.time.LocalDate;

public class DayNotFoundException extends RuntimeException {

    public DayNotFoundException(Long id) {
        super("Day with ID " + id + " not found");
    }

    public DayNotFoundException(LocalDate date) {
        super("Day with ID " + date + " not found");
    }
}