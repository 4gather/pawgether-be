package com.example.pawgetherbe.patfair

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.query.PetFairMainQueryApi
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.MainCommentResponse
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto
import com.example.pawgetherbe.exception.query.CommentQueryErrorCode
import com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POSTER
import com.example.pawgetherbe.usecase.comment.ReadCommentsUseCase
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase
import io.kotest.core.spec.style.FreeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ActiveProfiles("test")
class PetFairMainQueryApiTest: FreeSpec({

    lateinit var mockMvc: MockMvc
    lateinit var readPostsUseCase: ReadPostsUseCase
    lateinit var petFairMainQueryApi: PetFairMainQueryApi
    lateinit var readCommentsUseCase : ReadCommentsUseCase

    beforeTest {
        readPostsUseCase = mockk()
        readCommentsUseCase = mockk()
        petFairMainQueryApi = PetFairMainQueryApi(readPostsUseCase, readCommentsUseCase)

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

    "Comment 목록 조회" - {
        "조회 성공" {
            val commentQueryDtoList = listOf(
                CommentQueryDto.MainCommentDto(1L, 1L, "abc", "cccc", "", "", 0),
                CommentQueryDto.MainCommentDto(2L, 2L, "def", "def", "", "", 0),
                CommentQueryDto.MainCommentDto(3L, 3L, "def", "def", "", "", 0)
            )
            val mainCommentResponse = MainCommentResponse(commentQueryDtoList)

            every { readCommentsUseCase.mainComments() } returns mainCommentResponse

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/main/comments"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.comments[0].commentId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comments[1].commentId").value(2L))

            verify(exactly = 1) { readCommentsUseCase.mainComments() }
        }

        "조회 실패" {
            every { readCommentsUseCase.mainComments() } throws CustomException(CommentQueryErrorCode.NOT_FOUND_COMMENT_CALENDAR)

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/main/comments"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND_COMMENT_CALENDAR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("댓글이 존재하지 않습니다."))

            verify(exactly = 1) { readCommentsUseCase.mainComments() }
        }
    }
})