package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.CommentMapper;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdatedCommentDto;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.HttpForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    public CommentDto createComment(Integer userId, Integer eventId, NewCommentDto dto) {
        User u = findUserById(userId);
        Event e = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%s doesn't exist", eventId)));
        return CommentMapper.INSTANCE.toDto(commentRepository.save(
                Comment.builder()
                        .user(u)
                        .event(e)
                        .text(dto.getText())
                        .createdOn(LocalDateTime.now())
                        .build()));
    }

    @Transactional
    public CommentDto patchCommentByUser(Integer userId, Integer commentId, UpdatedCommentDto updateRequest) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new HttpForbiddenException(
                    String.format("User id=%s is not allowed to update the comment with id=%s", userId, commentId));
        }
        Optional.ofNullable(updateRequest.getText()).ifPresent(comment::setText);
        return CommentMapper.INSTANCE.toDto(comment);
    }

    @Transactional
    public CommentDto patchCommentByAdmin(Integer commentId, UpdatedCommentDto updateRequest) {
        Comment comment = findCommentById(commentId);
        Optional.ofNullable(updateRequest.getText()).ifPresent(comment::setText);

        return CommentMapper.INSTANCE.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsByEventId(Integer eventId) {
        return commentRepository.findAllByEventId(eventId).stream()
                .map(CommentMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCommentByUser(Integer userId, Integer commentId) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);
        if (!comment.getUser().getId().equals(userId)) {
            throw new HttpForbiddenException(
                    String.format("User id=%s not allowed to delete comment with the id=%s", userId, commentId));
        }
        commentRepository.deleteById(commentId);
    }

    @Transactional
    public void deleteCommentByAdmin(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment findCommentById(Integer id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Comment with id=%s not found", id)));
    }

    private User findUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("User with id=%s not found", id)));
    }
}
