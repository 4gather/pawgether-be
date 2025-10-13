package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.QBookmarkEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
