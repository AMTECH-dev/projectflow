package io.amtech.projectflow.service.token;

public interface TokenService {
    TokenPayload decode(String token);

    TokenDto generate(AuthDto dto);

    TokenDto refresh(TokenRefreshDto dto);

    void delete(String accessToken);

    boolean isValid(String token);
}
