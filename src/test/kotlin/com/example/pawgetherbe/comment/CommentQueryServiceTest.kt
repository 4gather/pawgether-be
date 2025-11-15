package com.example.pawgetherbe.comment

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.MainCommentDto
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.MainCommentResponse
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.exception.query.CommentQueryErrorCode
import com.example.pawgetherbe.exception.query.PetFairQueryErrorCode
import com.example.pawgetherbe.repository.query.CommentQueryDSLRepository
import com.example.pawgetherbe.repository.query.PetFairQueryRepository
import com.example.pawgetherbe.service.query.CommentQueryService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension


@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class CommentQueryServiceTest: FreeSpec({
    lateinit var commentQueryService: CommentQueryService
    lateinit var petFairQueryRepository: PetFairQueryRepository
    lateinit var commentQueryDSLRepository: CommentQueryDSLRepository

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

            every { petFairQueryRepository.existsById(any()) } returns true
            every { commentQueryDSLRepository.readComments(any(), any()) } returns readCommentResponse

            // when
            var result = commentQueryService.readComments(1L, 1L)

            // then
            result shouldBe readCommentResponse
            verify(exactly = 1){ commentQueryDSLRepository.readComments(any(), any()) }
        }
        "댓글 조회 실패" - {
            "petFair 없을 경우" {
                // given
                every { petFairQueryRepository.existsById(any()) } returns false

                // when
                val exception = shouldThrow<CustomException> {commentQueryService.readComments(1L, 1L)}

                // then
                exception.errorCode shouldBe PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_CALENDAR
            }
        }
    }

    "메인 페이지 댓글 조회" - {
        "메인 페이지 댓글 조회 성공" {
            val mainCommentDto = listOf(
                MainCommentDto(1L, 1L, "abc", "test", "", "", 0),
                MainCommentDto(2L, 1L, "abc", "test", "", "", 0),
                MainCommentDto(3L, 1L, "abc", "test", "", "", 0)
            )
            val mainCommentResponse = MainCommentResponse(mainCommentDto)

            every { commentQueryDSLRepository.mainComments() } returns mainCommentResponse

            var result = commentQueryService.mainComments()

            result shouldBe mainCommentResponse
            verify(exactly = 1) { commentQueryDSLRepository.mainComments() }
        }

        "메인 페이지 댓글 조회 실패" {
            every { commentQueryDSLRepository.mainComments() } returns null

            val exception = shouldThrow<CustomException> {commentQueryService.mainComments()}

            exception.errorCode shouldBe CommentQueryErrorCode.NOT_FOUND_COMMENT_CALENDAR
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)

        petFairQueryRepository = mockk(relaxed = true)
        commentQueryDSLRepository = mockk(relaxed = true)

        commentQueryService = CommentQueryService(petFairQueryRepository, commentQueryDSLRepository)
    }

    afterTest {
        unmockkStatic(UserContext::class)
    }
})