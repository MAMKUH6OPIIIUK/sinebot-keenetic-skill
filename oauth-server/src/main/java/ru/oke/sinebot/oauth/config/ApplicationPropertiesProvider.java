package ru.oke.sinebot.oauth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.authorizationserver")
public class ApplicationPropertiesProvider implements AuthorizationServerPropertiesProvider {
    private String issuerUrl;

    private String introspectionEndpoint;
}