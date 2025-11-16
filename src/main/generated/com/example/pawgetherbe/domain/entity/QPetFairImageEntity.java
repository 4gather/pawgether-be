package com.example.pawgetherbe.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPetFairImageEntity is a Querydsl query type for PetFairImageEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPetFairImageEntity extends EntityPathBase<PetFairImageEntity> {

    private static final long serialVersionUID = -1898164749L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPetFairImageEntity petFairImageEntity = new QPetFairImageEntity("petFairImageEntity");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final DateTimePath<java.time.Instant> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath imageUrl = createString("imageUrl");

    public final QPetFairEntity petFair;

    public final NumberPath<Integer> sortOrder = createNumber("sortOrder", Integer.class);

    //inherited
    public final DateTimePath<java.time.Instant> updatedAt = _super.updatedAt;

    public QPetFairImageEntity(String variable) {
        this(PetFairImageEntity.class, forVariable(variable), INITS);
    }

    public QPetFairImageEntity(Path<? extends PetFairImageEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPetFairImageEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPetFairImageEntity(PathMetadata metadata, PathInits inits) {
        this(PetFairImageEntity.class, metadata, inits);
    }

    public QPetFairImageEntity(Class<? extends PetFairImageEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.petFair = inits.isInitialized("petFair") ? new QPetFairEntity(forProperty("petFair"), inits.get("petFair")) : null;
    }

}

