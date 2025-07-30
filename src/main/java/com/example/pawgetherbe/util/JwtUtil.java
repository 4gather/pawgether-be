package com.example.pawgetherbe.util;

import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.domain.status.AccessTokenStatus;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public final class JwtUtil {

    private final SecretKey signatureKey;

    public JwtUtil(@Value("${jwt.secret-key}") String secretKey) {
        this.signatureKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public static final long ACCESS_TOKEN_VALIDITY_MS = 1000L*60*15; // 15 분

    public String generateAccessToken(UserEntity user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
//                .claim("email", user.getEmail())
//                .claim("nickname", user.getNickName())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_MS))
                .signWith(signatureKey)
                .compact();
    }

    public AccessTokenStatus validateToken(String accessToken) {
        try {
            parseToken(accessToken);
            return AccessTokenStatus.VALID;
        } catch (ExpiredJwtException e) {
            return AccessTokenStatus.EXPIRED;
        } catch (Exception e) {
            return AccessTokenStatus.INVALID;
        }
    }

    private Jws<Claims> parseToken(String accessToken) {
        return Jwts.parser()
                .verifyWith(signatureKey)
                .build()
                .parseSignedClaims(accessToken);
    }

    public Long getUserIdFromToken(String accessToken) {
        return Long.parseLong(parseToken(accessToken).getPayload().getSubject());
    }

    public String getUserEmailFromToken(String accessToken) {
        return parseToken(accessToken).getPayload().get("email", String.class);
    }

    public String getUserNicknameFromToken(String accessToken) {
        return parseToken(accessToken).getPayload().get("nickname", String.class);
    }

    public String getUserRoleFromToken(String accessToken) {
        return parseToken(accessToken).getPayload().get("role", String.class);
    }
}
