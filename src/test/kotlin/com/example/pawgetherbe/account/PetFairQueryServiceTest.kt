package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCarouselResponse
import com.example.pawgetherbe.repository.query.PetFairQueryDSLRepository
import com.example.pawgetherbe.service.query.PetFairQueryService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class PetFairQueryServiceTest: FreeSpec ({
    lateinit var petFairQueryService: PetFairQueryService
    lateinit var petFairQueryDSLRepository: PetFairQueryDSLRepository

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

    beforeTest {
        petFairQueryService = mockk(relaxed = true)
        petFairQueryDSLRepository = mockk(relaxed = true)

        petFairQueryService = PetFairQueryService(petFairQueryDSLRepository)
    }
})