package com.example.pawgetherbe.util;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public final class EncryptUtil {

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();

    //리프레쉬 토큰 발행
    public static String generateRefreshToken() {
        byte[] randomBytes = new byte[32]; // 256비트
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    // 비밀번호 암호화
    public static String passwordEncode(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // 비밀번호 일치 확인
    public static boolean matches(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
