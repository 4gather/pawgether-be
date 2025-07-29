package com.example.pawgetherbe.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "pawgether.oauth")
@Getter
@Setter
public class OauthConfig {
    private Map<String, Provider> providers;

    @Getter
    @Setter
    public static class Provider {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private List<String> scope;
        private String authorizationUri;
        private String tokenUri;
        private String userInfoUri;
    }
}
