package ru.practicum.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PROTECTED)
public class UserShortDto {
    Integer id;
    String name;
}
