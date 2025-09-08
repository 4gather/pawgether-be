package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.command.PetFairCommandApi
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.IMAGE_CONVERT_FAIL
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.PET_FAIR_CREATE_FAIL
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.REMOVED_PET_FAIR
import com.example.pawgetherbe.usecase.post.DeletePostUseCase
import com.example.pawgetherbe.usecase.post.RegistryPostUseCase
import io.kotest.core.spec.style.FreeSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ActiveProfiles("test")
class PetFairCommandApiTest: FreeSpec({
    lateinit var mockMvc: MockMvc
    lateinit var petFairApi: PetFairCommandApi
    lateinit var registryPostUseCase: RegistryPostUseCase
    lateinit var deletePostUseCase : DeletePostUseCase

    beforeTest {
        mockkStatic(UserContext::class)
        every { UserContext.getUserId() } returns "1"

        registryPostUseCase = mockk()
        deletePostUseCase = mockk()

        petFairApi = PetFairCommandApi(registryPostUseCase, deletePostUseCase)

        mockMvc = MockMvcBuilders.standaloneSetup(petFairApi)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    afterTest {
        unmockkStatic(UserContext::class)
    }

    "PetFairPostCreate" - {
        val poster = MockMultipartFile(
            "posterImage", "poster.webp", "image/webp", "poster-bytes".toByteArray()
        )
        val img1 = MockMultipartFile("images", "a.webp", "image/webp", "i1".toByteArray())
        val img2 = MockMultipartFile("images", "b.webp", "image/webp", "i2".toByteArray())

        val form = multipart("/api/v1/petfairs")
            .file("posterImage", poster.bytes)
            .file("images", img1.bytes)
            .file("images", img2.bytes)
            .param("title", "제목")
            .param("content", "내용")
            .param("simpleAddress", "서울시")
            .param("detailAddress", "강남구")
            .param("startDate", "2025-09-04")
            .param("endDate", "2025-09-05")
            .param("telNumber", "02-123-4567")
            .param("petFairUrl", "https://example.com")
            .characterEncoding("UTF-8")

        "생성 성공" {
            val resp = PetFairCommandDto.PetFairCreateResponse(
                1L,
                "제목",
                "내용",
                "poster/2025/09/0904.webp",
                "https://example.com",
                "강남구",
                "서울시 강남구",
                "2025-09-04",
                "2025-09-05",
                "02-123-4567",
                listOf("images/2025/09/0904-1.webp", "images/2025/09/0904-2.webp"),
                0L,
                "2025-09-05",
                "2025-09-05"

            )
            every { registryPostUseCase.postCreate(any<PetFairCommandDto.PetFairCreateRequest>()) } returns resp

            // when + then
            mockMvc.perform(form)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.petFairId").value(1))
                .andExpect(jsonPath("$.posterImageUrl").value("poster/2025/09/0904.webp"))
                .andExpect(jsonPath("$.images[0]").value("images/2025/09/0904-1.webp"))

            verify(exactly = 1) { registryPostUseCase.postCreate(any()) }
        }

        "이미지 변환 실패" {
            every { registryPostUseCase.postCreate(any<PetFairCommandDto.PetFairCreateRequest>()) } throws CustomException(
                IMAGE_CONVERT_FAIL
            )

            mockMvc.perform(form)
                .andExpect(status().is5xxServerError)
                .andExpect(jsonPath("$.code").value("IMAGE_CONVERT_FAIL"))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").value("이미지를 webp로 변환하는 중 오류가 발생했습니다."))

            verify(exactly = 1) { registryPostUseCase.postCreate(any()) }
        }

        "생성 실패" {
            every { registryPostUseCase.postCreate(any<PetFairCommandDto.PetFairCreateRequest>()) } throws CustomException(
                PET_FAIR_CREATE_FAIL
            )

            mockMvc.perform(form)
                .andExpect(status().is5xxServerError)
                .andExpect(jsonPath("$.code").value("CREATE_FAIL"))
                .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.message").value("펫페어 생성 실패"))

            verify(exactly = 1) { registryPostUseCase.postCreate(any()) }
        }
    }

    "PetFairPostDelete" - {
        "삭제 성공" {
            every { deletePostUseCase.deletePost(1L) } just Runs

            mockMvc.perform(delete("/api/v1/petfairs/{petfairId}", 1L))
                .andExpect(status().isOk)

            verify(exactly = 1) { deletePostUseCase.deletePost(1L) }
        }

        "삭제 실패" {
            every { deletePostUseCase.deletePost(1L) } throws CustomException(
                REMOVED_PET_FAIR
            )

            mockMvc.perform(delete("/api/v1/petfairs/{petfairId}", 1L))
                .andExpect(status().is4xxClientError)
                .andExpect(jsonPath("$.code").value("REMOVED_PET_FAIR"))
                .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(jsonPath("$.message").value("이미 삭제된 게시글입니다."))

            verify(exactly = 1) { deletePostUseCase.deletePost(1L) }
        }
    }
})