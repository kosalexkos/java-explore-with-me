package ru.practicum.event.dto.update_request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    @NotNull
    List<Integer> requestIds;
    @NotNull
    EventRequestStatusUpdateRequest.StateAction status;

    public enum StateAction {
        CONFIRMED,
        REJECTED
    }
}
