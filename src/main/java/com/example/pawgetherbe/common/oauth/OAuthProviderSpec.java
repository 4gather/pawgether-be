package com.example.pawgetherbe.common.oauth;

import com.github.scribejava.core.builder.api.DefaultApi20;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OAuthProviderSpec extends DefaultApi20 {

    private final String authorizationUrl;
    private final String tokenUrl;

    @Override
    public String getAccessTokenEndpoint() {
        return tokenUrl;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return authorizationUrl;
    }
}
