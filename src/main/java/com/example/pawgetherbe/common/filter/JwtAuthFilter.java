package com.example.pawgetherbe.common.filter;

import com.example.pawgetherbe.common.exceptionHandler.dto.ErrorResponseDto;
import com.example.pawgetherbe.common.filter.dto.ExcludeRule;
import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.status.AccessTokenStatus;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

    public static final String AUTH_BEARER = "Bearer ";
    public static final String REQUEST_HEADER_AUTH = "Authorization";

    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String CHARSET_ENCODING_UTF8 = "UTF-8";

    private final JwtUtil jwtUtil;

    private final AntPathMatcher ant = new AntPathMatcher();
    private static final List<ExcludeRule> EXCLUDES = List.of(

            new ExcludeRule(HttpMethod.GET,  "/api/v1/main/*"),
            new ExcludeRule(HttpMethod.GET,  "/api/v1/calendar"),
            new ExcludeRule(HttpMethod.POST, "/api/v1/refresh"),

            new ExcludeRule(HttpMethod.GET,  "/api/v1/petfairs"),
            new ExcludeRule(HttpMethod.GET,  "/api/v1/petfairs/*"),

            new ExcludeRule(HttpMethod.POST, "/api/v1/account"),
            new ExcludeRule(HttpMethod.POST, "/api/v1/account/signup"),
            new ExcludeRule(HttpMethod.POST, "/api/v1/account/signup/*"),
            new ExcludeRule(HttpMethod.GET,  "/api/v1/account/oauth/naver"),
            new ExcludeRule(HttpMethod.GET,  "/api/v1/account/oauth/google"),

            new ExcludeRule(HttpMethod.GET,  "/api/v1/comments/*"),
            new ExcludeRule(HttpMethod.GET,  "/api/v1/comments/count/*"),

            new ExcludeRule(HttpMethod.GET,  "/api/v1/replies/*"),
            new ExcludeRule(HttpMethod.POST, "/api/v1/replies/count")
    );

    private boolean isExcluded(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        return EXCLUDES.stream().anyMatch(rule ->
                rule.method().matches(method) && ant.match(rule.pattern(), uri)
        );
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            String uri = request.getRequestURI();
            log.info("uri = {}",uri);
            if (isExcluded(request)) {
                filterChain.doFilter(request, response);
                return;
            }

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
        log.info("requestUserId = {}, requestUserRole = {}", requestUserId, requestUserRole);
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

        ErrorResponseDto errorResponse = new ErrorResponseDto(statusValue, code, message);

        response.getWriter().write(
                new ObjectMapper().writeValueAsString(errorResponse)
        );
    }
}
