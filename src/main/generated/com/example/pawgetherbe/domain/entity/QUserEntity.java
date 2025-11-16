package com.example.pawgetherbe.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUserEntity is a Querydsl query type for UserEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserEntity extends EntityPathBase<UserEntity> {

    private static final long serialVersionUID = -389890746L;

    public static final QUserEntity userEntity = new QUserEntity("userEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    public final ListPath<BookmarkEntity, QBookmarkEntity> bookmarkEntities = this.<BookmarkEntity, QBookmarkEntity>createList("bookmarkEntities", BookmarkEntity.class, QBookmarkEntity.class, PathInits.DIRECT2);

    public final ListPath<CommentEntity, QCommentEntity> commentEntities = this.<CommentEntity, QCommentEntity>createList("commentEntities", CommentEntity.class, QCommentEntity.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<LikeEntity, QLikeEntity> likeEntities = this.<LikeEntity, QLikeEntity>createList("likeEntities", LikeEntity.class, QLikeEntity.class, PathInits.DIRECT2);

    public final StringPath nickName = createString("nickName");

    public final ListPath<OauthEntity, QOauthEntity> oauthEntities = this.<OauthEntity, QOauthEntity>createList("oauthEntities", OauthEntity.class, QOauthEntity.class, PathInits.DIRECT2);

    public final StringPath password = createString("password");

    public final ListPath<PetFairEntity, QPetFairEntity> petFairEntities = this.<PetFairEntity, QPetFairEntity>createList("petFairEntities", PetFairEntity.class, QPetFairEntity.class, PathInits.DIRECT2);

    public final ListPath<ReplyEntity, QReplyEntity> replyEntities = this.<ReplyEntity, QReplyEntity>createList("replyEntities", ReplyEntity.class, QReplyEntity.class, PathInits.DIRECT2);

    public final EnumPath<com.example.pawgetherbe.domain.status.UserRole> role = createEnum("role", com.example.pawgetherbe.domain.status.UserRole.class);

    public final EnumPath<com.example.pawgetherbe.domain.status.UserStatus> status = createEnum("status", com.example.pawgetherbe.domain.status.UserStatus.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public final StringPath userImg = createString("userImg");

    public QUserEntity(String variable) {
        super(UserEntity.class, forVariable(variable));
    }

    public QUserEntity(Path<? extends UserEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserEntity(PathMetadata metadata) {
        super(UserEntity.class, metadata);
    }

}

