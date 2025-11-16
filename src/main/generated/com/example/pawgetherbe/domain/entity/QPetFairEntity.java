package com.example.pawgetherbe.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPetFairEntity is a Querydsl query type for PetFairEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPetFairEntity extends EntityPathBase<PetFairEntity> {

    private static final long serialVersionUID = -821105170L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPetFairEntity petFairEntity = new QPetFairEntity("petFairEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<BookmarkEntity, QBookmarkEntity> bookmarkEntities = this.<BookmarkEntity, QBookmarkEntity>createList("bookmarkEntities", BookmarkEntity.class, QBookmarkEntity.class, PathInits.DIRECT2);

    public final ListPath<CommentEntity, QCommentEntity> comments = this.<CommentEntity, QCommentEntity>createList("comments", CommentEntity.class, QCommentEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final NumberPath<Long> counter = createNumber("counter", Long.class);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath detailAddress = createString("detailAddress");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<PetFairImageEntity, QPetFairImageEntity> pairImages = this.<PetFairImageEntity, QPetFairImageEntity>createList("pairImages", PetFairImageEntity.class, QPetFairImageEntity.class, PathInits.DIRECT2);

    public final StringPath petFairUrl = createString("petFairUrl");

    public final StringPath posterImageUrl = createString("posterImageUrl");

    public final StringPath simpleAddress = createString("simpleAddress");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final EnumPath<com.example.pawgetherbe.domain.status.PetFairStatus> status = createEnum("status", com.example.pawgetherbe.domain.status.PetFairStatus.class);

    public final StringPath telNumber = createString("telNumber");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final QUserEntity user;

    public QPetFairEntity(String variable) {
        this(PetFairEntity.class, forVariable(variable), INITS);
    }

    public QPetFairEntity(Path<? extends PetFairEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPetFairEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPetFairEntity(PathMetadata metadata, PathInits inits) {
        this(PetFairEntity.class, metadata, inits);
    }

    public QPetFairEntity(Class<? extends PetFairEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

