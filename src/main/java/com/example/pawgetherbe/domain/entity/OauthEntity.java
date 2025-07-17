package com.example.pawgetherbe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "oauth")
public class OauthEntity extends BaseEntity {
    @Column(name = "oauth_provider", nullable = false, length = 255)
    private String oauthProvider;

    @Column(name = "oauth_user_id", nullable = false, length = 255)
    private String oauthUserId;
}
