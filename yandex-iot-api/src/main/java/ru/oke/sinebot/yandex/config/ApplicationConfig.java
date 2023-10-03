package ru.oke.sinebot.yandex.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ApplicationPropertiesProvider.class)
public class ApplicationConfig {
}
