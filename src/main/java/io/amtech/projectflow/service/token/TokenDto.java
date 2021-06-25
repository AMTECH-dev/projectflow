package io.amtech.projectflow.service.token;

import io.amtech.projectflow.domain.Token;
import lombok.Value;

@Value
public class TokenDto {
    String accessToken;
    String refreshToken;

    public TokenDto(final Token token) {
        this.accessToken = token.getAccessToken();
        this.refreshToken = token.getRefreshToken();
    }
}
