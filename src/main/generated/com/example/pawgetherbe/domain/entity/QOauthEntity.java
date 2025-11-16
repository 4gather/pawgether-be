package com.example.pawgetherbe.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QOauthEntity is a Querydsl query type for OauthEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QOauthEntity extends EntityPathBase<OauthEntity> {

    private static final long serialVersionUID = -1907637598L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QOauthEntity oauthEntity = new QOauthEntity("oauthEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath oauthProvider = createString("oauthProvider");

    public final StringPath oauthProviderId = createString("oauthProviderId");

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final QUserEntity user;

    public QOauthEntity(String variable) {
        this(OauthEntity.class, forVariable(variable), INITS);
    }

    public QOauthEntity(Path<? extends OauthEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QOauthEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QOauthEntity(PathMetadata metadata, PathInits inits) {
        this(OauthEntity.class, metadata, inits);
    }

    public QOauthEntity(Class<? extends OauthEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new QUserEntity(forProperty("user")) : null;
    }

}

