package ru.oke.sinebot.keenetic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import ru.oke.sinebot.keenetic.service.router.keenetic.KeeneticAuthInterceptor;
import ru.oke.sinebot.oauth.config.IntrospectionPropertiesProvider;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RestConfig {
    public static final String KEENETIC_REST_TEMPLATE = "keeneticRestTemplate";

    public static final String INTROSPECTION_REST_TEMPLATE = "introspectionRestTemplate";

    private final IntrospectionPropertiesProvider introspectionProperties;

    @Bean
    @Primary
    public RestTemplate defaultRestTemplate() {
        return new RestTemplate();
    }

    @Bean(name = KEENETIC_REST_TEMPLATE)
    public RestTemplate keeneticRestTemplate(KeeneticAuthInterceptor authInterceptor) {
        RestTemplate template = new RestTemplate();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
        interceptors.add(authInterceptor);
        return template;
    }

    @LoadBalanced
    @Bean(name = INTROSPECTION_REST_TEMPLATE)
    public RestTemplate ribbonRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        String clientId = introspectionProperties.getClientId();
        String clientSecret = introspectionProperties.getClientSecret();
        BasicAuthenticationInterceptor auth = new BasicAuthenticationInterceptor(clientId, clientSecret);
        restTemplate.getInterceptors().add(auth);
        return restTemplate;
    }
}
