package com.example.pawgetherbe.repository;

import com.example.pawgetherbe.domain.entity.OauthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthRepository extends JpaRepository<OauthEntity, Long> {
    boolean existsByOauthProviderIdAndOauthProvider(String oauthProviderId, String oauthProvider);
    Optional<OauthEntity> findByOauthProviderId(String oauthProviderId);
    boolean existsByEmail(String email);
    void deleteByEmail(String email);

    boolean existsByUser_Id(Long userId);
    void deleteByUser_Id(Long userId);
}
