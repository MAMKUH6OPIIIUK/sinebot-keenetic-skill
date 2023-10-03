package ru.oke.sinebot.keenetic.service.router.keenetic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.oke.sinebot.keenetic.config.CacheConfig;
import ru.oke.sinebot.keenetic.dto.rci.common.AuthRequest;
import ru.oke.sinebot.keenetic.exception.NotFoundException;
import ru.oke.sinebot.keenetic.model.Device;
import ru.oke.sinebot.keenetic.repository.DeviceRepository;

import java.net.URI;

/**
 * Сервис, предоставляющий кешируемые методы для прохождения аутентификации в rci API роутера Keenetic
 *
 * @author k.oshoev
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CachingKeeneticAuthServiceImpl implements CachingKeeneticAuthService {
    private final DeviceRepository deviceRepository;

    private final RestTemplate authTemplate;

    /**
     * Метод для прохождения аутентификации на auth endpoint роутера Keenetic. Результаты кешируются и переиспользуются,
     * их необходимо выселять из кэша после истечения сессии
     *
     * @param domainName FQDN роутера Keenetic, доступного из интернета
     * @return Cookie аутентифицированной сессии. Может использоваться в запросах к rci API роутера в заголовке Cookie
     */
    @Override
    @Cacheable(value = CacheConfig.KEENETIC_SESSIONS, key = "#domainName", unless = "#result == null")
    public String getSessionCookie(String domainName, String scheme) {
        log.debug("Cookie аутентифицированной сессии для роутера \"" + domainName + "\" не найдены в кэше. Начинаем " +
                "процесс аутентификации");
        URI authEndpointUri = KeeneticRciUtils.buildAuthUri(domainName, scheme);
        try {
            authTemplate.getForObject(authEndpointUri, String.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.UNAUTHORIZED) && e.getResponseHeaders() != null) {
                return postAuthRequest(authEndpointUri, e.getResponseHeaders());
            }
        }
        return null;
    }

    /**
     * Метод для извлечения cookie сессии из кэша. Может вызываться, если сессия, связанная с cookie, истекла
     *
     * @param domainName FQDN роутера
     */
    @Override
    @CacheEvict(value = CacheConfig.KEENETIC_SESSIONS, key = "#domainName")
    public void evictSessionCookie(String domainName) {
        log.debug("Сессия для роутера \"" + domainName + "\" удалена из кэша");
    }

    /**
     * Метод отвечает за конструирование корректного POST запроса к auth endpoint роутера для прохождения аутентификации
     * сессии и отправку этого запроса
     *
     * @param authEndpointUri URI auth endpoint конкретного устройства
     * @param responseHeaders заголовки, полученные в ответ на GET запрос к auth endpoint
     * @return значение Cookie аутентифицированной сессии, либо null, если устройство не предоставило все необходимые
     * заголовки в ответе (например, если по URI отзывается устройство совсем другой модели)
     */
    private String postAuthRequest(URI authEndpointUri, HttpHeaders responseHeaders) {
        String saltHeader = responseHeaders.getFirst("X-NDM-Challenge");
        String realmHeader = responseHeaders.getFirst("X-NDM-Realm");
        String sessionCookieHeader = responseHeaders.getFirst("Set-Cookie");
        if (saltHeader != null && realmHeader != null && sessionCookieHeader != null) {
            Device device = this.deviceRepository.findByDomainName(authEndpointUri.getHost())
                    .orElseThrow(() -> new NotFoundException("Устройство по доменному имени " +
                            authEndpointUri.getHost() + " не найдено"));
            AuthRequest authRequestBody = this.buildAuthRequestBody(device.getLogin(), device.getPassword(), saltHeader,
                    realmHeader);
            HttpHeaders authRequestHeaders = new HttpHeaders();
            authRequestHeaders.setContentType(MediaType.APPLICATION_JSON);
            authRequestHeaders.add("Cookie", sessionCookieHeader);
            HttpEntity<AuthRequest> authRequest = new HttpEntity<>(authRequestBody, authRequestHeaders);
            authTemplate.exchange(authEndpointUri, HttpMethod.POST, authRequest, String.class);
            return sessionCookieHeader;
        }
        return null;
    }

    /**
     * Метод выполняет хэширование пароля от роутера на основе значений заголовков, получаемых в ответ на GET запрос
     * к auth endpoint роутера
     *
     * @param login       plain text логин от устройства
     * @param password    plain text пароль от устройства
     * @param saltHeader  значение заголовка X-NDM-Challenge
     * @param realmHeader значение заголовка X-NDM-Realm
     * @return dto для отправки POST-запросом на auth endpoint роутера для аутентификации
     */
    private AuthRequest buildAuthRequestBody(String login, String password, String saltHeader, String realmHeader) {
        String toMd5 = login + ":" + realmHeader + ":" + password;
        String md5Hash = DigestUtils.md5Hex(toMd5);
        String toSha256 = saltHeader + md5Hash;
        String passwordSaltedHash = DigestUtils.sha256Hex(toSha256.getBytes());
        return new AuthRequest(login, passwordSaltedHash);
    }


}
