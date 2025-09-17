package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentDto;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CommentQueryMapper {
    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "nickName", source = "user.nickName")
    @Mapping(target = "petFairId", source = "petFair.id")
    ReadCommentDto toReadCommentDto(CommentEntity comment);
    CommentQueryDto.ReadCommentResponse toReadCommentResponse(List<ReadCommentDto> comments, boolean hasMore, long nextCursor);
}
