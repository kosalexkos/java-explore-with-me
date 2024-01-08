package ru.practicum.event.service;

import ru.practicum.event.controller.EventController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.EventState;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.update_request.EventUpdateAdminRequest;
import ru.practicum.event.dto.update_request.EventUpdateUserRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto create(Integer userId, EventNewDto eventNewDto);

    EventFullDto patchByAdmin(Integer eventId, EventUpdateAdminRequest eventUpdateAdminRequest);

    EventFullDto patchByInitiator(Integer userId, Integer eventId, EventUpdateUserRequest updateEventUserRequest);

    EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(Integer userId,
                                                                         Integer eventId,
                                                                         EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<EventFullDto> getAllByAdmin(List<Integer> users,
                                     List<EventState> states,
                                     List<Integer> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from,
                                     int size);

    List<EventShortDto> getAllByInitiator(Integer userId, int from, int size);

    EventFullDto getByIdByInitiator(Integer userId, Integer eventId);

    List<ParticipationRequestDto> getParticipationRequestsByInitiator(Integer userId, Integer eventId);


    List<EventShortDto> getAllPublic(String text,
                                     List<Integer> categories,
                                     Boolean paid,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     boolean onlyAvailable,
                                     EventController.SortMode sort,
                                     int from,
                                     int size,
                                     HttpServletRequest request);

    EventFullDto getByIdPublic(Integer eventId, HttpServletRequest request);
}
