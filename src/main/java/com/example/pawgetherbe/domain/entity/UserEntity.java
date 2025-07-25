package com.example.pawgetherbe.domain.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "users")
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "user_img", length = 255)
    private String userImg;

    @Column(name = "nick_name", length = 255)
    private String nickName;

    @Column(name = "role", length = 255)
    private String role;

    @OneToMany(mappedBy="user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<PetFairEntity> petFairEntities = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ReplyEntity> replyEntities = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OauthEntity> oauthEntities = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<CommentEntity> commentEntities = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BookmarkEntity> bookmarkEntities = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<LikeEntity> likeEntities = new ArrayList<>();
}
