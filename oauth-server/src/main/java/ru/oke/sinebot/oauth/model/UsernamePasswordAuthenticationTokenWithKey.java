package ru.oke.sinebot.oauth.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Расширение для {@link UsernamePasswordAuthenticationToken}, позволяющее сохранить в информации об аутентификации
 * специфичный для пользователя ключ шифрования чувствительных данных.
 * Если данный класс инициализируется до того, как Spring Security выполнит очистку (erase) credentials после успешной
 * аутентификации, в качестве ключа шифрования можно задать, например, значение, сгенерированное на основе оригинального
 * введенного пользователем пароля
 *
 * @author k.oshoev
 */
@Getter
@Setter
public class UsernamePasswordAuthenticationTokenWithKey extends UsernamePasswordAuthenticationToken {
    private String encryptionKey;

    public UsernamePasswordAuthenticationTokenWithKey(Object principal, Object credentials,
                                                      Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
