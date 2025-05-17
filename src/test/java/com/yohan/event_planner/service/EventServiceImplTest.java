package com.yohan.event_planner.service;

import com.yohan.event_planner.business.EventBO;
import com.yohan.event_planner.dto.EventCreateDTO;
import com.yohan.event_planner.dto.EventResponseDTO;
import com.yohan.event_planner.dto.EventUpdateDTO;
import com.yohan.event_planner.exception.EventNotFoundException;
import com.yohan.event_planner.mapper.EventMapper;
import com.yohan.event_planner.model.Event;
import com.yohan.event_planner.model.User;
import com.yohan.event_planner.util.TestConstants;
import com.yohan.event_planner.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @Mock private EventBO eventBO;
    @Mock private EventMapper eventMapper;

    @InjectMocks private EventServiceImpl eventService;

    private User testUser;
    private Event testEvent;
    private ZoneId systemZone;

    @BeforeEach
    void setUp() {
        testUser = TestUtils.createUserWithId(TestConstants.USER_ID_1);
        testEvent = TestUtils.createEventWithId(
                TestConstants.EVENT_ID_1,
                TestConstants.EVENT_WORKOUT,
                TestConstants.MAY_20_2025_9AM,
                TestConstants.MAY_20_2025_11AM,
                testUser);
        systemZone = ZoneId.systemDefault();
    }

    @Test
    void getById_existingEvent_returnsDto() {
        when(eventBO.getById(TestConstants.EVENT_ID_1)).thenReturn(Optional.of(testEvent));
        when(eventMapper.toDto(testEvent, systemZone)).thenReturn(
                new EventResponseDTO(testEvent.getId(), testEvent.getName(), testEvent.getStartTime(), testEvent.getEndTime(), testUser.getId(), List.of()));

        EventResponseDTO dto = eventService.getById(TestConstants.EVENT_ID_1);

        assertNotNull(dto);
        assertEquals(testEvent.getId(), dto.id());
        verify(eventBO).getById(TestConstants.EVENT_ID_1);
        verify(eventMapper).toDto(testEvent, systemZone);
    }

    @Test
    void getById_nonExistingEvent_throwsException() {
        when(eventBO.getById(TestConstants.EVENT_ID_1)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.getById(TestConstants.EVENT_ID_1));

        verify(eventBO).getById(TestConstants.EVENT_ID_1);
        verify(eventMapper, never()).toDto(any(), any());
    }

    @Test
    void getByUserId_returnsDtoList() {
        List<Event> events = List.of(testEvent);
        when(eventBO.getByCreatorId(TestConstants.USER_ID_1)).thenReturn(events);
        when(eventMapper.toDtoList(events, systemZone)).thenReturn(List.of(
                new EventResponseDTO(testEvent.getId(), testEvent.getName(), testEvent.getStartTime(), testEvent.getEndTime(), testUser.getId(), List.of())
        ));

        List<EventResponseDTO> dtos = eventService.getByUserId(TestConstants.USER_ID_1);

        assertEquals(1, dtos.size());
        verify(eventBO).getByCreatorId(TestConstants.USER_ID_1);
        verify(eventMapper).toDtoList(events, systemZone);
    }

    @Test
    void getByDayId_returnsDtoList() {
        List<Event> events = List.of(testEvent);
        when(eventBO.getByDayId(10L)).thenReturn(events);
        when(eventMapper.toDtoList(events, systemZone)).thenReturn(List.of(
                new EventResponseDTO(testEvent.getId(), testEvent.getName(), testEvent.getStartTime(), testEvent.getEndTime(), testUser.getId(), List.of())
        ));

        List<EventResponseDTO> dtos = eventService.getByDayId(10L);

        assertEquals(1, dtos.size());
        verify(eventBO).getByDayId(10L);
        verify(eventMapper).toDtoList(events, systemZone);
    }

    @Test
    void getByDate_returnsDtoList() {
        LocalDate date = LocalDate.of(2025, 5, 20);
        List<Event> events = List.of(testEvent);
        when(eventBO.getEventsByDate(date, systemZone)).thenReturn(events);
        when(eventMapper.toDtoList(events, systemZone)).thenReturn(List.of(
                new EventResponseDTO(testEvent.getId(), testEvent.getName(), testEvent.getStartTime(), testEvent.getEndTime(), testUser.getId(), List.of())
        ));

        List<EventResponseDTO> dtos = eventService.getByDate(date);

        assertEquals(1, dtos.size());
        verify(eventBO).getEventsByDate(date, systemZone);
        verify(eventMapper).toDtoList(events, systemZone);
    }

    @Test
    void createEvent_success_returnsDto() {
        EventCreateDTO createDTO = TestConstants.VALID_EVENT_CREATE_DTO;
        when(eventBO.createEvent(createDTO, testUser)).thenReturn(testEvent);
        when(eventMapper.toDto(testEvent, createDTO.startTime().getZone())).thenReturn(
                new EventResponseDTO(testEvent.getId(), testEvent.getName(), testEvent.getStartTime(), testEvent.getEndTime(), testUser.getId(), List.of())
        );

        EventResponseDTO dto = eventService.createEvent(createDTO, testUser);

        assertNotNull(dto);
        assertEquals(testEvent.getId(), dto.id());
        verify(eventBO).createEvent(createDTO, testUser);
        verify(eventMapper).toDto(testEvent, createDTO.startTime().getZone());
    }

    @Test
    void updateEvent_existingEvent_returnsUpdatedDto() {
        EventUpdateDTO updateDTO = TestConstants.VALID_EVENT_UPDATE_DTO;

        when(eventBO.getById(TestConstants.EVENT_ID_1)).thenReturn(Optional.of(testEvent));
        doNothing().when(eventMapper).updateEntity(testEvent, updateDTO);
        when(eventBO.updateEvent(TestConstants.EVENT_ID_1, updateDTO)).thenReturn(testEvent);
        when(eventMapper.toDto(testEvent, systemZone)).thenReturn(
                new EventResponseDTO(testEvent.getId(), testEvent.getName(), testEvent.getStartTime(), testEvent.getEndTime(), testUser.getId(), List.of())
        );

        EventResponseDTO updatedDto = eventService.updateEvent(TestConstants.EVENT_ID_1, updateDTO);

        assertNotNull(updatedDto);
        assertEquals(testEvent.getId(), updatedDto.id());

        verify(eventBO).getById(TestConstants.EVENT_ID_1);
        verify(eventMapper).updateEntity(testEvent, updateDTO);
        verify(eventBO).updateEvent(TestConstants.EVENT_ID_1, updateDTO);
        verify(eventMapper).toDto(testEvent, systemZone);
    }

    @Test
    void updateEvent_nonExistingEvent_throwsException() {
        EventUpdateDTO updateDTO = TestConstants.VALID_EVENT_UPDATE_DTO;

        when(eventBO.getById(TestConstants.EVENT_ID_1)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.updateEvent(TestConstants.EVENT_ID_1, updateDTO));

        verify(eventBO).getById(TestConstants.EVENT_ID_1);
        verify(eventMapper, never()).updateEntity(any(), any());
        verify(eventBO, never()).updateEvent(anyLong(), any());
        verify(eventMapper, never()).toDto(any(), any());
    }

    @Test
    void saveEvent_delegatesToBO() {
        when(eventBO.save(testEvent)).thenReturn(testEvent);

        Event result = eventService.saveEvent(testEvent);

        assertSame(testEvent, result);
        verify(eventBO).save(testEvent);
    }

    @Test
    void deleteById_existing_deletesSuccessfully() {
        when(eventBO.getById(TestConstants.EVENT_ID_1)).thenReturn(Optional.of(testEvent));
        doNothing().when(eventBO).deleteById(TestConstants.EVENT_ID_1);

        assertDoesNotThrow(() -> eventService.deleteById(TestConstants.EVENT_ID_1));

        verify(eventBO).getById(TestConstants.EVENT_ID_1);
        verify(eventBO).deleteById(TestConstants.EVENT_ID_1);
    }

    @Test
    void deleteById_nonExisting_throwsException() {
        when(eventBO.getById(TestConstants.EVENT_ID_1)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> eventService.deleteById(TestConstants.EVENT_ID_1));

        verify(eventBO).getById(TestConstants.EVENT_ID_1);
        verify(eventBO, never()).deleteById(anyLong());
    }
}
