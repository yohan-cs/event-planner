import com.yohan.event_planner.model.Day;
import com.yohan.event_planner.model.User;
import com.yohan.event_planner.repository.DayRepository;
import com.yohan.event_planner.service.DayServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DayServiceImplTest {

    @Mock
    private DayRepository dayRepository;

    @InjectMocks
    private DayServiceImpl dayService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();  // assuming default constructor or create a mock user as needed
    }

    @Test
    void getOrCreateAllDaysBetween_shouldSaveMissingDays_whenSomeMissing() {
        LocalDate start = LocalDate.of(2025, 5, 24);
        LocalDate end = LocalDate.of(2025, 5, 26);
        LocalDate missingDate = start.plusDays(1);

        Day existing = new Day(start, user);
        Day newDay = new Day(missingDate, user);

        // Use mutable list here
        when(dayRepository.findAllByDateInAndCreator(anySet(), eq(user))).thenReturn(new ArrayList<>(List.of(existing)));
        when(dayRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        Set<Day> result = dayService.getOrCreateAllDaysBetween(start, end, user);

        assertEquals(3, result.size());
        assertTrue(result.stream().anyMatch(d -> d.getDate().equals(missingDate)));
        verify(dayRepository).saveAll(any());
    }

    @Test
    void getDayById_shouldReturnDayWhenFound() {
        Day day = new Day(LocalDate.now(), user);
        when(dayRepository.findById(anyLong())).thenReturn(Optional.of(day));

        Optional<Day> result = dayService.getDayById(1L);

        assertTrue(result.isPresent());
        assertEquals(day, result.get());
    }

    @Test
    void getDayByDate_shouldReturnDayWhenFound() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        Day day = new Day(date, user);
        when(dayRepository.findByDateAndCreator(date, user)).thenReturn(Optional.of(day));

        Optional<Day> result = dayService.getDayByDate(date, user);

        assertTrue(result.isPresent());
        assertEquals(day, result.get());
    }

    @Test
    void getOrCreateDay_shouldReturnExistingDay_ifFound() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        Day existing = new Day(date, user);

        when(dayRepository.findByDateAndCreator(date, user)).thenReturn(Optional.of(existing));

        Day result = dayService.getOrCreateDay(date, user);

        assertEquals(existing, result);
        verify(dayRepository, never()).save(any());
    }

    @Test
    void getOrCreateDay_shouldSaveAndReturnNewDay_ifNotFound() {
        LocalDate date = LocalDate.of(2025, 5, 21);
        Day newDay = new Day(date, user);

        when(dayRepository.findByDateAndCreator(date, user)).thenReturn(Optional.empty());
        when(dayRepository.save(any(Day.class))).thenReturn(newDay);

        Day result = dayService.getOrCreateDay(date, user);

        assertEquals(newDay, result);
        verify(dayRepository).save(any(Day.class));
    }

    @Test
    void saveDay_shouldSaveAndReturnDay() {
        Day day = new Day(LocalDate.now(), user);

        when(dayRepository.save(day)).thenReturn(day);

        Day result = dayService.saveDay(day);

        assertEquals(day, result);
        verify(dayRepository).save(day);
    }

    @Test
    void saveAllDays_shouldSaveAndReturnDays() {
        List<Day> days = List.of(new Day(LocalDate.now(), user), new Day(LocalDate.now().plusDays(1), user));

        when(dayRepository.saveAll(days)).thenReturn(days);

        List<Day> result = dayService.saveAllDays(days);

        assertEquals(days, result);
        verify(dayRepository).saveAll(days);
    }

    @Test
    void deleteDay_shouldDelete_whenDayExists() {
        Long id = 1L;

        when(dayRepository.existsById(id)).thenReturn(true);

        assertDoesNotThrow(() -> dayService.deleteDay(id));
        verify(dayRepository).deleteById(id);
    }

    @Test
    void deleteDay_shouldThrowException_whenDayDoesNotExist() {
        Long id = 1L;

        when(dayRepository.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> dayService.deleteDay(id)); // Replace with your DayNotFoundException if available
        verify(dayRepository, never()).deleteById(anyLong());
    }
}
