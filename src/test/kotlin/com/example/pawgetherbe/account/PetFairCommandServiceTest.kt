package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.PetFairEntity
import com.example.pawgetherbe.domain.entity.PetFairImageEntity
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.PetFairStatus
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.IMAGE_CONVERT_FAIL
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.PET_FAIR_CREATE_FAIL
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.REMOVED_PET_FAIR
import com.example.pawgetherbe.mapper.command.PetFairCommandMapper
import com.example.pawgetherbe.repository.command.PetFairCommandRepository
import com.example.pawgetherbe.repository.command.UserCommandRepository
import com.example.pawgetherbe.service.command.PetFairCommandService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.IOException
import java.util.*


@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class PetFairCommandServiceTest: FreeSpec({

    lateinit var petFairCommandService: PetFairCommandService
    lateinit var userCommandRepository: UserCommandRepository
    lateinit var petFairCommandMapper: PetFairCommandMapper
    lateinit var petFairCommandRepository: PetFairCommandRepository
    lateinit var r2Client: S3Client

    lateinit var req: PetFairCommandDto.PetFairCreateRequest
    lateinit var user: UserEntity
    lateinit var entity: PetFairEntity

    fun webp(name: String, bytes: String) = mockk<MultipartFile>().also {
        every { it.originalFilename } returns name
        every { it.contentType } returns "image/webp"
        every { it.bytes } returns bytes.toByteArray()
    }

    fun brokenJpeg(name: String = "poster.jpg"): MultipartFile = mockk {
        every { originalFilename } returns name
        every { contentType } returns "image/jpeg"
        every { inputStream } throws IOException("boom")
    }

    "PetFairPostCreate" - {
        "post 생성 성공" {
            // given
            every { req.posterImage() } returns webp("poster.webp", "poster")
            every { req.images() } returns listOf(webp("a.webp", "i1"), webp("b.webp", "i2"))
            every { petFairCommandRepository.save(entity) } returns entity

            val resp = mockk<PetFairCommandDto.PetFairCreateResponse>()
            every { petFairCommandMapper.toPetFairCreateResponse(entity) } returns resp

            // when
            val result = petFairCommandService.postCreate(req)

            // then
            result shouldBe resp

            verify(exactly = 1) {
                r2Client.putObject(
                    match<PutObjectRequest> { it.key() == "poster/2025/09/0904.webp" },
                    any<RequestBody>()
                )
            }
            verify(exactly = 1) {
                r2Client.putObject(
                    match<PutObjectRequest> { it.key() == "images/2025/09/0904-1.webp" },
                    any<RequestBody>()
                )
            }
            verify(exactly = 1) {
                r2Client.putObject(
                    match<PutObjectRequest> { it.key() == "images/2025/09/0904-2.webp" },
                    any<RequestBody>()
                )
            }
            verify(exactly = 1) {
                entity.updateImage(
                    "poster/2025/09/0904.webp",
                    withArg<List<PetFairImageEntity>> { it.size shouldBe 2 },
                    PetFairStatus.ACTIVE,
                    user
                )
            }
            verify(exactly = 1) { petFairCommandRepository.save(entity) }
        }

        ".webp 변환 실패" {
            // given
            every { req.posterImage() } returns brokenJpeg()

            // when
            val exception = shouldThrow<CustomException> { petFairCommandService.postCreate(req) }

            // then
            exception.errorCode shouldBe IMAGE_CONVERT_FAIL

            verify(exactly = 0) { r2Client.putObject(any<PutObjectRequest>(), any<RequestBody>()) }
            verify(exactly = 0) { petFairCommandRepository.save(any()) }
        }

        "post 생성 실패" {
            // given
            every { req.posterImage() } returns webp("poster.webp", "poster")
            every { req.images() } returns listOf(webp("a.webp", "i1"), webp("b.webp", "i2"))
            every { petFairCommandRepository.save(entity) } throws RuntimeException("DB down")

            // when
            val exception = shouldThrow<CustomException> { petFairCommandService.postCreate(req) }

            // then
            exception.errorCode shouldBe PET_FAIR_CREATE_FAIL

            verify(atLeast = 2) { r2Client.putObject(any<PutObjectRequest>(), any<RequestBody>()) }
        }
    }

    "deletePost" - {
        "Post 정상 삭제" {
            // given
            every { entity.status } returns PetFairStatus.ACTIVE
            every { petFairCommandRepository.findById(1L) } returns Optional.of(entity)

            // when
            petFairCommandService.deletePost(1L)

            // then
            verify(exactly = 1) { entity.updateStatus(PetFairStatus.REMOVED) }
        }

        "Post 삭제 실패" {
            // given
            every { entity.status } returns PetFairStatus.REMOVED
            every { petFairCommandRepository.findById(1L) } returns Optional.of(entity)

            // when
            val exception = shouldThrow<CustomException> { petFairCommandService.deletePost(1L) }

            // then
            exception.errorCode shouldBe REMOVED_PET_FAIR
            verify(exactly = 0) { entity.updateStatus(PetFairStatus.REMOVED) }
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)

        user = mockk()
        req = mockk(relaxed = true)
        entity = mockk(relaxed = true)
        petFairCommandService = mockk(relaxed = true)
        userCommandRepository = mockk(relaxed = true)
        petFairCommandMapper = mockk(relaxed = true)
        petFairCommandRepository = mockk(relaxed = true)
        r2Client = mockk(relaxed = true)

        every { UserContext.getUserId() } returns "1"
        every { userCommandRepository.findById(1L) } returns Optional.of(user)
        every { petFairCommandMapper.toPetFairEntity(req) } returns entity
        every { req.startDate() } returns "2025-09-04"
        every { req.images() } returns emptyList()

        petFairCommandService = PetFairCommandService(
            petFairCommandRepository,
            userCommandRepository,
            petFairCommandMapper,
            r2Client
        )
    }
})