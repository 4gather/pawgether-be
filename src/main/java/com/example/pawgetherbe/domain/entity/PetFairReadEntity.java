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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "pet_fair_read")
public class PetFairReadEntity extends BaseEntity {

    @Column(name = "user_id", columnDefinition = "BIGINT")
    private long userId;

    @Column(name = "pet_fair_id", columnDefinition = "BIGINT")
    private long petFairId;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Lob
    @Column(name = "content")
    private String content;

    @Lob
    @Column(name = "poster_image_url")
    private String posterImageUrl;

    @Lob
    @Column(name = "petFairUrl")
    private String petFairUrl;

    @Lob
    @Column(name = "map_url")
    private String mapUrl;

    @Column(name = "counter")
    private Long counter;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "simple_address", length = 255)
    private String simpleAddress;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "latitude", length = 255)
    private String latitude;

    @Column(name = "longitude", length = 255)
    private String longitude;

    @Column(name = "tel_number", length = 255)
    private String telNumber;

    @Column(name = "status", length = 255)
    private String status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "image_url", columnDefinition = "jsonb")
    private List<String> images;
}
