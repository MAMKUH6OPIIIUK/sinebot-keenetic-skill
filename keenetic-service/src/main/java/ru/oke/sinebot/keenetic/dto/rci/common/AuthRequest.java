package ru.oke.sinebot.keenetic.dto.rci.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.oke.sinebot.keenetic.service.router.keenetic.CachingKeeneticAuthServiceImpl;

/**
 * DTO для отправки на endpoint аутентификации роутера Keenetic. Пароль для данного объекта хешируется особым
 * образом на основе заголовков, получаемых от auth API роутера.
 * Детали см. {@link CachingKeeneticAuthServiceImpl}
 */
@AllArgsConstructor
@Getter
@Setter
public class AuthRequest {
    private String login;

    private String password;
}
