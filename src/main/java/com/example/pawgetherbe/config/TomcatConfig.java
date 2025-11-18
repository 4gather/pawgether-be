package com.example.pawgetherbe.config;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> tomcatCustomizer() {
        return factory -> factory.addConnectorCustomizers(connector -> {
            int size = 200 * 1024 * 1024; // 200MB

            connector.setMaxPostSize(size);
            connector.setMaxPartHeaderSize(8 * 1024);

            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?> http) {
                http.setMaxSavePostSize(size);
                http.setMaxSwallowSize(-1);
            }
        });
    }
}
