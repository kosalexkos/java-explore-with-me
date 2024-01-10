package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdatedCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
public class CommentPrivateController {
    private final CommentService commentService;
    private final String path = "/{commentId}";
    private final String path2 = "/{eventId}";

    @PostMapping(path2)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto create(@PathVariable Integer userId,
                             @PathVariable Integer eventId,
                             @Valid @RequestBody NewCommentDto commentNewDto) {
        return commentService.createComment(userId, eventId, commentNewDto);
    }

    @PatchMapping(path)
    public CommentDto patch(@PathVariable Integer userId,
                            @PathVariable Integer commentId,
                            @Valid @RequestBody UpdatedCommentDto commentUpdate) {
        return commentService.patchCommentByUser(userId, commentId, commentUpdate);
    }

    @DeleteMapping(path)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer userId, @PathVariable Integer commentId) {
        commentService.deleteCommentByUser(userId, commentId);
    }
}
