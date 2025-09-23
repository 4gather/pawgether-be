package com.example.pawgetherbe.comment

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.command.CommentCommandApi
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.exception.command.CommentCommandErrorCode
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode
import com.example.pawgetherbe.exception.command.UserCommandErrorCode
import com.example.pawgetherbe.usecase.comment.DeleteCommentUseCase
import com.example.pawgetherbe.usecase.comment.EditCommentUseCase
import com.example.pawgetherbe.usecase.comment.RegistryCommentUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.kotest.core.spec.style.FreeSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ActiveProfiles("test")
class CommentCommandApiTest: FreeSpec({
    lateinit var mockMvc: MockMvc
    lateinit var commentCommandApi: CommentCommandApi
    lateinit var registryCommentUseCase: RegistryCommentUseCase
    lateinit var editCommentUseCase: EditCommentUseCase
    lateinit var deleteCommentUseCase: DeleteCommentUseCase
    lateinit var objectMapper: ObjectMapper

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

            every { registryCommentUseCase.createComment(any(), any()) } returns commentCreateResponse

            // when
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/comments/{petfairId}",1L)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(commentCreateRequest))
            ).andExpect(status().isOk)

            // then
            verify(exactly = 1) { registryCommentUseCase.createComment(any(), any()) }
        }
        "댓글 생성 실패" - {
            var commentCreateRequest = CommentCommandDto.CommentCreateRequest(
                "test"
            )
            "User 정보 없을 경우" {
                // given
                every { registryCommentUseCase.createComment(any(), any()) } throws CustomException(UserCommandErrorCode.NOT_FOUND_USER)

                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/comments/{petfairId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_USER"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 계정입니다."))
            }
            "petFair 없을 경우" {
                // given
                every { registryCommentUseCase.createComment(any(), any()) } throws CustomException(PetFairCommandErrorCode.NOT_FOUND_PET_FAIR)

                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/comments/{petfairId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_PET_FAIR"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("해당 펫페어가 없습니다."))
            }
            "댓글 생성중 실패할 경우" {
                // given
                every { registryCommentUseCase.createComment(any(), any()) } throws CustomException(CommentCommandErrorCode.CREATE_INTERNAL_COMMENT)

                // when
                mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/comments/{petfairId}",1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(commentCreateRequest))
                ).andExpect(status().is5xxServerError)
                    .andExpect(jsonPath("$.code").value("CREATE_INTERNAL_COMMENT"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .andExpect(jsonPath("$.message").value("댓글 생성중 오류가 발생했습니다."))
            }
        }
    }

    "댓글 수정" - {
        "댓글 수정 성공" {
            // given
            var commentUpdateRequest = CommentCommandDto.CommentUpdateRequest(
                "test"
            )

            var commentUpdateResponse = CommentCommandDto.CommentUpdateResponse(
                1L,
                1L,
                1L,
                commentUpdateRequest.content,
                "",
                "",
                0
            )

            every { editCommentUseCase.updateComment(any(), any(), any()) } returns commentUpdateResponse

            // when
            mockMvc.perform(
                MockMvcRequestBuilders.patch("/api/v1/comments/{petfairId}/{commentId}",1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(commentUpdateRequest))
            ).andExpect(status().isOk)

            // then
            verify(exactly = 1) { editCommentUseCase.updateComment(any(), any(), any()) }
        }

        "댓글 수정 실패" - {
            var commentUpdateRequest = CommentCommandDto.CommentUpdateRequest(
                "test"
            )
            "user 정보가 없을 경우" {
                // given
                every { editCommentUseCase.updateComment(any(), any(), any()) } throws CustomException(UserCommandErrorCode.NOT_FOUND_USER)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.patch("/api/v1/comments/{petfairId}/{commentId}",1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(commentUpdateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_USER"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 계정입니다."))
            }
            "petFair 가 없을 경우" {
                // given
                every { editCommentUseCase.updateComment(any(), any(), any()) } throws CustomException(PetFairCommandErrorCode.NOT_FOUND_PET_FAIR)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.patch("/api/v1/comments/{petfairId}/{commentId}",1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(commentUpdateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_PET_FAIR"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("해당 펫페어가 없습니다."))
            }
        }
    }

    "댓글 삭제" - {
        "댓글 삭제 성공" {
            // given
            every { deleteCommentUseCase.deleteComment(any(), any()) } just Runs

            // when
            mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/v1/comments/{petfairId}/{commentId}",1L, 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
            ).andExpect(status().isNoContent)

            // then
            verify(exactly = 1) { deleteCommentUseCase.deleteComment(any(), any()) }
        }
        "댓글 삭제 실패" - {
            "user 정보가 없을 경우" {
                // given
                every { deleteCommentUseCase.deleteComment(any(), any()) } throws CustomException(UserCommandErrorCode.NOT_FOUND_USER)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/comments/{petfairId}/{commentId}",1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_USER"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 계정입니다."))
            }
            "petFair 가 없을 경우" {
                // given
                every { deleteCommentUseCase.deleteComment(any(), any()) } throws CustomException(PetFairCommandErrorCode.NOT_FOUND_PET_FAIR)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/comments/{petfairId}/{commentId}",1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_PET_FAIR"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("해당 펫페어가 없습니다."))
            }
            "이미 댓글이 삭제된 경우" {
                // given
                every { deleteCommentUseCase.deleteComment(any(), any()) } throws CustomException(CommentCommandErrorCode.DELETE_CONFLICT_COMMENT)

                // when, then
                mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/comments/{petfairId}/{commentId}",1L, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("DELETE_CONFLICT_COMMENT"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.message").value("이미 삭제된 댓글입니다."))
            }
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)
        every { UserContext.getUserId() } returns "1"

        registryCommentUseCase = mockk()
        editCommentUseCase = mockk()
        deleteCommentUseCase = mockk()

        commentCommandApi = CommentCommandApi(
            registryCommentUseCase,
            editCommentUseCase,
            deleteCommentUseCase
        )

        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        val converter = MappingJackson2HttpMessageConverter(objectMapper)

        mockMvc = MockMvcBuilders.standaloneSetup(commentCommandApi)
            .setControllerAdvice(GlobalExceptionHandler())
            .setMessageConverters(converter)
            .build()
    }
    afterTest {
        unmockkStatic(UserContext::class)
    }
})