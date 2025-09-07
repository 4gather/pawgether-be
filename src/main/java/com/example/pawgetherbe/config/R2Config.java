package com.example.pawgetherbe.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class R2Config {
    @Value("${cloudflare.r2.endpoint}")
    private String endpoint;

    @Value("${cloudflare.r2.access-key}")
    private String accessKey;

    @Value("${cloudflare.r2.secret-key}")
    private String secretKey;

    @Bean
    public S3Client r2Client() {
        return S3Client.builder()
                .region(Region.of("auto")) // R2는 region 고정
                .endpointOverride(URI.create(endpoint))
                .serviceConfiguration(
                        S3Configuration.builder()
                                // 버킷-서브도메인 방식 사용(권장)
                                .pathStyleAccessEnabled(false)
                                .build()
                )
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey.trim(), secretKey.trim())
                        )
                )
                .build();
    }
}
