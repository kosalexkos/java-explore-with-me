package ru.practicum.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestState;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "participation_requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipationRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participation_request_id")
    Integer id;

    @ManyToOne
    @JoinColumn(name = "participation_user_id")
    User requester;

    @ManyToOne
    @JoinColumn(name = "participation_event_id")
    Event event;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ParticipationRequestState status;

    @Column(name = "created_date")
    LocalDateTime created;
}
