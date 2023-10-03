package ru.oke.sinebot.yandex.service.component;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Component;

/**
 * Компонент внедряется в Feign клиент и выполняет функционал добавления текущего аутентифицированного OAuth2 токена
 * в запросы к нижестоящему микросервису.
 * Если когда-то захочется запускать Feign в каком-нибудь пуле потоков, например, resilence4j, то это надо будет
 * переделать
 *
 * @author k.oshoev
 */
@Component
public class OAuth2TokenRequestInterceptor implements RequestInterceptor {
    private static final String HEADER = "Authorization";

    private static final String TOKEN_HEADER_PREFIX = "Bearer ";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof BearerTokenAuthentication bearerAuth) {
            if (!requestTemplate.headers().containsKey(HEADER)) {
                String accessToken = bearerAuth.getToken().getTokenValue();
                requestTemplate.header(HEADER, TOKEN_HEADER_PREFIX + accessToken);
            }
        }
    }
}
