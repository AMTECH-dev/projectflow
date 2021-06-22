package io.amtech.projectflow.service.token;

import javax.servlet.http.HttpServletRequest;

public interface TokenService {
    TokenDto generate(AuthDto dto);
    TokenDto refresh(TokenRefreshDto dto);
    void delete(String accessToken);
    boolean isValid(String token);
    String resolveToken(HttpServletRequest request);
}
