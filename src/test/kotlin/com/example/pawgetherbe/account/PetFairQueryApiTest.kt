package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.query.PetFairQueryApi
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCountByStatusResponse
import com.example.pawgetherbe.domain.status.PetFairFilterStatus
import com.example.pawgetherbe.domain.status.PetFairStatus
import com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POST
import com.example.pawgetherbe.usecase.post.CountPostsUseCase
import com.example.pawgetherbe.usecase.post.ReadPostByIdUseCase
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.Instant
import java.time.LocalDate

@WebMvcTest(controllers = [PetFairQueryApi::class])
@ContextConfiguration(classes = [
    PetFairQueryApi::class,
    PetFairQueryApiTest.TestConfig::class
])
@Import(GlobalExceptionHandler::class)
class PetFairQueryApiTest {

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var readPostByIdUSeCase: ReadPostByIdUseCase
    @Autowired lateinit var countPostsUseCase: CountPostsUseCase
    @Autowired lateinit var radPostsUseCase: ReadPostsUseCase

    @TestConfiguration
    class TestConfig {
        @Bean fun readPostByIdUseCase(): ReadPostByIdUseCase = mock()
        @Bean fun countPostsUseCase(): CountPostsUseCase = mock()
        @Bean fun readPostsUseCase(): ReadPostsUseCase = mock()
    }

    @BeforeEach
    fun setUp() {
        Mockito.reset(readPostByIdUSeCase)
    }

    @Test
    fun `(2xx) 단건 조회 성공`() {
        // Given
        val petFairId = 1L
        val userId = 1L
        val localDateNow = LocalDate.now()
        val instantNow = Instant.now()
        val imageDtoList = listOf(
            "images/content/2025/05/0515-1.webp",
            "images/content/2025/05/0515-2.webp"
        )

        whenever(readPostByIdUSeCase.readDetailPetFair(petFairId)).thenReturn(
            DetailPetFairResponse(
                petFairId,
                userId,
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
        )

        // When & Then
        mockMvc.get("/api/v1/petfairs/{petfairId}", petFairId) {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                jsonPath("$.petFairId") { value(petFairId) }
                jsonPath("$.userId") { value(userId) }
                jsonPath("$.title") { value("2025 메가주 일산(상) 1") }
                jsonPath("$.images[0].imageUrl") { value("images/content/2025/05/0515-1.webp")}
            }
        }
    }

    @Test
    fun `(4xx) 존재하지 않는 게시글 조회`() {
        val petFairId = 1L

        whenever(readPostByIdUSeCase.readDetailPetFair(petFairId))
            .thenThrow(
                CustomException(NOT_FOUND_PET_FAIR_POST)
            )

        mockMvc.get("/api/v1/petfairs/{petfairId}", petFairId) {
        }.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `(4xx) 입력된 게시글 Id Type이 다름`() {
        val petFairId = "id"

        mockMvc.get("/api/v1/petfairs/{petfairId}", petFairId) {
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @ParameterizedTest(name = "{0} 상태 테스트")
    @EnumSource(PetFairFilterStatus::class)
    fun `(2xx) 게시글 상태에 따른 count 조회`(status: PetFairFilterStatus) {

        val expectedCount = when (status) {
            PetFairFilterStatus.PET_FAIR_ALL -> 1L
            PetFairFilterStatus.PET_FAIR_ACTIVE -> 2L
            PetFairFilterStatus.PET_FAIR_FINISHED -> 3L
        }

        whenever(countPostsUseCase.countActiveByFilterStatus(status))
            .thenReturn(PetFairCountByStatusResponse(status, expectedCount))

        mockMvc.get("/api/v1/petfairs/count/{filterStatus}", status)
            .andExpect {
                status { isOk() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    jsonPath("$.status") { value(status.name) }
                    jsonPath("$.count") { value(expectedCount) }
                }
            }
    }

    // TODO: Expected: 400, Actual: 404 ??? -> @PathVariable 은 null 을 받지 않고 바로 Exception
//    @Test
//    fun `(4xx) filter 상태 미입력`() {
//        val invalidFilterStatus = null;
//
//        mockMvc.get("/api/v1/petfairs/count/{filterStatus}", invalidFilterStatus)
//            .andExpect {
//                status { isBadRequest() }
//                content {
//                    contentType(MediaType.APPLICATION_JSON)
//                    jsonPath("$.status") { value(HttpStatus.BAD_REQUEST)}
//                    jsonPath("$.code") { value("VALIDATION_FAILED")}
//                }
//            }
//    }

    @Test
    fun `(2xx) 목록 조회`() {

        mockMvc.get("/api/v1/petfairs")
            .andExpect {  {
                status { isOk() }

            } }

    }

    @Test
    fun `(2xx) 검색 조회`() {

    }
}