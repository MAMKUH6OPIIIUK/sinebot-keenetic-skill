package ru.oke.sinebot.configserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SuppressWarnings("unused")
@Configuration
public class SecurityConfig {

    /**
     * Настройка безопасности. Отключаем доступ к endpoint /decrypt для всех
     *
     * @param http настрока web безопасности
     * @return цепочка фильтров безопасности, в которой запрещен доступ к decrypt (эндпоинту для расшифровки защищенных
     * свойств приложений)
     * @throws Exception -
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/decrypt").denyAll()
                        .anyRequest().permitAll()
                )
                .build();
    }
}
