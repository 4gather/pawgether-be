package com.example.pawgetherbe.config;

import com.example.pawgetherbe.common.filter.JwtAuthFilter;
import com.example.pawgetherbe.util.JwtUtil;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegister() {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<JwtAuthFilter>();
        registration.setFilter(new JwtAuthFilter(jwtUtil));
        registration.setDispatcherTypes(DispatcherType.REQUEST); // 한 번의 요청에 한 번만 Filter 실행
        registration.addUrlPatterns("/api/v2"); // ✅ 인증이 필요한 경로 설정
        registration.setOrder(1); // 필터 순서 (낮을수록 먼저 실행)

        return registration;
    }
}
