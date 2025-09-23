package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairRequest
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairResponse
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.PetFairEntity
import com.example.pawgetherbe.domain.entity.PetFairImageEntity
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.PetFairStatus
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.*
import com.example.pawgetherbe.mapper.command.PetFairCommandMapper
import com.example.pawgetherbe.repository.command.PetFairCommandRepository
import com.example.pawgetherbe.repository.command.UserCommandRepository
import com.example.pawgetherbe.service.command.PetFairCommandService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
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
    lateinit var updateRequest: UpdatePetFairRequest
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

    "updatePost" - {
        "2xx] 업데이트 성공" - {
            // given
            val updateResponse = UpdatePetFairResponse(
                1L,
                "test title",
                "test content",
                "imageUrl",
                "petFairUrl",
                "simpleAddress",
                "detailAddress",
                "2025-09-15",
                "2025-09-16",
                "02-1234-5678",
                listOf("images/2025/09/0905-1.webp", "images/2025/09/0905-2.webp"),
                0L,
                "2025-09-01",
                "2025-09-02"
            )
            val userEntity = UserEntity.builder().role(UserRole.ADMIN).build()
            val petFairEntity = PetFairEntity.builder()
                .user(userEntity)
                .build()

            every { UserContext.getUserRole() } returns UserRole.ADMIN.toString()
            every { petFairCommandRepository.findById(any<Long>()) } returns Optional.of(petFairEntity)
            every { entity.updatePetFair(any<String>(), any<List<PetFairImageEntity>>(), any<UpdatePetFairRequest>()) } just runs
            every { petFairCommandMapper.toUpdatePetFairResponse(any<PetFairEntity>()) } returns updateResponse

            every { updateRequest.startDate } returns "2025-09-05"
            every { updateRequest.endDate } returns "2025-09-06"
            every { updateRequest.posterImage() } returns webp("poster.webp", "poster")
            every { updateRequest.images() } returns listOf(webp("a.webp", "i1"), webp("b.webp", "i2"))

            // when
            val result = petFairCommandService.updatePetFairPost(1L, updateRequest)

            // then
            "startDate & endDate 업데이트 검증" {
                result.startDate shouldNotBe  null
                result.startDate shouldBe "2025-09-15"
                result.endDate shouldNotBe null
                result.endDate shouldBe "2025-09-16"
            }
            "images 업데이트 검증" {
                result.images shouldNotBe null
                result.images.size shouldBe 2
            }

        }

        "4xx] 요청 게시글이 없으면 예외" {
            // Given
            every { petFairCommandRepository.findById(any<Long>()) } returns Optional.empty()

            // When
            val exception = shouldThrow<CustomException> {
                petFairCommandService.updatePetFairPost(1L, updateRequest)
            }

            // Then
            exception.errorCode shouldBe NOT_FOUND_PET_FAIR
        }

        "4xx] 권한이 없으면 예외" {
            // Given
            every { petFairCommandRepository.findById(any<Long>()) } returns Optional.of(entity)
            every { UserContext.getUserRole() } returns UserRole.USER_EMAIL.toString()

            // When
            val exception = shouldThrow<CustomException> {
                petFairCommandService.updatePetFairPost(1L, updateRequest)
            }

            // Then
            exception.errorCode shouldBe UNAUTHORIZED_UPDATE_PET_FAIR
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)

        user = mockk()
        req = mockk(relaxed = true)
        updateRequest = mockk(relaxed = true)
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

    afterTest {
        unmockkStatic(UserContext::class)
    }
})