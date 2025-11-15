package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.CommentCountDto;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.CommentCountResponse;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.MainCommentResponse;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentResponse;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import com.example.pawgetherbe.domain.entity.QCommentEntity;
import com.example.pawgetherbe.domain.entity.QLikeEntity;
import com.example.pawgetherbe.domain.status.CommentStatus;
import com.example.pawgetherbe.mapper.query.CommentQueryMapper;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class CommentQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCommentEntity commentEntity = QCommentEntity.commentEntity;
    private final QLikeEntity likeEntity = QLikeEntity.likeEntity;
    private final CommentQueryMapper commentQueryMapper;

    @Transactional(readOnly = true)
    public ReadCommentResponse readComments(Long id, long cursor) {
        List<CommentEntity> comments = jpaQueryFactory
                .select(commentEntity)
                .from(commentEntity)
                .where(
                        commentEntity.status.eq(CommentStatus.ACTIVE),
                        commentEntity.petFair.id.eq(id),
                        cursorCondition(cursor)
                )
                .groupBy(commentEntity.id)
                .orderBy(commentEntity.createdAt.asc(), commentEntity.id.asc())
                .limit(11)
                .fetch();

        var commentList = comments.stream()
                .map(commentQueryMapper::toReadCommentDto)
                .toList();

        boolean hasMore = commentList.size() == 11;
        long nextCursor = hasMore ? commentList.get(10).commentId() : 0;
        if (commentList.size() == 11) {
            commentList = commentList.subList(0, 10);
        }
        return commentQueryMapper.toReadCommentResponse(commentList, hasMore, nextCursor);
    }

    @Transactional(readOnly = true)
    public MainCommentResponse mainComments() {
        List<CommentEntity> comments = jpaQueryFactory
                .select(commentEntity)
                .from(commentEntity)
                .where(
                        commentEntity.status.eq(CommentStatus.ACTIVE)
                )
                .groupBy(commentEntity.id)
                .orderBy(commentEntity.createdAt.desc())
                .limit(10)
                .fetch();

        var commentList = comments.stream()
                .map(commentQueryMapper::toMainCommentResponse)
                .toList();
        return new MainCommentResponse(commentList);
    }

    @Transactional(readOnly = true)
    public CommentCountResponse commentCountResponse(long petfairId) {
        Long count = jpaQueryFactory.select(commentEntity.id.count())
                    .from(commentEntity)
                    .where(
                        commentEntity.petFair.id.eq(petfairId),
                        commentEntity.status.eq(CommentStatus.ACTIVE))
                    .fetchOne();

        long safeCount = (count == null) ? 0L : count;

        var commentCountDto = new CommentCountDto("petFair", petfairId, safeCount);

        return new CommentCountResponse(commentCountDto);
    }


    @Transactional(readOnly = true)
    public boolean existsByCommentId(Long commentId) {
        return jpaQueryFactory
                .selectOne()
                .from(commentEntity)
                .where(
                        commentEntity.user.id.eq(1L),
//                        실제 Service에서 주석 코드 사용
//                        reply.user.id.eq(Long.parseLong(UserContext.getUserId()));
                        commentEntity.id.eq(commentId)
                )
                .fetchFirst() != null;
    }

    @Transactional(readOnly =true)
    public Set<Long> existsByCommentIdList(Set<Long> commentIdList) {
        return new HashSet<>(
                jpaQueryFactory
                        .select(commentEntity.id)
                        .from(commentEntity)
                        .where(
                                commentEntity.user.id.eq(1L),
//                                실제 Service에서 주석 코드 사용
//                                reply.user.id.eq(Long.parseLong(UserContext.getUserId()));
                                commentEntity.id.in(commentIdList)
                        )
                        .fetch()
        );
    }


    private BooleanExpression cursorCondition(long cursor) {
        if (cursor > 0) {
            return commentEntity.id.goe(cursor);
        }
        return null;
    }
}
