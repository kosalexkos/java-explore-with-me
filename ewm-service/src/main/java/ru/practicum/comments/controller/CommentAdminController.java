package ru.practicum.comments.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.dto.CommentDto;
import ru.practicum.comments.dto.UpdatedCommentDto;
import ru.practicum.comments.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;
    private final String path = "/{commentId}";

    @PatchMapping(path)
    public CommentDto patch(@PathVariable Integer commentId,
                            @Valid @RequestBody UpdatedCommentDto commentUpdate) {
        return commentService.patchCommentByAdmin(commentId, commentUpdate);
    }

    @DeleteMapping(path)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}