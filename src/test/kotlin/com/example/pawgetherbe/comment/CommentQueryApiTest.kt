package com.example.pawgetherbe.comment

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.query.CommentQueryApi
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.exception.query.PetFairQueryErrorCode
import com.example.pawgetherbe.usecase.comment.ReadCommentsUseCase
import io.kotest.core.spec.style.FreeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ActiveProfiles("test")
class CommentQueryApiTest: FreeSpec({
    lateinit var mockMvc: MockMvc
    lateinit var commentQueryApi: CommentQueryApi
    lateinit var readCommentsUseCase: ReadCommentsUseCase

    "댓글 조회" - {
        "댓글 조회 성공" {
            // given
            var readCommentDtos = listOf(
                CommentQueryDto.ReadCommentDto(1L, 1L, "abc", "test", "", "", 0),
                CommentQueryDto.ReadCommentDto(2L, 1L, "abc", "test", "", "", 0),
                CommentQueryDto.ReadCommentDto(3L, 1L, "abc", "test", "", "", 0)
            )

            var readCommentResponse = CommentQueryDto.ReadCommentResponse(
                false,
                0,
                readCommentDtos
            )

            every { readCommentsUseCase.readComments(any(), any()) } returns readCommentResponse

            // when
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/comments/{petfairId}",1L)
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.hasMore").value(false))
                .andExpect(jsonPath("$.nextCursor").value(0))


            // then
            verify(exactly = 1) { readCommentsUseCase.readComments(any(), any()) }
        }
        "댓글 조회 실패" - {
            "petFair 없을 경우" {
                // given
                every { readCommentsUseCase.readComments(any(), any()) } throws CustomException(PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_CALENDAR)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comments/{petfairId}",1L)
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_PET_FAIR_CALENDAR"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("펫페어 행사가 없습니다."))
            }
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)
        every { UserContext.getUserId() } returns "1"

        readCommentsUseCase = mockk()

        commentQueryApi = CommentQueryApi(
            readCommentsUseCase
        )

        mockMvc = MockMvcBuilders.standaloneSetup(commentQueryApi)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }
    afterTest {
        unmockkStatic(UserContext::class)
    }
})