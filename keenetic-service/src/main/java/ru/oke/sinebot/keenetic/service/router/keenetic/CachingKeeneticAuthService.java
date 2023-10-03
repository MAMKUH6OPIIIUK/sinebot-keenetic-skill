package ru.oke.sinebot.keenetic.service.router.keenetic;

public interface CachingKeeneticAuthService {
    String getSessionCookie(String domainName, String scheme);

    void evictSessionCookie(String domainName);
}
