package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateRequest;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateResponse;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateResponse;
import com.example.pawgetherbe.domain.entity.ReplyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ReplyCommandMapper {
    ReplyEntity toCreateEntity(ReplyCreateRequest replyCreateRequest);

    @Mapping(target = "replyId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "commentId", source = "comment.id")
    ReplyCreateResponse toReplyCreateResponse(ReplyEntity replyEntity);

    @Mapping(target = "replyId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "commentId", source = "comment.id")
    ReplyUpdateResponse toReplyUpdateResponse(ReplyEntity replyEntity);
}
