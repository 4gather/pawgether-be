package com.example.pawgetherbe.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBookmarkEntity is a Querydsl query type for BookmarkEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBookmarkEntity extends EntityPathBase<BookmarkEntity> {

    private static final long serialVersionUID = -845816207L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBookmarkEntity bookmarkEntity = new QBookmarkEntity("bookmarkEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final QPetFairEntity petFair;

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final QUserEntity user;

    public QBookmarkEntity(String variable) {
        this(BookmarkEntity.class, forVariable(variable), INITS);
    }

    public QBookmarkEntity(Path<? extends BookmarkEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBookmarkEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBookmarkEntity(PathMetadata metadata, PathInits inits) {
        this(BookmarkEntity.class, metadata, inits);
    }

    public QBookmarkEntity(Class<? extends BookmarkEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.petFair = inits.isInitialized("petFair") ? new QPetFairEntity(forProperty("petFair"), inits.get("petFair")) : null;
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

