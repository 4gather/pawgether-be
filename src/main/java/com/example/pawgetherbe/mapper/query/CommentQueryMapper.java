package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentDto;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CommentQueryMapper {
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "nickName", source = "comment.user.nickName")
    @Mapping(target = "petFairId", source = "comment.petFair.id")
    @Mapping(target = "heart", source = "count")
    ReadCommentDto toReadCommentDto(CommentEntity comment, int count);
    CommentQueryDto.ReadCommentResponse toReadCommentResponse(List<ReadCommentDto> comments, boolean hasMore, long nextCursor);
}
