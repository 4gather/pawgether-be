package com.example.pawgetherbe.account
import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.dto.UserDto
import com.example.pawgetherbe.controller.dto.UserDto.UserSignUpRequest
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.domain.status.UserStatus
import com.example.pawgetherbe.mapper.UserMapper
import com.example.pawgetherbe.repository.OauthRepository
import com.example.pawgetherbe.repository.UserRepository
import com.example.pawgetherbe.service.UserService
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
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.server.ResponseStatusException
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class UserServiceTest: FreeSpec({
    lateinit var userRepository: UserRepository
    lateinit var oauthRepository: OauthRepository
    lateinit var userMapper: UserMapper
    lateinit var redisTemplate: RedisTemplate<String, String>
    lateinit var jwtUtil: JwtUtil
    lateinit var oauthConfig: OauthConfig
    lateinit var userService: UserService

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

            every { userMapper.toUserEntity(signUpRequest) } returns entity
            every { userRepository.save(any()) } returns savedEntity

            // When
            userService.signUp(signUpRequest)

            // Then
            val slot = slot<UserEntity>()
            verify { userRepository.save(capture(slot)) }

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

            every { userMapper.toUserEntity(signUpRequest) } returns entity
            every { userRepository.existsByEmail("test@example.com") } returns true

            // When & Then
            val exception = shouldThrow<ResponseStatusException> {
                userService.signUp(signUpRequest)
            }

            exception.statusCode shouldBe HttpStatus.CONFLICT
            exception.reason shouldBe "이미 가입된 계정입니다"

            verify(exactly = 0) { userRepository.save(any()) }
        }
    }

    "이메일 중복 검사" - {
        "이메일 중복되지 않을떄" {
            val email = "test@example.com"

            every { userRepository.existsByEmail(email) } returns false

            userService.signupEmailCheck(email)
        }

        "중복 이메일이면 예외 발생" {
            val email = "test@example.com"

            every { userRepository.existsByEmail(email) } returns true

            val exception = shouldThrow<ResponseStatusException> {
                userService.signupEmailCheck(email)
            }

            exception.statusCode shouldBe HttpStatus.CONFLICT
            exception.reason shouldBe email + " 이미 가입된 계정입니다."
        }
    }

    "닉네임 중복 검사" - {
        "닉네임이 중복되지 않을때" {
            val nickName = "tester"

            every { userRepository.existsByNickName(nickName) } returns false

            userService.signupNicknameCheck(nickName)
        }

        "중복 닉네임이면 예외 발생" {
            val nickName = "tester"

            every { userRepository.existsByNickName(nickName) } returns true

            val exception = shouldThrow<ResponseStatusException> {
                userService.signupNicknameCheck(nickName)
            }

            exception.statusCode shouldBe HttpStatus.CONFLICT
            exception.reason shouldBe nickName + " 이미 존재하는 닉네임입니다."
        }
    }

    "회원 탈퇴" - {
        "이메일 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { UserContext.getUserId() } returns "1"
            every { oauthRepository.existsByUser_Id(id) } returns false
            every { userRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userService.deleteAccount(refreshToken)

            verify { userRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }

        "간편 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { oauthRepository.existsByUser_Id(id) } returns true
            every { oauthRepository.deleteByUser_Id(id) } just Runs
            every { userRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userService.deleteAccount(refreshToken)

            verify { oauthRepository.deleteByUser_Id(1L) }
            verify { userRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }
    }

    "로그아웃" - {
        "refreshToken 삭제 성공" {
            val refreshToken = "refreshToken123"

            every { redisTemplate.delete(refreshToken) } returns true

            userService.signOut(refreshToken)

            verify { redisTemplate.delete(refreshToken) }
        }
        "refreshToken 삭제 실패" {
            val refreshToken = "refreshToken123"
            every { redisTemplate.delete(refreshToken) } returns false

            userService.signOut(refreshToken)

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
            val refreshToken = "refreshToken123"
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

            every { userRepository.findById(id) } returns Optional.of(user)
            every { jwtUtil.generateAccessToken(any()) } returns "newAccessToken"
            every { EncryptUtil.generateRefreshToken() } returns "newRefreshToken"
            every { redisTemplate.delete(refreshToken) } returns true

            val result = userService.updateUserInfo(
                UserDto.UpdateUserRequest(newNickname, newUserImg),
                refreshToken
            )
            verify {
                userRepository.findById(id)
                user.updateProfile(newNickname, newUserImg)
                redisTemplate.delete(refreshToken)
                jwtUtil.generateAccessToken(
                    withArg { dto ->
                        dto.id shouldBe user.id
                        dto.role shouldBe user.role
                    }
                )
            }
            result.accessToken shouldBe "newAccessToken"
            result.refreshToken shouldBe "newRefreshToken"
            result.userImg shouldBe newUserImg
            result.nickName shouldBe newNickname
        }
        "업데이트 실패" {
            mockkStatic(UserContext::class)
            mockkStatic(EncryptUtil::class)

            every { UserContext.getUserId() } returns "1"
            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"
            val newNickname = "newNick"
            val newUserImg = "newImgUrl"

            every { userRepository.findById(id) } returns Optional.empty()

            val exception = shouldThrow<ResponseStatusException> {
                userService.updateUserInfo(
                    UserDto.UpdateUserRequest(newNickname, newUserImg),
                    refreshToken
                )
            }

            verify(exactly = 0) { redisTemplate.delete(refreshToken) }
            exception.statusCode shouldBe HttpStatus.NOT_FOUND
            exception.reason shouldBe " 존재하지 않는 계정입니다."
        }
    }

    beforeTest {
        userRepository = mockk(relaxed = true)
        oauthRepository = mockk(relaxed = true)
        userMapper = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        jwtUtil = mockk(relaxed = true)
        oauthConfig = mockk(relaxed = true)

        userService = UserService(
            userRepository,
            oauthRepository,
            userMapper,
            redisTemplate,
            jwtUtil,
            oauthConfig
        )
    }
})
