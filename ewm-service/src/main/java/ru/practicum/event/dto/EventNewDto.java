package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.location.LocationDto;
import ru.practicum.dateConstraints.DateConstants;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventNewDto {
    @NotNull
    Integer category;
    @NotNull
    LocationDto location;
    @NotBlank
    @Size(min = 3, max = 120)
    String title;
    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;
    @NotBlank
    @Size(min = 20, max = 7000)
    String description;
    @NotNull
    @Future
    @JsonProperty("eventDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateConstants.DATE_FORMAT)
    LocalDateTime eventTimestamp;
    Integer participantLimit;
    Boolean paid;
    Boolean requestModeration;

}
