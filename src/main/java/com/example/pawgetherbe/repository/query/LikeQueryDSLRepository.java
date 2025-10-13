package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeRequest;
import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import com.example.pawgetherbe.domain.entity.QLikeEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LikeQueryDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QLikeEntity likeEntity = QLikeEntity.likeEntity;

    @Transactional(readOnly = true)
    public boolean hasUserLikedTarget(LikeRequest likeRequest) {
        return jpaQueryFactory.selectOne()
                .from(likeEntity)
                .where(
                        // 실제 서비스에서는 Long.parseLong(UserContext.getUserId()) 사용
//                        likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId())),
                        likeEntity.user.id.eq(1L),
                        likeEntity.targetType.eq(likeRequest.targetType()),
                        likeEntity.targetId.eq(likeRequest.targetId())
                )
                .fetchFirst() != null;
    }

    @Transactional(readOnly = true)
    public Optional<LikeEntity> findLikeByUserAndTarget(LikeRequest likeRequest) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(likeEntity)
                        .where(
                                // 실제 서비스에서는 Long.parseLong(UserContext.getUserId()) 사용
//                                likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId())),
                                likeEntity.user.id.eq(1L),
                                likeEntity.targetType.eq(likeRequest.targetType()),
                                likeEntity.targetId.eq(likeRequest.targetId())
                        )
                        .fetchOne()
        );
    }

    @Transactional(readOnly = true)
    public long countLikeByTarget(LikeRequest likeRequest) {
        Long count = jpaQueryFactory
                .select(likeEntity.count())
                .from(likeEntity)
                .where(
                        likeEntity.targetType.eq(likeRequest.targetType()),
                        likeEntity.targetId.eq(likeRequest.targetId())
                )
                .fetchOne();

        return (count != null) ? count : 0L;
    }

    @Transactional(readOnly = true)
    public List<LikeEntity> findLikesByUser() {
        return jpaQueryFactory
                .selectFrom(likeEntity)
                .where(
                        // 실제 서비스에서는 Long.parseLong(UserContext.getUserId()) 사용
//                        likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId()))
                        likeEntity.user.id.eq(1L)
                )
                .orderBy(likeEntity.createdAt.desc())  // 최신 좋아요 순으로 정렬
                .fetch();
    }
}
