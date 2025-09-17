package com.example.pawgetherbe.controller.command;

import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateResponse;
import com.example.pawgetherbe.usecase.comment.DeleteCommentUseCase;
import com.example.pawgetherbe.usecase.comment.EditCommentUseCase;
import com.example.pawgetherbe.usecase.comment.RegistryCommentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentCommandApi {

    private final RegistryCommentUseCase registryCommentUseCase;
    private final EditCommentUseCase editCommentUseCase;
    private final DeleteCommentUseCase deleteCommentUseCase;

    @PostMapping("/{petfairId}")
    public CommentCreateResponse commentCreate(@PathVariable long petfairId, @RequestBody CommentCreateRequest request) {
        return registryCommentUseCase.createComment(petfairId, request);
    }

    @PatchMapping("/{petfairId}/{commentId}")
    public CommentUpdateResponse commentUpdate(@PathVariable long petfairId, @PathVariable long commentId, @RequestBody CommentUpdateRequest request) {
        return editCommentUseCase.updateComment(petfairId, commentId, request);
    }

    @DeleteMapping("/{petfairId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable long petfairId, @PathVariable long commentId) {
        deleteCommentUseCase.deleteComment(petfairId, commentId);
    }
}
