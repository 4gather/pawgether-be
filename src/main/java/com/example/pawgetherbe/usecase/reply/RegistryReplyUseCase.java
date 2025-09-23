package com.example.pawgetherbe.usecase.reply;

import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateRequest;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateResponse;

public interface RegistryReplyUseCase {
    ReplyCreateResponse replyCreate(ReplyCreateRequest request);
}
