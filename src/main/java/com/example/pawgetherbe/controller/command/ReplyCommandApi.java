package com.example.pawgetherbe.controller.command;

import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateRequest;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateResponse;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateRequest;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateResponse;
import com.example.pawgetherbe.usecase.reply.DeleteReplyUseCase;
import com.example.pawgetherbe.usecase.reply.EditReplyUseCase;
import com.example.pawgetherbe.usecase.reply.RegistryReplyUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/replies")
@RequiredArgsConstructor
public class ReplyCommandApi {

    private final RegistryReplyUseCase registryReplyUseCase;
    private final EditReplyUseCase editReplyUseCase;
    private final DeleteReplyUseCase deleteReplyUseCase;

    @PostMapping
    public ReplyCreateResponse replyCreate(@RequestBody @Validated ReplyCreateRequest request) {
        return registryReplyUseCase.replyCreate(request);
    }

    @PatchMapping
    public ReplyUpdateResponse replyUpdate(@RequestBody @Validated ReplyUpdateRequest request) {
        return editReplyUseCase.updateReply(request);
    }

    @DeleteMapping("/{commentId}/{replyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void replyDelete(@PathVariable long replyId, @PathVariable long commentId) {
        deleteReplyUseCase.deleteReply(commentId, replyId);
    }
}
