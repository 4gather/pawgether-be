package com.example.pawgetherbe.repository;

import com.example.pawgetherbe.domain.entity.OauthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OauthRepository extends JpaRepository<OauthEntity, Long> {
    boolean existsByOauthRegistrationIdAndOauthUserId(String oauthRegistrationId, String oauthUserId);
}
