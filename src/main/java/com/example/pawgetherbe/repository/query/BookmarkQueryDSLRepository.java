package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.QBookmarkEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class BookmarkQueryDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QBookmarkEntity bookmarkEntity = QBookmarkEntity.bookmarkEntity;

    public List<BookmarkEntity> readBookmarks() {
        return jpaQueryFactory
                .selectFrom(bookmarkEntity)
                .orderBy(bookmarkEntity.createdAt.desc(), bookmarkEntity.id.desc())
                .limit(11) // hasMore 고려
                .fetch();
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
}
