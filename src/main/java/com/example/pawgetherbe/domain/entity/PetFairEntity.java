package com.example.pawgetherbe.domain.entity;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairRequest;
import com.example.pawgetherbe.domain.status.PetFairStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.EMPTY_ANY_FIELD;

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

//    @Lob
//    @Column(name = "map_url")
//    private String mapUrl;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "counter")
    private Long counter;

//    @Column(name = "latitude", length = 255)
//    private String latitude;
//
//    @Column(name = "longitude", length = 255)
//    private String longitude;

    @Column(name = "tel_number", length = 255)
    private String telNumber;

    @Column(name = "status", length = 255)
    @Enumerated(EnumType.STRING)
    private PetFairStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @OneToMany(mappedBy="petFair", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    private List<PetFairImageEntity> pairImages = new ArrayList<>();

    @OneToMany(mappedBy="petFair", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<CommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy="petFair", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<BookmarkEntity> bookmarkEntities = new ArrayList<>();

    public void updateImage(String posterImageUrl, List<PetFairImageEntity> pairImages,
                            PetFairStatus status, UserEntity user) {
        this.posterImageUrl = posterImageUrl;
        this.status = status;
        this.user = user;

        this.pairImages.clear();
        for (PetFairImageEntity img : pairImages) {
            this.addImage(img);
        }
    }

    public void addImage(PetFairImageEntity image) {
        pairImages.add(image);
        image.setPetFair(this);
    }

    public void updateStatus(PetFairStatus status) {
        this.status = status;
    }

    public void updatePetFair(String posterImageUrl, List<PetFairImageEntity> pairImages, UpdatePetFairRequest request) {
        try {
            Objects.requireNonNull(request.title(), "title을 입력해주세요.");
            Objects.requireNonNull(request.content(), "content를 입력해주세요.");
            Objects.requireNonNull(posterImageUrl, "posterImageUrl을 입력해주세요.");
            Objects.requireNonNull(request.simpleAddress(), "simpleAddress를 입력해주세요.");
            Objects.requireNonNull(request.detailAddress(), "detailAddress를 입력해주세요.");
            Objects.requireNonNull(request.startDate(), "startDate를 입력해주세요.");
            Objects.requireNonNull(request.endDate(), "endDate를 입력해주세요.");
            Objects.requireNonNull(request.telNumber(), "telNumber를 입력해주세요.");
            Objects.requireNonNull(request.petFairUrl(), "petFairUrl을 입력해주세요.");
            Objects.requireNonNull(pairImages, "pairImages를 입력해주세요.");
        } catch (NullPointerException e) {
            throw new CustomException(EMPTY_ANY_FIELD);
        }

        this.title = request.title();
        this.content = request.content();
        this.posterImageUrl = posterImageUrl;
        this.simpleAddress = request.simpleAddress();
        this.detailAddress = request.detailAddress();
        this.startDate = LocalDate.parse(request.startDate());
        this.endDate = LocalDate.parse(request.endDate());
        this.telNumber = request.telNumber();
        this.petFairUrl = request.petFairUrl();

        this.pairImages.clear();
        for (PetFairImageEntity img : pairImages) {
            this.addImage(img);
        }
    }
}
