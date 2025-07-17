package com.example.pawgetherbe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "comments")
public class CommentEntity extends BaseEntity {
    @Lob
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "comment_created_at")
    private Instant commentCreatedAt;

    @Column(name = "comment_updated_at")
    private Instant commentUpdatedAt;

    @Column(name = "status", length = 255)
    private String status;
}
