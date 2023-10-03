package ru.oke.sinebot.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers("/oauth2/authorize/**", "/oauth2/token/**", "/oauth2/token-info/**",
                        "/user/**").permitAll()
                .pathMatchers("/api/yandex/v1.0/**").permitAll()
                .anyExchange().authenticated()
                .and()
                .oauth2Login();
        return http.build();
    }
}
