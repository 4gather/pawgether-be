package com.example.pawgetherbe.common.filter;

import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.status.AccessTokenStatus;
import com.example.pawgetherbe.exception.ApiErrorResponse;
import com.example.pawgetherbe.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

    private static final String REQUEST_HEADER_AUTH = "Authorization";
    private static final String AUTH_BEARER = "Bearer ";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARSET_ENCODING_UTF8 = "UTF-8";

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
                createJsonErrorMessage(response, HttpStatus.UNAUTHORIZED.value(), "ACCESS_TOKEN_EXPIRED", "인증이 만료되었습니다.");
                return;
            }
            // 2] 유효하지 않은 토큰 - 로그인 화면으로 이동 요구
            if (jwtUtil.validateToken(accessToken).equals(AccessTokenStatus.INVALID)) {
                createJsonErrorMessage(response, HttpStatus.UNAUTHORIZED.value(), "ACCESS_TOKEN_INVALID", "인증이 유효하지 않습니다.");
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
//        String requestUserEmail = jwtUtil.getUserEmailFromToken(accessToken);
        String requestUserRole = jwtUtil.getUserRoleFromToken(accessToken);
//        String requestUserNickname = jwtUtil.getUserNicknameFromToken(accessToken);

        UserContext.setUserId(requestUserId);
//        UserContext.setUserEmail(requestUserEmail);
        UserContext.setUserRole(requestUserRole);
//        UserContext.setUserNickname(requestUserNickname);
    }

    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(REQUEST_HEADER_AUTH);

        if (header == null || !header.startsWith(AUTH_BEARER)) {
            throw new IllegalArgumentException("인증을 다시 확인해 주세요.");
        }

        return header.substring(AUTH_BEARER.length());
    }

    private void createJsonErrorMessage(HttpServletResponse response, int statusValue, String code, String message) throws IOException {
        response.setStatus(statusValue);
        response.setContentType(CONTENT_TYPE_JSON);
        response.setCharacterEncoding(CHARSET_ENCODING_UTF8);

        ApiErrorResponse errorResponse = new ApiErrorResponse(statusValue, code, message);

        response.getWriter().write(
                new ObjectMapper().writeValueAsString(errorResponse)
        );
    }
}
