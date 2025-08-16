package com.example.pawgetherbe.repository.command;

import com.example.pawgetherbe.domain.entity.OauthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OauthCommandRepository extends JpaRepository<OauthEntity, Long> {
    boolean existsByOauthProviderIdAndOauthProvider(String oauthProviderId, String oauthProvider);
    Optional<OauthEntity> findByOauthProviderId(String oauthProviderId);
    boolean existsByUser_Id(Long userId);
    void deleteByUser_Id(Long userId);
}
