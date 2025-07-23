package com.example.pawgetherbe.config;

import com.example.pawgetherbe.common.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilterRegister() {
        FilterRegistrationBean<JwtAuthFilter> registration = new FilterRegistrationBean<JwtAuthFilter>();
        registration.setFilter(jwtAuthFilter);
        // registration.addUrlPatterns("/api/*"); // ✅ 인증이 필요한 경로 설정
        registration.setOrder(1); // 필터 순서 (낮을수록 먼저 실행)

        return registration;
    }
}
