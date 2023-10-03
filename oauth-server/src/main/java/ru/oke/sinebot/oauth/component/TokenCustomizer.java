package ru.oke.sinebot.oauth.component;

import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenClaimsSet;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.stereotype.Component;
import ru.oke.sinebot.oauth.config.CustomClaimConstants;
import ru.oke.sinebot.oauth.model.Authority;
import ru.oke.sinebot.oauth.model.User;
import ru.oke.sinebot.oauth.model.UsernamePasswordAuthenticationTokenWithKey;

import java.util.stream.Collectors;

/**
 * Кастомизация токена.
 * Если первоначальная аутентификация для получения токена пройдена при помощи
 * {@link ru.oke.sinebot.oauth.component.provider.PasswordHashingDaoAuthenticationProvider}, то к набору claim,
 * содержащимся в токене, будет добавлен хэш введенного пользователем пароля, который может использоваться для
 * шифрования\дешифрования чувствительных данных (например, паролей от устройств пользователя)
 * Дополнительно будет добавлен идентификатор пользователя из БД и его granted authority
 *
 * @author k.oshoev
 */
@Component
public class TokenCustomizer implements OAuth2TokenCustomizer<OAuth2TokenClaimsContext> {
    @Override
    public void customize(OAuth2TokenClaimsContext context) {
        UsernamePasswordAuthenticationTokenWithKey principalWithKey = context.getPrincipal();
        User authenticatedUser = (User) principalWithKey.getPrincipal();
        OAuth2TokenClaimsSet.Builder claims = context.getClaims();
        claims.claim(CustomClaimConstants.KEY_USER_ID, authenticatedUser.getId());
        claims.claim(CustomClaimConstants.KEY_USER_ENCRYPTION_PASS, principalWithKey.getEncryptionKey());
        String scopeClaimValue = authenticatedUser.getAuthorities().stream()
                .map(Authority::getAuthority)
                .collect(Collectors.joining(" "));
        claims.claim("scope", scopeClaimValue);
    }
}
