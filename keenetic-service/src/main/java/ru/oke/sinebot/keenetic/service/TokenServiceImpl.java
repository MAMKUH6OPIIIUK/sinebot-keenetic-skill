package ru.oke.sinebot.keenetic.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TokenServiceImpl implements TokenService {
    @Override
    public Object getTokenClaimValue(String claimName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof BearerTokenAuthentication bearerAuth) {
            Map<String, Object> attributes = bearerAuth.getTokenAttributes();
            return attributes.get(claimName);
        }
        return null;
    }
}
