package com.example.pawgetherbe.patfair

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.query.PetFairMainQueryApi
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase
import io.kotest.core.spec.style.FreeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POSTER
import org.springframework.http.HttpStatus

@ActiveProfiles("test")
class PetFairMainQueryApiTest: FreeSpec({

    lateinit var mockMvc: MockMvc
    lateinit var readPostsUseCase: ReadPostsUseCase
    lateinit var petFairMainQueryApi: PetFairMainQueryApi

    beforeTest {
        readPostsUseCase = mockk()

        petFairMainQueryApi = PetFairMainQueryApi(readPostsUseCase)

        mockMvc = MockMvcBuilders.standaloneSetup(petFairMainQueryApi)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    "Carousel 조회" - {
        "Carousel 조회 성공" {
            val petFairImages = listOf(
                PetFairQueryDto.PetFairPosterDto(1L, "images/poster/2025/05/0505.webp"),
                PetFairQueryDto.PetFairPosterDto(2L, "images/poster/2025/07/0715.webp"),
                PetFairQueryDto.PetFairPosterDto(3L, "images/poster/2025/08/0805.webp")
            )
            val petFairCarouselResponse = PetFairQueryDto.PetFairCarouselResponse(petFairImages)

            every { readPostsUseCase.petFairCarousel() } returns petFairCarouselResponse

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/main/carousel"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.petFairImages[0].petFairId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petFairImages[1].posterImageUrl").value("images/poster/2025/07/0715.webp"))

            verify(exactly = 1) { readPostsUseCase.petFairCarousel() }
        }

        "Carousel 조회 없음" {
            every { readPostsUseCase.petFairCarousel() } throws CustomException(NOT_FOUND_PET_FAIR_POSTER)

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/main/carousel"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND_PET_FAIR_POSTER"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("펫페어 포스터가 없습니다."))

            verify(exactly = 1) { readPostsUseCase.petFairCarousel() }
        }
    }
})