package ru.oke.sinebot.oauth.config;

public interface AuthorizationServerPropertiesProvider {
    String getIssuerUrl();

    String getIntrospectionEndpoint();
}
