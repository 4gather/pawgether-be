package com.example.pawgetherbe.common.filter;

import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.status.AccessTokenStatus;
import com.example.pawgetherbe.util.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

    public static final String REQUEST_HEADER_AUTH = "Authorization";
    public static final String AUTH_BEARER = "Bearer ";

    private final JwtUtil jwtUtil;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            String accessToken = getToken(request);

            // Signature 검증
            // 1] 만료 토큰 - 리프레시 토큰 요구
            if (jwtUtil.validateToken(accessToken).equals(AccessTokenStatus.EXPIRED)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "required refreshToken");
                return;
            }
            // 2] 유효하지 않은 토큰 - 로그인 화면으로 이동 요구
            if (jwtUtil.validateToken(accessToken).equals(AccessTokenStatus.INVALID)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Access Token");
                return;
            }

            processJwtToken(accessToken);

            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private void processJwtToken(String accessToken) {
        // JWT 토큰에서 정보 추출
        String requestUserId = String.valueOf(jwtUtil.getUserIdFromToken(accessToken));
        String requestUserEmail = jwtUtil.getUserEmailFromToken(accessToken);
        String requestUserRole = jwtUtil.getUserRoleFromToken(accessToken);
        String requestUserNickname = jwtUtil.getUserNicknameFromToken(accessToken);

        UserContext.setUserId(requestUserId);
        UserContext.setUserEmail(requestUserEmail);
        UserContext.setUserRole(requestUserRole);
        UserContext.setUserNickname(requestUserNickname);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(REQUEST_HEADER_AUTH);

        if (header == null || !header.startsWith(AUTH_BEARER)) {
            throw new IllegalArgumentException("인증을 다시 확인해 주세요.");
        }

        return header.substring(AUTH_BEARER.length());
    }
}
