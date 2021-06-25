package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, String> {
    Optional<Token> findByAccessToken(String accessToken);
    Optional<Token> findByRefreshToken(String refreshToken);
    void deleteByAccessToken(String accessToken);

    default Token findByRefreshTokenOrThrow(String refreshToken) {
        return findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ObjectNotFoundException(Token.class.getSimpleName(), refreshToken));
    }

    default Token findByAccessTokenOrThrow(String accessToken) {
        return findByAccessToken(accessToken)
                .orElseThrow(() -> new ObjectNotFoundException(Token.class.getSimpleName(), accessToken));
    }
}
