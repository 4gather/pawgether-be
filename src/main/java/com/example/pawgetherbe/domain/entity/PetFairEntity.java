package com.example.pawgetherbe.domain.entity;

import com.example.pawgetherbe.domain.status.PostStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "pet_fair")
public class PetFairEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_fair_id")
    private Long id;

    @Column(name = "title", length = 255)
    private String title;

    @Lob
    @Column(name = "poster_image_url")
    private String posterImageUrl;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "simple_address", length = 255)
    private String simpleAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Lob
    @Column(name = "pet_fair_url")
    private String petFairUrl;

    @Lob
    @Column(name = "map_url")
    private String mapUrl;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "counter")
    private Long counter;

    @Column(name = "latitude", length = 255)
    private String latitude;

    @Column(name = "longitude", length = 255)
    private String longitude;

    @Column(name = "tel_number", length = 255)
    private String telNumber;

    @Column(name = "status", length = 255)
    @Enumerated(EnumType.STRING)
    private PostStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy="petFair", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<PetFairImageEntity> pairImages = new ArrayList<>();

    @OneToMany(mappedBy="petFair", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy="petFair", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BookmarkEntity> bookmarkEntities = new ArrayList<>();
}
