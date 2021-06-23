package io.amtech.projectflow.service.token;

import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {
    TokenDto generate(AuthDto dto);
    TokenDto generate(String username, String password);
    TokenDto refresh(TokenRefreshDto dto);
    void delete(String accessToken);
    boolean isValid(String token);
    String resolveToken(Authentication auth);
    Authentication getAuthentication(HttpServletRequest request);
}
