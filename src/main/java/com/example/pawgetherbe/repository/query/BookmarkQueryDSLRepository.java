package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.ReadBookmarkListResponse;
import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.QBookmarkEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BookmarkQueryDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QBookmarkEntity bookmarkEntity = QBookmarkEntity.bookmarkEntity;

    public List<ReadBookmarkListResponse> readBookmarkList(Long lastCursor) {
        List<Tuple> tupleList =  jpaQueryFactory
                .select(bookmarkEntity.id, bookmarkEntity.petFair.id)
                .from(bookmarkEntity)
                .where(
                        bookmarkEntity.user.id.eq(1L),
                        // TODO: Filter에서 UserContext 사용 시 주석 코드 사용
//                        bookmarkEntity.user.id.eq(Long.parseLong(UserContext.getUserId())),
                        // TODO: cursor(마지막 게시글 아이디), 필터링 이상
                        lessThenCursor(lastCursor)
                )
                .orderBy(bookmarkEntity.id.desc())
                .limit(11) // hasMore 고려
                .fetch();

        return tupleList.stream()
                .map(tuple -> new ReadBookmarkListResponse(tuple.get(bookmarkEntity.id),
                        tuple.get(bookmarkEntity.petFair.id)))
                .collect(Collectors.toList());
    }

    public Set<Long> existsBookmark(Set<Long> targetIds) {
        return new HashSet<>(
                jpaQueryFactory
                    .select(bookmarkEntity.petFair.id)
                    .from(bookmarkEntity)
                    .where(
                            bookmarkEntity.user.id.eq(1L),
                            // TODO: Filter에서 UserContext 사용 시 주석 코드 사용
//                            bookmarkEntity.user.id.eq(Long.parseLong(UserContext.getUserId())),
                            bookmarkEntity.petFair.id.in(targetIds)
                    )
                    .fetch()
        );
    }

    private BooleanExpression lessThenCursor(Long cursor) {
        if (cursor == null) {
            return null;
        }

        return bookmarkEntity.id.lt(cursor);
    }
}
