package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.update_request.EventUpdateUserRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventNewDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {
    private final EventService eventService;
    private final String path = "/{eventId}";
    private final String path1 = "/{eventId}/requests";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable Integer userId,
                               @Valid @RequestBody EventNewDto eventNewDto) {
        return eventService.create(userId, eventNewDto);
    }

    @PatchMapping(path)
    public EventFullDto patchEventInfo(@PathVariable Integer userId,
                                       @PathVariable Integer eventId,
                                       @Valid @RequestBody EventUpdateUserRequest updateEventUserRequest) {
        return eventService.patchByInitiator(userId, eventId, updateEventUserRequest);
    }

    @PatchMapping(path1)
    public EventRequestStatusUpdateResult patchEventRequests(@PathVariable Integer userId,
                                                             @PathVariable Integer eventId,
                                                             @Valid @RequestBody EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return eventService.patchParticipationRequestsByInitiator(userId, eventId, eventRequestStatusUpdateRequest);
    }

    @GetMapping(path)
    public EventFullDto getById(@PathVariable Integer userId,
                                @PathVariable Integer eventId) {
        return eventService.getByIdByInitiator(userId, eventId);
    }

    @GetMapping(path1)
    public List<ParticipationRequestDto> getParticipationByInitiator(@PathVariable Integer userId,
                                                                     @PathVariable Integer eventId) {
        return eventService.getParticipationRequestsByInitiator(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getAll(@PathVariable Integer userId,
                                      @Valid @RequestParam(defaultValue = "0") @Min(0) int from,
                                      @Valid @RequestParam(defaultValue = "10") @Min(1) int size) {
        return eventService.getAllByInitiator(userId, from, size);
    }
}
