package ru.oke.sinebot.gateway.service;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Чистейший костыль. К сожалению, я не нашел, как включить на стороне Spring oauth2-server endpoint user-info,
 * который требуется для работы используемого oauth2-client, и не нашел, как отключить необходимость использования
 * этого endpoint на стороне spring oauth2-client.
 * Поэтому запрос информации о клиенте будет построен на использовании introspect endpoint сервера авторизации :)
 * Честно признаюсь, RFC oauth внимательно не читал и большая часть кода честно скопипащена из
 * {@link DefaultReactiveOAuth2UserService} и попилена на небольшие методы
 *
 * @author k.oshoev
 */
@RequiredArgsConstructor
@Service
public class IntrospectionReactiveOAuth2UserService implements ReactiveOAuth2UserService<OAuth2UserRequest,
        OAuth2User> {

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

    private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";

    private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";

    private static final ParameterizedTypeReference<Map<String, Object>> STRING_OBJECT_MAP =
            new ParameterizedTypeReference<>() {
            };

    private static final ParameterizedTypeReference<Map<String, String>> STRING_STRING_MAP =
            new ParameterizedTypeReference<>() {
            };

    private final WebClient webClient;

    /**
     * Метод для загрузки информации о конечном пользователе по выданному ему токену. В данной реализации расчитан на
     * обращение к Introspection endpoint сервера авторизации
     *
     * @param userRequest объект, содержащий необходимую информацию для построения запроса информации о пользователе
     * @return объект, содержащий информацию о claims конечного пользователя
     * @throws OAuth2AuthenticationException при невозможности выполнить обращение к introspection endpoint. Например,
     *                                       при некорректно настроенных client-id и client-secret, либо в случае, если
     *                                       токен неактивен
     */
    @Override
    public Mono<OAuth2User> loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return Mono.defer(() -> {
            Assert.notNull(userRequest, "userRequest cannot be null");
            String userInfoUri = IntrospectionReactiveOAuth2UserService.validateUserInfoUri(userRequest);
            String userNameAttributeName = IntrospectionReactiveOAuth2UserService
                    .validateUserNameAttribute(userRequest);
            Mono<Map<String, Object>> userAttributes = getUserAttributes(userRequest, userInfoUri);
            return userAttributes.map((attrs) -> parseUserInfo(userRequest, userNameAttributeName, attrs))
                    .onErrorMap((ex) -> (ex instanceof UnsupportedMediaTypeException ||
                            ex.getCause() instanceof UnsupportedMediaTypeException), (ex) -> {
                        throw handleUnsupportedContentTypeError(userRequest, ex);
                    })
                    .onErrorMap((ex) -> {
                        OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                                "An error occurred reading the UserInfo response: " + ex.getMessage(),
                                null);
                        return new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
                    });
        });
    }

    /**
     * Метод выполняет непосредственное обращение к introspection endpoint и разбирает claims токена на атрибуты
     *
     * @param userRequest объект, содержащий необходимую информацию для построения запроса информации о пользователе
     * @param userInfoUri адрес introspection endpoint
     * @return Mono с claims токена, построенными в виде hash map
     */
    private Mono<Map<String, Object>> getUserAttributes(OAuth2UserRequest userRequest, String userInfoUri) {
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = getRequestHeaderSpec(userRequest, userInfoUri);
        return requestHeadersSpec.retrieve()
                .onStatus(HttpStatusCode::isError, (response) ->
                        parseError(response)
                                .map((userInfoErrorResponse) -> {
                                    String description = userInfoErrorResponse.getErrorObject().getDescription();
                                    OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                                            description, null);
                                    throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
                                })
                )
                .bodyToMono(IntrospectionReactiveOAuth2UserService.STRING_OBJECT_MAP);
    }

    /**
     * Метод, конструирующий корректный запрос к introspection endpoint сервера авторизации oauth2
     *
     * @param userRequest объект, содержащий необходимую информацию для построения запроса информации о пользователе
     * @param userInfoUri адрес introspection endpoint
     * @return сконструированный при помощи WebClient объект запроса к oauth2 серверу
     */
    private WebClient.RequestHeadersSpec<?> getRequestHeaderSpec(OAuth2UserRequest userRequest, String userInfoUri) {
        String clientId = userRequest.getClientRegistration().getClientId();
        String clientSecret = userRequest.getClientRegistration().getClientSecret();
        return this.webClient.post()
                .uri(userInfoUri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .headers((headers) -> headers.setBasicAuth(clientId, clientSecret))
                .bodyValue("token=" + userRequest.getAccessToken().getTokenValue());
    }

    private static String validateUserInfoUri(OAuth2UserRequest userRequest) {
        String userInfoUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUri();
        if (!StringUtils.hasText(userInfoUri)) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_INFO_URI_ERROR_CODE,
                    "Missing required UserInfo Uri in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return userInfoUri;
    }

    private static String validateUserNameAttribute(OAuth2UserRequest userRequest) {
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        if (!StringUtils.hasText(userNameAttributeName)) {
            OAuth2Error oauth2Error = new OAuth2Error(MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE,
                    "Missing required \"user name\" attribute name in UserInfoEndpoint for Client Registration: "
                            + userRequest.getClientRegistration().getRegistrationId(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString());
        }
        return userNameAttributeName;
    }

    private static OAuth2User parseUserInfo(OAuth2UserRequest userRequest, String userNameAttributeName,
                                            Map<String, Object> userClaims) {
        GrantedAuthority authority = new OAuth2UserAuthority(userClaims);
        Set<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(authority);
        OAuth2AccessToken token = userRequest.getAccessToken();
        for (String scope : token.getScopes()) {
            authorities.add(new SimpleGrantedAuthority("SCOPE_" + scope));
        }
        return new DefaultOAuth2User(authorities, userClaims, userNameAttributeName);
    }

    /**
     * Метод парсинга ошибки. От introspection endpoint может приходить только UNAUTHORIZED
     *
     * @param httpResponse ответ, полученный от introspection endpoint и содержащий код ошибки HTTP
     * @return объект Mono, содержащий описание ошибки получения UserInfo
     */
    @SuppressWarnings("deprecation")
    private static Mono<UserInfoErrorResponse> parseError(ClientResponse httpResponse) {
        return httpResponse.bodyToMono(STRING_STRING_MAP)
                .map((body) -> new UserInfoErrorResponse(ErrorObject.parse(new JSONObject(body))));
    }

    private OAuth2AuthenticationException handleUnsupportedContentTypeError(OAuth2UserRequest userRequest,
                                                                            Throwable ex) {
        String contentType = (ex instanceof UnsupportedMediaTypeException) ?
                Objects.requireNonNull(((UnsupportedMediaTypeException) ex).getContentType()).toString() :
                Objects.requireNonNull(((UnsupportedMediaTypeException) ex.getCause()).getContentType()).toString();
        String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
                + userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUri()
                + "': response contains invalid content type '" + contentType + "'. "
                + "The UserInfo Response should return a JSON object (content type 'application/json') "
                + "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
                + "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
                + userRequest.getClientRegistration().getRegistrationId()
                + "' conforms to Introspection OAuth2 Endpoint";
        OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage,
                null);
        return new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
    }
}
