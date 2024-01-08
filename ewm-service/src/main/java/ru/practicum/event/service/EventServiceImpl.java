package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.controller.EventController;
import ru.practicum.event.dto.*;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateRequest;
import ru.practicum.event.dto.update_request.EventRequestStatusUpdateResult;
import ru.practicum.event.dto.update_request.EventUpdateAdminRequest;
import ru.practicum.event.dto.update_request.EventUpdateUserRequest;
import ru.practicum.event.location.Location;
import ru.practicum.event.location.LocationDto;
import ru.practicum.event.location.LocationMapper;
import ru.practicum.event.location.LocationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.ParticipationRequestMapper;
import ru.practicum.request.dto.ParticipationRequestState;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.ParticipationRequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.dateConstraints.DateConstants;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    @Autowired
    private final EventRepository eventRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoryRepository categoryRepository;
    @Autowired
    private final LocationRepository locationRepository;
    @Autowired
    private final ParticipationRequestRepository participationRepository;
    @Autowired
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto create(Integer userId, EventNewDto eventNewDto) {
        if (LocalDateTime.now().plusHours(2).isAfter(eventNewDto.getEventTimestamp())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        User user = findUserById(userId);
        Category category = findCategoryById(eventNewDto.getCategory());
        Location location = handleLocationDto(eventNewDto.getLocation());

        Event event = EventMapper.INSTANCE.fromDto(eventNewDto, category, location);

        event.setInitiator(user);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);

        if (eventNewDto.getPaid() == null) {
            event.setPaid(false);
        }

        if (eventNewDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (eventNewDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        return EventMapper.INSTANCE.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto patchByAdmin(Integer eventId, EventUpdateAdminRequest updateEventAdminRequest) {
        Event event = findEventById(eventId);

        if (updateEventAdminRequest.getEventTimestamp() != null && LocalDateTime.now().plusHours(1)
                .isAfter(updateEventAdminRequest.getEventTimestamp())) {
            throw new ConflictException("The event date must be 1 hours from the current time or later.");
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(EventUpdateAdminRequest.StateAction.PUBLISH_EVENT) &&
                    !event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Cannot publish the event because it's not in the right state: "
                        + event.getState());
            }

            if (updateEventAdminRequest.getStateAction().equals(EventUpdateAdminRequest.StateAction.REJECT_EVENT) &&
                    event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException("Cannot reject the event because it's not in the right state: "
                        + event.getState());
            }
        }

        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventAdminRequest.getCategory()));
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(handleLocationDto(updateEventAdminRequest.getLocation()));
        }

        Optional.ofNullable(updateEventAdminRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventAdminRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventAdminRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventAdminRequest.getEventTimestamp()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventAdminRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventAdminRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventAdminRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        return EventMapper.INSTANCE.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto patchByInitiator(Integer userId, Integer eventId, EventUpdateUserRequest updateEventUserRequest) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        if (updateEventUserRequest.getEventTimestamp() != null && LocalDateTime.now().plusHours(2)
                .isAfter(updateEventUserRequest.getEventTimestamp())) {
            throw new ConflictException("The event date must be 2 hours from the current time or later.");
        }

        if (!(event.getState().equals(EventState.CANCELED) ||
                event.getState().equals(EventState.PENDING))) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(findCategoryById(updateEventUserRequest.getCategory()));
        }

        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(handleLocationDto(updateEventUserRequest.getLocation()));
        }

        Optional.ofNullable(updateEventUserRequest.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateEventUserRequest.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateEventUserRequest.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateEventUserRequest.getEventTimestamp()).ifPresent(event::setEventDate);
        Optional.ofNullable(updateEventUserRequest.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateEventUserRequest.getPaid()).ifPresent(event::setPaid);
        Optional.ofNullable(updateEventUserRequest.getRequestModeration()).ifPresent(event::setRequestModeration);

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        return EventMapper.INSTANCE.toFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchParticipationRequestsByInitiator(Integer userId,
                                                                                Integer eventId,
                                                                                EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        findUserById(userId);
        Event event = findEventById(eventId);

        long confirmLimit = event.getParticipantLimit() - participationRepository.countByEventIdAndStatus(eventId, ParticipationRequestState.CONFIRMED);

        if (confirmLimit <= 0) {
            throw new ConflictException("The participant limit has been reached");
        }

        List<ParticipationRequest> requestList = participationRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        List<Integer> notFoundIds = eventRequestStatusUpdateRequest.getRequestIds().stream()
                .filter(requestId -> requestList.stream().noneMatch(request -> request.getId().equals(requestId)))
                .collect(Collectors.toList());

        if (!notFoundIds.isEmpty()) {
            throw new NotFoundException("Participation request with id=" + notFoundIds + " was not found");
        }

        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();

        for (ParticipationRequest req : requestList) {
            if (!req.getEvent().getId().equals(eventId)) {
                throw new NotFoundException("Participation request with id=" + req.getId() + " was not found");
            }

            if (confirmLimit <= 0) {
                req.setStatus(ParticipationRequestState.REJECTED);
                result.getRejectedRequests().add(ParticipationRequestMapper.INSTANCE.toDto(req));
                continue;
            }

            switch (eventRequestStatusUpdateRequest.getStatus()) {
                case CONFIRMED:
                    req.setStatus(ParticipationRequestState.CONFIRMED);
                    result.getConfirmedRequests().add(ParticipationRequestMapper.INSTANCE.toDto(req));
                    confirmLimit--;
                    break;
                case REJECTED:
                    req.setStatus(ParticipationRequestState.REJECTED);
                    result.getRejectedRequests().add(ParticipationRequestMapper.INSTANCE.toDto(req));
                    break;
            }
        }

        participationRepository.saveAll(requestList);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByAdmin(List<Integer> users,
                                            List<EventState> states,
                                            List<Integer> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from,
                                            int size) {
        Pageable pageable = PageRequest.of(from, size);

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }

        if (rangeEnd == null) {
            rangeEnd = DateConstants.getMaxDT();
        }

        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);

        List<String> eventUrls = events.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStats> viewStats = statsClient.getStatistics(rangeStart.format(DateConstants.getDefaultFormatter()),
                rangeEnd.format(DateConstants.getDefaultFormatter()), eventUrls, true);

        return events.stream()
                .map(EventMapper.INSTANCE::toFullDto)
                .peek(dto -> {
                    Optional<ViewStats> matchingStats = viewStats.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStats::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllByInitiator(Integer userId, int from, int size) {
        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size)).stream()
                .map(EventMapper.INSTANCE::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdByInitiator(Integer userId, Integer eventId) {
        Event event = findEventById(eventId);
        checkInitiator(userId, eventId, event.getInitiator().getId());

        return EventMapper.INSTANCE.toFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getParticipationRequestsByInitiator(Integer userId, Integer eventId) {
        findUserById(userId);
        findEventById(eventId);

        return participationRepository.findAllByEventId(eventId).stream()
                .map(ParticipationRequestMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getAllPublic(String text,
                                            List<Integer> categories,
                                            Boolean paid,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            boolean onlyAvailable,
                                            EventController.SortMode sort,
                                            int from,
                                            int size,
                                            HttpServletRequest request) {
        if (categories != null && categories.size() == 1 && categories.get(0).equals(0)) {
            categories = null;
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Start date must be before end date");
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = DateConstants.getMaxDT();
        }

        statsClient.add(EndpointHitDto.builder()
                .app("ewm")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<Event> eventList = eventRepository.getAllPublic(text, categories, paid, rangeStart, rangeEnd);

        if (onlyAvailable) {
            eventList = eventList.stream()
                    .filter(event -> event.getParticipantLimit().equals(0) || event.getParticipantLimit()
                            < participationRepository.countByEventIdAndStatus(event.getId(), ParticipationRequestState.CONFIRMED))
                    .collect(Collectors.toList());
        }

        List<String> eventUrls = eventList.stream()
                .map(event -> "/events/" + event.getId())
                .collect(Collectors.toList());

        List<ViewStats> viewStatsDtos = statsClient.getStatistics(rangeStart.format(DateConstants.getDefaultFormatter()),
                rangeEnd.format(DateConstants.getDefaultFormatter()), eventUrls, true);

        List<EventShortDto> eventShortDtoList = eventList.stream()
                .map(EventMapper.INSTANCE::toShortDto)
                .peek(dto -> {
                    Optional<ViewStats> matchingStats = viewStatsDtos.stream()
                            .filter(statsDto -> statsDto.getUri().equals("/events/" + dto.getId()))
                            .findFirst();
                    dto.setViews(matchingStats.map(ViewStats::getHits).orElse(0L));
                })
                .peek(dto -> dto.setConfirmedRequests(participationRepository.countByEventIdAndStatus(dto.getId(),
                        ParticipationRequestState.CONFIRMED)))
                .collect(Collectors.toList());

        switch (sort) {
            case EVENT_DATE:
                eventShortDtoList.sort(Comparator.comparing(EventShortDto::getEventDate));
                break;
            case VIEWS:
                eventShortDtoList.sort(Comparator.comparing(EventShortDto::getViews).reversed());
                break;
        }

        if (from >= eventShortDtoList.size()) {
            return Collections.emptyList();
        }

        int toIndex = Math.min(from + size, eventShortDtoList.size());
        return eventShortDtoList.subList(from, toIndex);
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getByIdPublic(Integer eventId, HttpServletRequest request) {
        Event event = findEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        statsClient.add(EndpointHitDto.builder()
                .app("ewm")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<String> eventUrls = Collections.singletonList("/events/" + event.getId());

        List<ViewStats> viewStats = statsClient.getStatistics(DateConstants.getMinDT().format(DateConstants.getDefaultFormatter()),
                DateConstants.getMaxDT().plusYears(1).format(DateConstants.getDefaultFormatter()), eventUrls, true);

        EventFullDto dto = EventMapper.INSTANCE.toFullDto(event);
        dto.setViews(viewStats.isEmpty() ? 0 : viewStats.get(0).getHits());
        dto.setConfirmedRequests(participationRepository.countByEventIdAndStatus(dto.getId(), ParticipationRequestState.CONFIRMED));

        return dto;
    }

    private Event findEventById(Integer id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id + " was not found"));
    }

    private User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id=" + id + " was not found"));
    }

    private Category findCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category with id=" + id + " was not found"));
    }

    private void checkInitiator(Integer userId, Integer eventId, Integer initiatorId) {
        if (!userId.equals(initiatorId)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }
    }

    private Location handleLocationDto(LocationDto locationDto) {
        Location location = locationRepository.findByLatAndLon(locationDto.getLat(), locationDto.getLon());
        return location != null ? location : locationRepository.save(LocationMapper.INSTANCE.fromDto(locationDto));
    }
}
