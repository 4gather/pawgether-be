package com.example.pawgetherbe.controller.command.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public final class PetFairCommandDto {

    public record PetFairCreateRequest(
            @NotBlank(message = "제목을 입력해 주세요")
            String title,

            @NotBlank(message = "내용을 입력해 주세요")
            String content,

            @NotNull(message = "PosterImage 를 첨부해 주세요")
            MultipartFile posterImage,

            @NotBlank(message = "simpleAddress 를 입력해 주세요")
            String simpleAddress,

            @NotBlank(message = "detailAddress 를 입력해 주세요")
            String detailAddress,

            @NotBlank(message = "시작날짜를 입력해 주세요")
            String startDate,

            @NotBlank(message = "종료날짜를 입력해 주세요")
            String endDate,

            @NotBlank(message = "행사 회사 번호를 입력해 주세요")
            String telNumber,

            @NotBlank(message = "행사 링크를 입력해 주세요")
            String petFairUrl,

            @NotNull(message = "image 를 첨부해 주세요")
            @Size(max = 5, message = "image 는 최대 5개까지 첨부가능합니다.")
            List<MultipartFile> images
    ) {
        @AssertTrue(message = "PosterImage 를 첨부해 주세요")
        public boolean isPosterImageNotEmpty() {
            return posterImage != null && !posterImage.isEmpty();
        }

        @AssertTrue(message = "비어있는 image 파일이 있습니다")
        public boolean isImagesAllNonEmpty() {
            return images != null && !images.isEmpty()
                    && images.stream().allMatch(f -> f != null && !f.isEmpty());
        }
    }

    public record PetFairCreateResponse(
            Long petFairId,
            String title,
            String content,
            String posterImageUrl,
            String petFairUrl,
            String simpleAddress,
            String detailAddress,
            String startDate,
            String endDate,
            String telNumber,
            List<String> images,
            Long counter,
            String createdAt,
            String updatedAt
    ) {}
}
