package ru.oke.sinebot.yandex.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.server.resource.introspection.NimbusOpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.client.RestTemplate;
import ru.oke.sinebot.oauth.config.IntrospectionPropertiesProvider;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final IntrospectionPropertiesProvider introspectionProperties;

    @LoadBalanced
    @Bean(name = "ribbonRestTemplate")
    public RestTemplate ribbonRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        String clientId = introspectionProperties.getClientId();
        String clientSecret = introspectionProperties.getClientSecret();
        BasicAuthenticationInterceptor auth = new BasicAuthenticationInterceptor(clientId, clientSecret);
        restTemplate.getInterceptors().add(auth);
        return restTemplate;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::opaqueToken);
        return http.build();
    }

    @Bean
    OpaqueTokenIntrospector opaqueTokenIntrospector() {
        return new NimbusOpaqueTokenIntrospector(introspectionProperties.getIntrospectionEndpoint(),
                ribbonRestTemplate());
    }
}
