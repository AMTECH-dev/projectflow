package io.amtech.projectflow.service.token.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.amtech.projectflow.app.exception.ExpiredTokenException;
import io.amtech.projectflow.domain.Token;
import io.amtech.projectflow.repository.TokenRepository;
import io.amtech.projectflow.service.token.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Base64;

@Service
@Transactional
@RequiredArgsConstructor
@PropertySource("classpath:token.properties")
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    @Value("${expireTimeAccessToken}")
    private long expireTimeAccessToken;

    @Value("${expireTimeRefreshToken}")
    private long expireTimeRefreshToken;

    @Value("${secret}")
    private String secret;

    @PostConstruct
    public void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    @Override
    public TokenDto generate(final AuthDto dto) {
        Token token = createToken(dto.getUsername());
        Token savedToken = tokenRepository.save(token);
        return new TokenDto(savedToken);
    }

    @Override
    public TokenDto refresh(final TokenRefreshDto dto) {
        String refreshToken = dto.getRefreshToken();
        if (isValid(refreshToken)) {
            throw new ExpiredTokenException(refreshToken);
        }

        Token tokenFromDb = tokenRepository.findByRefreshTokenOrThrow(refreshToken);
        TokenPayload data = decode(refreshToken);
        Token token = createToken(data.getUsername());
        tokenFromDb.setAccessToken(token.getAccessToken())
                .setRefreshToken(token.getRefreshToken());
        return new TokenDto(tokenFromDb);
    }

    @Override
    public void delete(final String accessToken) {
        if (isValid(accessToken)) {
            throw new ExpiredTokenException(accessToken);
        }

        tokenRepository.findByAccessTokenOrThrow(accessToken);
        tokenRepository.deleteByAccessToken(accessToken);
    }

    @Override
    public boolean isValid(final String token) {
        TokenPayload data = decode(token);
        return Instant.now().isBefore(data.getExpireAt());
    }

    private Token createToken(final String username) {
        TokenPayload accessToken = new TokenPayload(username, Instant.now().plusMillis(expireTimeAccessToken));
        TokenPayload refreshToken = new TokenPayload(username, Instant.now().plusMillis(expireTimeRefreshToken));
        return new Token()
                .setAccessToken(encode(accessToken))
                .setRefreshToken(encode(refreshToken));
    }

    @Override
    @SneakyThrows
    public TokenPayload decode(final String token) {
        byte[] decodeTokenBytes = Base64.getDecoder().decode(token);
        return objectMapper.readValue(new String(decodeTokenBytes).replace(secret, ""), new TypeReference<>() {});
    }

    @SneakyThrows
    private String encode(final TokenPayload payload) {
        String jsonPayLoad = objectMapper.writeValueAsString(payload) + secret;
        return Base64.getEncoder().encodeToString(jsonPayLoad.getBytes());
    }
}
