package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadDto;
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadResponse;
import com.example.pawgetherbe.domain.entity.ReplyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface ReplyQueryMapper {

    @Mapping(target = "heart", source = "count")
    @Mapping(target = "replyId", source = "replyEntity.id")
    @Mapping(target = "nickName", source = "replyEntity.user.nickName")
    @Mapping(target = "commentId", source = "replyEntity.comment.id")
    ReplyReadDto toReplyReadDto(ReplyEntity replyEntity, int count);

    ReplyReadResponse toReplyReadResponse(List<ReplyReadDto> replies, boolean hasMore, long nextCursor);
}
