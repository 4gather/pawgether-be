package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.domain.entity.QUserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class UserQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private final QUserEntity user = QUserEntity.userEntity;

    @Transactional(readOnly = true)
    public boolean existsByNickNameToLowerCase(String nickName) {

        var lowerNickName = user.nickName.lower();

        long count = jpaQueryFactory.select(user)
                .from(user)
                .where(lowerNickName.eq(nickName.toLowerCase()))
                .fetchCount();

        return count > 0;
    }
}
