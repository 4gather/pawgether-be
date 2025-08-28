package com.example.pawgetherbe.petfair

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.query.dto.PetFairImageQueryDto.PetFairImageUrlResponse
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse
import com.example.pawgetherbe.domain.entity.PetFairEntity
import com.example.pawgetherbe.domain.entity.PetFairImageEntity
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.PostStatus
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.mapper.query.PetFairQueryMapper
import com.example.pawgetherbe.repository.query.PetFairQueryRepository
import com.example.pawgetherbe.service.query.PetFairQueryService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.time.Instant
import java.time.LocalDate
import java.util.*

class PetFairQueryServiceTest: FreeSpec ({
    lateinit var service: PetFairQueryService
    lateinit var repository: PetFairQueryRepository
    lateinit var mapper: PetFairQueryMapper

    lateinit var imageEntitiesList: List<PetFairImageEntity>
    lateinit var imageDtoList: List<PetFairImageUrlResponse>

    beforeTest {
        repository = mockk<PetFairQueryRepository>(relaxed = true)
        mapper = mockk<PetFairQueryMapper>(relaxed = true)

        service = PetFairQueryService(repository, mapper)
    }

    "단건조회" - {
        "2xx] 정상 조회" - {
            // Given
            val petFairId = 1L
            val localDateNow = LocalDate.now()
            val instantNow = Instant.now()
            imageEntitiesList = listOf(
                PetFairImageEntity.builder()
                    .id(1L)
                    .imageUrl("images/content/2025/05/0515-1.webp")
                    .build(),
                PetFairImageEntity.builder()
                    .id(2L)
                    .imageUrl("images/content/2025/05/0515-2.webp")
                    .build()
            )
            imageDtoList = listOf(
                PetFairImageUrlResponse("images/content/2025/05/0515-1.webp"),
                PetFairImageUrlResponse("images/content/2025/05/0515-2.webp")
            )
            val testUser = UserEntity.builder()
                .id(1L)
                .email("test@test.com")
                .role(UserRole.USER_EMAIL)
                .nickName("testNickName")
                .build()
            val savedPetFairEntity = PetFairEntity.builder()
                .id(1L)
                .user(testUser)
                .title("2025 메가주 일산(상) 1")
                .posterImageUrl("public/poster/2025/05/0515.webp")
                .startDate(localDateNow)
                .endDate(localDateNow)
                .simpleAddress("킨텍스 2전시장")
                .detailAddress("경기도 고양시 일산서구 킨텍스로 271-59")
                .petFairUrl("https://k-pet.co.kr/information/scheduled-list/2025_megazoo_spring/")
                .content("메가주 일산 설명")
                .counter(1L)
                .latitude("37.514575")
                .longitude("127.063287")
                .mapUrl("https://map.naver.com/p/entry/address/37.514575,127.063287,경기도 고양시 일산서구 킨텍스로 271-59?c=15.00,0,0,0,dh")
                .telNumber("02-6121-6247")
                .status(PostStatus.ACTIVE)
                .pairImages(imageEntitiesList)
                .build()
            val savedPetFairMappedDto = DetailPetFairResponse(
                1L,
                testUser.id,
                "2025 메가주 일산(상) 1",
                "public/poster/2025/05/0515.webp",
                localDateNow,
                localDateNow,
                "킨텍스 2전시장",
                "경기도 고양시 일산서구 킨텍스로 271-59",
                "https://k-pet.co.kr/information/scheduled-list/2025_megazoo_spring/",
                "메가주 일산 설명",
                1L,
                "37.514575",
                "127.063287",
                "https://map.naver.com/p/entry/address/37.514575,127.063287,경기도 고양시 일산서구 킨텍스로 271-59?c=15.00,0,0,0,dh",
                "02-6121-6247",
                PostStatus.ACTIVE,
                instantNow,
                instantNow,
                imageDtoList
            )

            every { repository.findByIdAndPostStatus(any<Long>(), PostStatus.ACTIVE) } returns Optional.of(savedPetFairEntity)
            every { mapper.toDetailPetFair(savedPetFairEntity)} returns savedPetFairMappedDto

            // When
            val result = service.readDetailPetFair(petFairId)

            // Then
            verify(exactly = 1) { repository.findByIdAndPostStatus(petFairId, PostStatus.ACTIVE) }

            "게시글 존재" {
                result shouldNotBe null
                result.petFairId shouldBeEqual 1L
            }
            "게시글 사용자 존재" {
                result.userId shouldBeEqual 1L
            }
            "title 존재" {
                result.title shouldBe "2025 메가주 일산(상) 1"
            }
            "pairImage URL 존재" {
                result.images.get(0).imageUrl shouldBe "images/content/2025/05/0515-1.webp"
            }
        }

        "4xx] 등록된 게시글이 존재하지 않음" - {
            // Given
            val petFairId = 1L
            val active = PostStatus.ACTIVE

            every { repository.findByIdAndPostStatus(any<Long>(), active) } returns Optional.empty()

            // When
            val exception = shouldThrow<CustomException> {
                service.readDetailPetFair(petFairId)
            }

            // Then
            verify(exactly = 1) { repository.findByIdAndPostStatus(petFairId, active) }

            "게시글 없는 ErrorCode" {
                val errorCode = exception.errorCode

                errorCode.httpStatus() shouldBeEqual HttpStatus.NOT_FOUND
                errorCode.code() shouldBeEqual  "NOT_FOUND_POST"
                errorCode.message() shouldBeEqual "게시글이 존재하지 않습니다."
            }

        }
    }
})
