package com.example.pawgetherbe.patfair

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.query.PetFairCalendarQueryApi
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto
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
import java.time.LocalDate
import com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_CALENDAR

@ActiveProfiles("test")
class PetFairCalendarQueryApiTest: FreeSpec({

    lateinit var mockMvc: MockMvc
    lateinit var petFairCalendarQueryApi: PetFairCalendarQueryApi
    lateinit var readPostsUseCase: ReadPostsUseCase

    beforeTest {
        readPostsUseCase = mockk()

        petFairCalendarQueryApi = PetFairCalendarQueryApi(readPostsUseCase)

        mockMvc = MockMvcBuilders.standaloneSetup(petFairCalendarQueryApi)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
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

            every { readPostsUseCase.petFairCalendar(any()) } returns petFairCalendarResponse

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/calendar")
                .param("date", "2025-05-05"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.petFairs[0].petFairId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.petFairs[1].posterImageUrl").value("images/poster/2025/07/0715.webp"))
            verify(exactly = 1) { readPostsUseCase.petFairCalendar(any()) }
        }
        "Calendar 조회 실패" {
            every { readPostsUseCase.petFairCalendar(any()) } throws CustomException(NOT_FOUND_PET_FAIR_CALENDAR)

            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/calendar")
                .param("date", "2025-05-05"))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND_PET_FAIR_CALENDAR"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("펫페어 행사가 없습니다."))

            verify(exactly = 1) { readPostsUseCase.petFairCalendar(any()) }
        }
    }
})