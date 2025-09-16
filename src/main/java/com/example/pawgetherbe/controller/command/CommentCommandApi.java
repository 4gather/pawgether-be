package com.example.pawgetherbe.controller.command;

import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateResponse;
import com.example.pawgetherbe.usecase.comment.RegistryCommentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentCommandApi {

    private final RegistryCommentUseCase registryCommentUseCase;

    @PostMapping("/{petfairId}")
    public CommentCreateResponse commentCreate(@PathVariable long petfairId, @RequestBody CommentCreateRequest request) {
        return registryCommentUseCase.createComment(petfairId, request);
    }

    @PatchMapping("/{petfairId}/{commentId}")
    public CommentUpdateResponse commentUpdate(@PathVariable long petfairId, @PathVariable long commentId, @RequestBody CommentUpdateRequest request) {
        return registryCommentUseCase.updateComment(petfairId, commentId, request);
    }
}
