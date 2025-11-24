package com.example.pawgetherbe.common.filter.dto;

import org.springframework.http.HttpMethod;

public record ExcludeRule(
        HttpMethod method,
        String pattern
) {}
