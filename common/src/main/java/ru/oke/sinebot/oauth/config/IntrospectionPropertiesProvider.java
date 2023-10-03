package ru.oke.sinebot.oauth.config;

public interface IntrospectionPropertiesProvider {
    String getClientId();

    String getClientSecret();

    String getIntrospectionEndpoint();
}
