package com.example.pawgetherbe.comment

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.CommentEntity
import com.example.pawgetherbe.domain.entity.PetFairEntity
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.CommentStatus
import com.example.pawgetherbe.exception.command.CommentCommandErrorCode
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode
import com.example.pawgetherbe.exception.command.UserCommandErrorCode
import com.example.pawgetherbe.mapper.command.CommentCommandMapper
import com.example.pawgetherbe.repository.command.CommentCommandRepository
import com.example.pawgetherbe.repository.command.PetFairCommandRepository
import com.example.pawgetherbe.repository.command.UserCommandRepository
import com.example.pawgetherbe.service.command.CommentCommandService
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
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class CommentCommandServiceTest: FreeSpec({
    lateinit var commentCommandService: CommentCommandService
    lateinit var commentCommandRepository: CommentCommandRepository
    lateinit var petFairCommandRepository: PetFairCommandRepository
    lateinit var userCommandRepository: UserCommandRepository
    lateinit var commentCommandMapper: CommentCommandMapper

    lateinit var user: UserEntity
    lateinit var petFair: PetFairEntity


    "댓글 생성" - {
        "댓글 생성 성공" {
            // given
            var commentCreateRequest = CommentCommandDto.CommentCreateRequest(
                "test"
            )

            var commentCreateResponse = CommentCreateResponse(
                1L,
                1L,
                1L,
                commentCreateRequest.content,
                "",
                "",
                0
            )
            val commentEntity = CommentEntity.builder()
                .id(1L)
                .content("test")
                .status(CommentStatus.ACTIVE)
                .build()

            every { petFairCommandRepository.findById(any()) } returns Optional.of(petFair)
            every { commentCommandMapper.toCreateEntity(any()) } returns commentEntity
            every { commentCommandMapper.toCreateResponse(any()) } returns commentCreateResponse
            every { commentCommandRepository.save(any()) } returns commentEntity

            // when
            var result = commentCommandService.createComment(1L, commentCreateRequest)

            // then
            result shouldBe commentCreateResponse
            verify(exactly = 1){ commentCommandRepository.save(any()) }
        }
        "댓글 생성 실패" - {
            var commentCreateRequest = CommentCommandDto.CommentCreateRequest(
                "test"
            )
            "User 정보 없을 경우" {
                // given
                every { userCommandRepository.findById(1L) } throws CustomException(UserCommandErrorCode.NOT_FOUND_USER)

                // when
                val exception = shouldThrow<CustomException> {commentCommandService.createComment(1L, commentCreateRequest)}

                // then
                exception.errorCode shouldBe UserCommandErrorCode.NOT_FOUND_USER
            }
            "petFair 없을 경우" {
                // given
                every { petFairCommandRepository.findById(any()) } throws CustomException(PetFairCommandErrorCode.NOT_FOUND_PET_FAIR)

                // when
                val exception = shouldThrow<CustomException> {commentCommandService.createComment(1L, commentCreateRequest)}

                // then
                exception.errorCode shouldBe PetFairCommandErrorCode.NOT_FOUND_PET_FAIR
            }
            "댓글 생성중 실패할 경우" {
                // given
                every { commentCommandRepository.save(any()) } throws CustomException(CommentCommandErrorCode.CREATE_INTERNAL_COMMENT)
                every { petFairCommandRepository.findById(any()) } returns Optional.of(petFair)

                // when
                val exception = shouldThrow<CustomException> {commentCommandService.createComment(1L, commentCreateRequest)}

                // then
                exception.errorCode shouldBe CommentCommandErrorCode.CREATE_INTERNAL_COMMENT
            }
        }
    }

    "댓글 업데이트" - {
        "댓글 업데이트 성공" {
            // given
            var commentUpdateRequest = CommentCommandDto.CommentUpdateRequest("test")
            var commentUpdateResponse = CommentCommandDto.CommentUpdateResponse(1L, 1L, 1L, "test2", "", "", 0)
            var commentEntity = CommentEntity.builder().content("테스트").build()

            every { commentCommandRepository.findById(any()) } returns Optional.of(commentEntity)
            every { commentCommandMapper.toCommentUpdateResponse(any()) } returns commentUpdateResponse

            // when
            var result = commentCommandService.updateComment(1L, 1L, commentUpdateRequest)

            // then
            result shouldBe commentUpdateResponse
            verify(exactly = 1){ commentCommandRepository.findById(any()) }
        }
        "댓글 업데이트 실패" - {
            var commentUpdateRequest = CommentCommandDto.CommentUpdateRequest("test")

            "user 정보가 없을 경우" {
                // given
                every { userCommandRepository.existsById(any()) } returns false

                // when
                var result = shouldThrow<CustomException> {commentCommandService.updateComment(1L, 1L, commentUpdateRequest)}

                // then
                result.errorCode shouldBe UserCommandErrorCode.NOT_FOUND_USER
            }
            "petFair 가 없을 경우" {
                // given
                every { petFairCommandRepository.existsById(any()) } returns false

                // when
                var result = shouldThrow<CustomException> {commentCommandService.updateComment(1L, 1L, commentUpdateRequest)}

                // then
                result.errorCode shouldBe PetFairCommandErrorCode.NOT_FOUND_PET_FAIR
            }
        }
    }

    "댓글 삭제" - {
        "댓글 삭제 성공" {
            // given
            var commentEntity = CommentEntity.builder().status(CommentStatus.ACTIVE).build()
            every { commentCommandRepository.findById(any()) } returns Optional.of(commentEntity)

            // when
            commentCommandService.deleteComment(1L, 1L)

            // then
            verify(exactly = 1) { commentCommandRepository.findById(any()) }
        }
        "댓글 삭제 실패" - {
            "user 정보가 없을 경우" {
                // given
                every { userCommandRepository.existsById(any()) } returns false

                // when
                var result = shouldThrow<CustomException> {commentCommandService.deleteComment(1L, 1L)}

                // then
                result.errorCode shouldBe UserCommandErrorCode.NOT_FOUND_USER
            }
            "petFair 가 없을 경우" {
                // given
                every { petFairCommandRepository.existsById(any()) } returns false

                // when
                var result = shouldThrow<CustomException> {commentCommandService.deleteComment(1L, 1L)}

                // then
                result.errorCode shouldBe PetFairCommandErrorCode.NOT_FOUND_PET_FAIR
            }
            "이미 댓글이 삭제된 경우" {
                // given
                var commentEntity = CommentEntity.builder().status(CommentStatus.REMOVED).build()
                every { commentCommandRepository.findById(any()) } returns Optional.of(commentEntity)

                // when
                var result = shouldThrow<CustomException> {commentCommandService.deleteComment(1L, 1L)}

                // then
                result.errorCode shouldBe CommentCommandErrorCode.DELETE_CONFLICT_COMMENT
            }
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)

        user = mockk()
        petFair = mockk()

        petFairCommandRepository = mockk(relaxed = true)
        commentCommandMapper = mockk(relaxed = true)
        commentCommandRepository = mockk(relaxed = true)
        userCommandRepository = mockk(relaxed = true)

        every { UserContext.getUserId() } returns "1"
        every { userCommandRepository.findById(1L) } returns Optional.of(user)

        every { userCommandRepository.existsById(any()) } returns true
        every { petFairCommandRepository.existsById(any()) } returns true

        commentCommandService = CommentCommandService(
            commentCommandRepository,
            petFairCommandRepository,
            userCommandRepository,
            commentCommandMapper
        )
    }

    afterTest {
        unmockkStatic(UserContext::class)
    }
})