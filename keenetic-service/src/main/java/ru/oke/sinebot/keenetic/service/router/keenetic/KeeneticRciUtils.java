package ru.oke.sinebot.keenetic.service.router.keenetic;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Утилитарный класс, отвечающий за формирование URI для обращения к различным endpoint rci API роутеров Keenetic
 *
 * @author k.oshoev
 */
public class KeeneticRciUtils {
    public static final String DEFAULT_PROTOCOL = "https";

    /**
     * Метод строит URI к endpoint, отвечающему за получение оперативной информации об интерфейсе устройства (в
     * частности, точке доступа)
     *
     * @param domainName FQDN роутера
     * @return URI для запроса по протоколу HTTPS
     */
    public static URI buildInterfaceInformationUrl(String domainName) {
        return UriComponentsBuilder.newInstance()
                .scheme(DEFAULT_PROTOCOL)
                .host(domainName)
                .path("rci/show/interface")
                .build().toUri();
    }

    /**
     * Метод строит URI к endpoint, отвечающему за управление интерфейсом устройства (в частности, точкой доступа)<p>
     * Позволяет выполнять конфигурационные операции и некоторые оперативные (например, запуск сессии WPS)
     *
     * @param domainName FQDN роутера
     * @return URI для запроса по протоколу HTTPS
     */
    public static URI buildInterfaceManagementUrl(String domainName) {
        return UriComponentsBuilder.newInstance()
                .scheme(DEFAULT_PROTOCOL)
                .host(domainName)
                .path("rci/interface")
                .build().toUri();
    }

    /**
     * Метод для построения URI к auth endpoint роутера с использованием переданного протокола
     *
     * @param domainName FQDN роутера, доступный из интернета
     * @param scheme     схема\протокол
     * @return URI для отправки GET и POST запросов для получения cookie аутентифицированной сессии на устройстве
     */
    public static URI buildAuthUri(String domainName, String scheme) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(domainName)
                .path("auth")
                .build().toUri();
    }

    /**
     * Метод для построения URI к endpoint управления системными функциями роутера (например, конфигурацией)
     *
     * @param domainName FQDN роутера
     * @return URI для запроса по протоколу HTTPS
     */
    public static URI buildSystemUri(String domainName) {
        return UriComponentsBuilder.newInstance()
                .scheme(DEFAULT_PROTOCOL)
                .host(domainName)
                .path("rci/system")
                .build().toUri();
    }
}
