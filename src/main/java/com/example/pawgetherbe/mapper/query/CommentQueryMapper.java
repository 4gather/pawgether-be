package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.MainCommentDto;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentDto;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentResponse;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CommentQueryMapper {
    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "nickName", source = "comment.user.nickName")
    @Mapping(target = "petFairId", source = "comment.petFair.id")
    ReadCommentDto toReadCommentDto(CommentEntity comment);
    ReadCommentResponse toReadCommentResponse(List<ReadCommentDto> comments, boolean hasMore, long nextCursor);

    @Mapping(target = "commentId", source = "comment.id")
    @Mapping(target = "nickName", source = "comment.user.nickName")
    @Mapping(target = "petFairId", source = "comment.petFair.id")
    MainCommentDto toMainCommentResponse(CommentEntity comment);
}
