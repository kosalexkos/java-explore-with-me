package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.event.location.LocationDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.dateConstraints.DateConstants;

import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Integer id;
    UserShortDto initiator;
    CategoryDto category;
    LocationDto location;
    String title;
    String annotation;
    String description;
    EventState state;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    LocalDateTime eventDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    LocalDateTime createdOn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    LocalDateTime publishedOn;
    Integer participantLimit;
    Boolean paid;
    Boolean requestModeration;
    Integer confirmedRequests;
    Long views;
}
