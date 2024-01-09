package ru.practicum.event.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventState;
import ru.practicum.event.location.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    Integer id;
    @ManyToOne
    @JoinColumn(name = "initiator_user_id")
    User initiator;
    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;
    @ManyToOne
    @JoinColumn(name = "location_id")
    Location location;
    @Column(name = "title")
    String title;
    @Column(name = "annotation")
    String annotation;
    @Column(name = "description")
    String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "state")
    EventState state;
    @Column(name = "date")
    LocalDateTime eventDate;
    @Column(name = "create_date")
    LocalDateTime createdOn;
    @Column(name = "publish_date")
    LocalDateTime publishedOn;
    @Column(name = "participant_limit")
    Integer participantLimit;
    @Column(name = "is_paid")
    Boolean paid;
    @Column(name = "is_request_moderation")
    Boolean requestModeration;
}
