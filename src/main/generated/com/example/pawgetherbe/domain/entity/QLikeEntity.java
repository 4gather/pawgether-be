package com.example.pawgetherbe.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikeEntity is a Querydsl query type for LikeEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikeEntity extends EntityPathBase<LikeEntity> {

    private static final long serialVersionUID = 1192720402L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikeEntity likeEntity = new QLikeEntity("likeEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public final StringPath targetType = createString("targetType");

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final QUserEntity user;

    public QLikeEntity(String variable) {
        this(LikeEntity.class, forVariable(variable), INITS);
    }

    public QLikeEntity(Path<? extends LikeEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikeEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikeEntity(PathMetadata metadata, PathInits inits) {
        this(LikeEntity.class, metadata, inits);
    }

    public QLikeEntity(Class<? extends LikeEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

