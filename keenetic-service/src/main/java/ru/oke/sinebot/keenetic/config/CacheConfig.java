package ru.oke.sinebot.keenetic.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
    public static final String KEENETIC_SESSIONS = "keeneticSessions";

    public static final String ACL_CACHE = "aclCache";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(KEENETIC_SESSIONS, ACL_CACHE);
    }
}
