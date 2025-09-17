package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentResponse;
import com.example.pawgetherbe.domain.entity.QCommentEntity;
import com.example.pawgetherbe.domain.status.CommentStatus;
import com.example.pawgetherbe.mapper.query.CommentQueryMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class CommentQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCommentEntity commentEntity = QCommentEntity.commentEntity;
    private final CommentQueryMapper commentQueryMapper;

    @Transactional(readOnly = true)
    public ReadCommentResponse readComments(Long id, long cursor) {
        var comments = jpaQueryFactory
                .select(commentEntity)
                .from(commentEntity)
                .where(
                        commentEntity.status.eq(CommentStatus.ACTIVE),
                        commentEntity.petFair.id.eq(id),
                        cursorCondition(cursor)
                )
                .orderBy(commentEntity.createdAt.asc(), commentEntity.id.asc())
                .limit(11)
                .fetch()
                .stream()
                .map(commentQueryMapper::toReadCommentDto)
                .toList();

        boolean hasMore = comments.size() == 11;
        long nextCursor = hasMore ? comments.get(10).commentId() : 0;
        if (comments.size() == 11) {
            comments = comments.subList(0, 10);
        }
        return commentQueryMapper.toReadCommentResponse(comments, hasMore, nextCursor);
    }

    private BooleanExpression cursorCondition(long cursor) {
        if (cursor > 0) {
            return commentEntity.id.goe(cursor);
        }
        return null;
    }
}
