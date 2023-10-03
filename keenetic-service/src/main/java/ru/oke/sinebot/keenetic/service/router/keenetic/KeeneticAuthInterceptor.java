package ru.oke.sinebot.keenetic.service.router.keenetic;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Перехватчик HTTP запросов для прохождения аутентификации на роутерах производителя Keenetic
 * Должен явным образом использоваться в RestTemplate, осуществляющем взаимодействие с оборудованием этого производителя
 *
 * @author k.oshoev
 */
@Component
@RequiredArgsConstructor
public class KeeneticAuthInterceptor implements ClientHttpRequestInterceptor {
    private final CachingKeeneticAuthService authService;

    /**
     * Метод при необходимости выполняет аутентификацию на хосте из запроса и добавляет к запросу заголовок Cookie
     * с информацией об аутентифицированной сессии. Если сессия уже была установлена и получена из кэша, то будет
     * использоваться она.
     * Предусмотрен ручной retry с выселением информации о сессии из кэша и повторной аутентификацией, если сессия
     * истекла
     *
     * @param request   объект HTTP запроса к роутеру
     * @param body      тело запроса
     * @param execution контекст выполнения запроса
     * @return результат выполнения запроса
     * @throws IOException в случае возникновения ошибок на I/O
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String sessionCookie = this.setSessionCookie(request);
        ClientHttpResponse response = execution.execute(request, body);
        if (HttpStatus.UNAUTHORIZED == response.getStatusCode() && sessionCookie != null) {
            this.authService.evictSessionCookie(request.getURI().getHost());
            this.setSessionCookie(request);
            response = execution.execute(request, body);
        }
        return response;
    }

    private String setSessionCookie(HttpRequest request) {
        String host = request.getURI().getHost();
        String scheme = request.getURI().getScheme();
        String sessionCookie = authService.getSessionCookie(host, scheme);
        if (sessionCookie != null) {
            request.getHeaders().remove("Cookie");
            request.getHeaders().add("Cookie", sessionCookie);
            return sessionCookie;
        }
        return null;
    }
}
