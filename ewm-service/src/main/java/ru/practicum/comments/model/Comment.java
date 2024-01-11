package ru.practicum.comments.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    Integer id;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    User user;
    @ManyToOne
    @JoinColumn(name = "event_id")
    Event event;
    @Column(name = "text")
    String text;
    @Column(name = "timestamp")
    LocalDateTime createdOn;
}
