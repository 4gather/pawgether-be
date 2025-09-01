package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.query.dto.PetFairImageQueryDto.PetFairImageUrlResponse
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCarouselResponse
import com.example.pawgetherbe.domain.entity.PetFairEntity
import com.example.pawgetherbe.domain.entity.PetFairImageEntity
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.PetFairStatus
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.mapper.query.PetFairQueryMapper
import com.example.pawgetherbe.repository.query.PetFairQueryDSLRepository
import com.example.pawgetherbe.service.query.PetFairQueryService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Instant
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class PetFairQueryServiceTest: FreeSpec ({
    lateinit var petFairQueryService: PetFairQueryService
    lateinit var petFairQueryDSLRepository: PetFairQueryDSLRepository
    lateinit var petFairQueryMapper: PetFairQueryMapper

    lateinit var imageEntitieList: List<PetFairImageEntity>
    lateinit var imageDtoList: List<PetFairImageUrlResponse>

    "Carousel 조회" - {
        "Carousel 조회 성공" {
            val petFairImages = listOf(
                PetFairQueryDto.PetFairPosterDto(1L, "images/poster/2025/05/0505.webp"),
                PetFairQueryDto.PetFairPosterDto(2L, "images/poster/2025/07/0715.webp"),
                PetFairQueryDto.PetFairPosterDto(3L, "images/poster/2025/08/0805.webp"),
                PetFairQueryDto.PetFairPosterDto(4L, "images/poster/2025/09/0915.webp"),
                PetFairQueryDto.PetFairPosterDto(5L, "images/poster/2025/10/1005.webp"),
                PetFairQueryDto.PetFairPosterDto(6L, "images/poster/2025/11/1115.webp"),
                PetFairQueryDto.PetFairPosterDto(7L, "images/poster/2025/12/1205.webp"),
                PetFairQueryDto.PetFairPosterDto(8L, "images/poster/2025/1/0115.webp"),
                PetFairQueryDto.PetFairPosterDto(9L, "images/poster/2025/2/0205.webp"),
                PetFairQueryDto.PetFairPosterDto(10L, "images/poster/2025/3/0315.webp"),
            )
            val petFairCarouselResponse = PetFairCarouselResponse(petFairImages)

            every { petFairQueryDSLRepository.petFairCarousel() } returns petFairImages

            val res = petFairQueryService.petFairCarousel()

            res shouldBe petFairCarouselResponse
            res.petFairImages shouldBe petFairCarouselResponse.petFairImages

            verify(exactly = 1) { petFairQueryDSLRepository.petFairCarousel() }

        }

        "Carousel 조회 없음" {
            val petFairImages = emptyList<PetFairQueryDto.PetFairPosterDto>()

            every { petFairQueryDSLRepository.petFairCarousel() } returns petFairImages

            val exception = shouldThrow<CustomException> {
                petFairQueryService.petFairCarousel()
            }

            verify(exactly = 1) { petFairQueryDSLRepository.petFairCarousel() }

            val errorCode = exception.errorCode
            errorCode.message() shouldBe "펫페어 포스터가 없습니다."
            errorCode.httpStatus() shouldBe HttpStatus.NOT_FOUND
            errorCode.code() shouldBe "NOT_FOUND_PET_FAIR_POSTER"
        }
    }

    "Calendar 조회" - {
        "Calendar 조회 성공" {
            val petFairCalendars = listOf(
                PetFairQueryDto.PetFairCalendarDto(1L, 100L, "PetFair 1", "images/poster/2025/05/0505.webp",
                    LocalDate.of(2025, 5, 5), LocalDate.of(2025, 5, 6), "경기도 고양시 킨텍스"),
                PetFairQueryDto.PetFairCalendarDto(2L, 200L, "PetFair 2", "images/poster/2025/07/0715.webp",
                    LocalDate.of(2025, 7, 15), LocalDate.of(2025, 7, 16), "서울 삼성동 코엑스")
            )
            val petFairCalendarResponse = PetFairQueryDto.PetFairCalendarResponse(petFairCalendars)

            every { petFairQueryDSLRepository.petFairCalendar(any()) } returns petFairCalendars

            val res = petFairQueryService.petFairCalendar("2025-08-29")

            res.petFairs shouldBe petFairCalendarResponse.petFairs
            res shouldBe petFairCalendarResponse

            verify(exactly = 1) { petFairQueryDSLRepository.petFairCalendar(any()) }

        }
        "Calendar 조회 실패" {
            val petFairCalendars = emptyList<PetFairQueryDto.PetFairCalendarDto>()

            every { petFairQueryDSLRepository.petFairCalendar(any()) } returns petFairCalendars

            val exception = shouldThrow<CustomException> {
                petFairQueryService.petFairCalendar("2025-08-29")
            }

            verify(exactly = 1) { petFairQueryDSLRepository.petFairCalendar(any()) }

            val errorCode = exception.errorCode
            errorCode.message() shouldBe "펫페어 행사가 없습니다."
            errorCode.httpStatus() shouldBe HttpStatus.NOT_FOUND
            errorCode.code() shouldBe "NOT_FOUND_PET_FAIR_CALENDAR"
        }
    }

    "단건조회" - {
        "2xx] 정상조회" - {
            val petFairId = 1L
            val localDateNow = LocalDate.now()
            val instantNow = Instant.now()
            imageEntitieList = listOf(
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
                .status(PetFairStatus.ACTIVE)
                .pairImages(imageEntitieList)
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
                PetFairStatus.ACTIVE,
                instantNow,
                instantNow,
                imageDtoList
            )
            every { petFairQueryDSLRepository.findActiveById(any<Long>()) } returns Optional.of(savedPetFairEntity)
            every { petFairQueryMapper.toDetailPetFair(savedPetFairEntity)} returns savedPetFairMappedDto

            // When
            val result = petFairQueryService.readDetailPetFair(petFairId)

            // Then
            verify(exactly = 1) { petFairQueryDSLRepository.findActiveById(petFairId) }

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

            every { petFairQueryDSLRepository.findActiveById(any<Long>()) } returns Optional.empty()

            // When
            val exception = shouldThrow<CustomException> {
                petFairQueryService.readDetailPetFair(petFairId)
            }

            // Then
            verify(exactly = 1) { petFairQueryDSLRepository.findActiveById(petFairId) }

            "게시글 없는 ErrorCode" {
                val errorCode = exception.errorCode

                errorCode.httpStatus() shouldBeEqual HttpStatus.NOT_FOUND
                errorCode.code() shouldBeEqual  "NOT_FOUND_PET_FAIR_POST"
                errorCode.message() shouldBeEqual "게시글이 존재하지 않습니다."
            }
        }
    }

    beforeTest {
        petFairQueryService = mockk(relaxed = true)
        petFairQueryDSLRepository = mockk(relaxed = true)
        petFairQueryMapper = mockk(relaxed = true)

        petFairQueryService = PetFairQueryService(petFairQueryDSLRepository, petFairQueryMapper)
    }
})