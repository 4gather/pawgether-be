package com.example.pawgetherbe.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "bookmarks")
public class BookmarkEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patFairId", nullable = false)
    private PetFairEntity petFair;

    public void addUser(UserEntity userEntity) {
        this.user = userEntity;
        userEntity.getBookmarkEntities().add(this);
    }

    public void addPetFair(PetFairEntity petFairEntity) {
        this.petFair = petFairEntity;
        petFairEntity.getBookmarkEntities().add(this);
    }

    public void removeUser(UserEntity userEntity) {
        userEntity.getBookmarkEntities().remove(this);
    }

    public void removePetFair(PetFairEntity petFairEntity) {
        petFairEntity.getBookmarkEntities().remove(this);
    }
}
