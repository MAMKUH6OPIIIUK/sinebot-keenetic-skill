package ru.oke.sinebot.keenetic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.oke.sinebot.oauth.config.IntrospectionPropertiesProvider;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.oauth2.introspection")
public class ApplicationPropertiesProvider implements IntrospectionPropertiesProvider {
    private String clientId;

    private String clientSecret;

    private String introspectionEndpoint;
}
