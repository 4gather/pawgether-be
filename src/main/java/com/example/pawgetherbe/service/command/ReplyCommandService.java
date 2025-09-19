package com.example.pawgetherbe.service.command;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateResponse;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateRequest;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateRequest;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateResponse;
import com.example.pawgetherbe.domain.status.ReplyStatus;
import com.example.pawgetherbe.mapper.command.ReplyCommandMapper;
import com.example.pawgetherbe.repository.command.CommentCommandRepository;
import com.example.pawgetherbe.repository.command.ReplyCommandRepository;
import com.example.pawgetherbe.repository.command.UserCommandRepository;
import com.example.pawgetherbe.usecase.reply.DeleteReplyUseCase;
import com.example.pawgetherbe.usecase.reply.EditReplyUseCase;
import com.example.pawgetherbe.usecase.reply.RegistryReplyUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.pawgetherbe.domain.UserContext.getUserId;
import static com.example.pawgetherbe.exception.command.CommentCommandErrorCode.NOT_FOUND_COMMENT;
import static com.example.pawgetherbe.exception.command.ReplyCommandErrorCode.NOT_FOUND_REPLY;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReplyCommandService implements RegistryReplyUseCase, EditReplyUseCase, DeleteReplyUseCase {

    private final ReplyCommandRepository replyCommandRepository;
    private final CommentCommandRepository commentCommandRepository;
    private final UserCommandRepository userCommandRepository;

    private final ReplyCommandMapper replyCommandMapper;

    @Override
    @Transactional
    public ReplyCreateResponse replyCreate(ReplyCreateRequest request) {
        var id = Long.valueOf(getUserId());
//        var id = 1L;
        var user = userCommandRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        var comment = commentCommandRepository.findById(request.commentId()).orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));

        var replyEntity = replyCommandMapper.toCreateEntity(request)
                .toBuilder()
                .user(user)
                .comment(comment)
                .status(ReplyStatus.ACTIVE)
                .build();

        var reply = replyCommandRepository.save(replyEntity);

        return replyCommandMapper.toReplyCreateResponse(reply);
    }

    @Override
    @Transactional
    public ReplyUpdateResponse updateReply(ReplyUpdateRequest request) {
        var id = Long.valueOf(getUserId());
//        var id = 1L;
        assertUserExists(id);
        assertCommentExists(request.commentId());

        var replyEntity = replyCommandRepository.findById(request.replyId()).orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        replyEntity.updateContent(request.content());

        return replyCommandMapper.toReplyUpdateResponse(replyEntity);
    }



    @Override
    @Transactional
    public void deleteReply(long commentId, long replyId) {
        var id = Long.valueOf(getUserId());
//        var id = 1L;
        assertUserExists(id);
        assertCommentExists(commentId);

        var replyEntity = replyCommandRepository.findById(replyId).orElseThrow(() -> new CustomException(NOT_FOUND_REPLY));

        replyEntity.updateStatus(ReplyStatus.REMOVED);
    }

    private void assertUserExists(long userId) {
        if (!userCommandRepository.existsById(userId)) {
            throw new CustomException(NOT_FOUND_USER);
        }
    }

    private void assertCommentExists(long commentId) {
        if (!commentCommandRepository.existsById(commentId)) {
            throw new CustomException(NOT_FOUND_COMMENT);
        }
    }
}
