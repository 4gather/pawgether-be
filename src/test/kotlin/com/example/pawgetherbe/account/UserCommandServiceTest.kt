package com.example.pawgetherbe.account
import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.command.dto.UserCommandDto
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UserSignUpRequest
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.domain.status.UserStatus
import com.example.pawgetherbe.mapper.command.UserCommandMapper
import com.example.pawgetherbe.repository.command.OauthCommandRepository
import com.example.pawgetherbe.repository.command.UserCommandRepository
import com.example.pawgetherbe.service.command.UserCommandService
import com.example.pawgetherbe.util.EncryptUtil
import com.example.pawgetherbe.util.EncryptUtil.passwordEncode
import com.example.pawgetherbe.util.JwtUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class UserCommandServiceTest: FreeSpec({
    lateinit var userCommandRepository: UserCommandRepository
    lateinit var oauthCommandRepository: OauthCommandRepository
    lateinit var userCommandMapper: UserCommandMapper
    lateinit var redisTemplate: RedisTemplate<String, String>
    lateinit var jwtUtil: JwtUtil
    lateinit var oauthConfig: OauthConfig
    lateinit var userCommandService: UserCommandService

    "회원가입" - {
        "정상적으로 가입" {
            // Given
            val signUpRequest = UserSignUpRequest(
                "tester",
                "test@example.com",
                "password123!"
            )
            val entity = UserEntity.builder()
                .nickName("tester")
                .email("test@example.com")
                .password("password123")
                .build()

            val savedEntity = entity.toBuilder()
                .password(passwordEncode(entity.password))
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER_EMAIL)
                .build()

            every { userCommandMapper.toUserEntity(signUpRequest) } returns entity
            every { userCommandRepository.save(any()) } returns savedEntity

            // When
            userCommandService.signUp(signUpRequest)

            // Then
            val slot = slot<UserEntity>()
            verify { userCommandRepository.save(capture(slot)) }

            val savedUser = slot.captured
            savedUser.email shouldBe "test@example.com"
            savedUser.nickName shouldBe "tester"
            savedUser.status shouldBe UserStatus.ACTIVE
            savedUser.role shouldBe UserRole.USER_EMAIL
        }

        "이메일 중복으로 가입 실패" {
            // Given
            val signUpRequest = UserSignUpRequest(
                "tester",
                "test@example.com",
                "password123!"
            )
            val entity = UserEntity.builder()
                .nickName("tester")
                .email("test@example.com")
                .password("password123")
                .build()

            every { userCommandMapper.toUserEntity(signUpRequest) } returns entity
            every { userCommandRepository.existsByEmail("test@example.com") } returns true

            // When & Then
            val exception = shouldThrow<RuntimeException> {
                userCommandService.signUp(signUpRequest)
            }

            verify(exactly = 0) { userCommandRepository.save(any()) }
        }
    }

    "회원 탈퇴" - {
        "이메일 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { UserContext.getUserId() } returns "1"
            every { oauthCommandRepository.existsByUser_Id(id) } returns false
            every { userCommandRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userCommandService.deleteAccount(refreshToken)

            verify { userCommandRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }

        "간편 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { oauthCommandRepository.existsByUser_Id(id) } returns true
            every { oauthCommandRepository.deleteByUser_Id(id) } just Runs
            every { userCommandRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userCommandService.deleteAccount(refreshToken)

            verify { oauthCommandRepository.deleteByUser_Id(1L) }
            verify { userCommandRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }
    }

    "로그아웃" - {
        "refreshToken 삭제 성공" {
            val refreshToken = "refreshToken123"

            every { redisTemplate.delete(refreshToken) } returns true

            userCommandService.signOut(refreshToken)

            verify { redisTemplate.delete(refreshToken) }
        }
        "refreshToken 삭제 실패" {
            val refreshToken = "refreshToken123"
            every { redisTemplate.delete(refreshToken) } returns false

            userCommandService.signOut(refreshToken)

            verify { redisTemplate.delete(refreshToken) }
        }
    }

    "회원 정보 수정" - {
        "nickname과 userImg가 업데이트" {
            mockkStatic(UserContext::class)
            mockkStatic(EncryptUtil::class)

            every { UserContext.getUserId() } returns "1"

            val newNickname = "newNick"
            val newUserImg = "newImgUrl"
            val id = UserContext.getUserId().toLong()
            val user = UserEntity.builder()
                .id(1L)
                .role(UserRole.USER_EMAIL)
                .status(UserStatus.ACTIVE)
                .nickName("tester")
                .userImg("/img/2025/05/05/202505.webp")
                .email("test@example.com")
                .password(passwordEncode("password123"))
                .build()

            val request = UserCommandDto.UpdateUserRequest(newNickname, newUserImg)
            val expectedResponse = UserCommandDto.UpdateUserResponse(newNickname, newUserImg)

            every { userCommandRepository.findById(id) } returns Optional.of(user)
            every { userCommandMapper.toUpdateUserResponse(request) } returns expectedResponse

            val result = userCommandService.updateUserInfo(request)
            verify {
                userCommandRepository.findById(id)
                user.updateProfile(newNickname, newUserImg)
                userCommandMapper.toUpdateUserResponse(request)
            }
            println("Result: $result")
            println("Expected nickName: $newNickname, Actual nickName: ${result.nickName}")
            println("Expected userImg: $newUserImg, Actual userImg: ${result.userImg}")
            result.nickName shouldBe newNickname
            result.userImg shouldBe newUserImg
        }
        "업데이트 실패" {
            mockkStatic(UserContext::class)
            mockkStatic(EncryptUtil::class)

            every { UserContext.getUserId() } returns "1"
            val id = UserContext.getUserId().toLong()
            val newNickname = "newNick"
            val newUserImg = "newImgUrl"

            every { userCommandRepository.findById(id) } returns Optional.empty()

            val exception = shouldThrow<RuntimeException> {
                userCommandService.updateUserInfo(
                    UserCommandDto.UpdateUserRequest(newNickname, newUserImg),
                )
            }

            exception.message shouldBe "존재하지 않는 계정입니다."
        }
    }

    beforeTest {
        userCommandRepository = mockk(relaxed = true)
        oauthCommandRepository = mockk(relaxed = true)
        userCommandMapper = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        jwtUtil = mockk(relaxed = true)
        oauthConfig = mockk(relaxed = true)

        userCommandService = UserCommandService(
            oauthCommandRepository,
            userCommandRepository,
            redisTemplate,
            jwtUtil,
            oauthConfig,
            userCommandMapper
        )
    }
})