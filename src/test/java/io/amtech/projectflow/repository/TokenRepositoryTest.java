package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.Token;
import io.amtech.projectflow.test.IntegrationTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

public class TokenRepositoryTest extends IntegrationTest {
    @Autowired
    private TokenRepository tokenRepository;

    private static final String INSERT_TOKEN = "classpath:db/repositoryTests/TokenRepositoryTest/insert_token.sql";

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "1q2w3e", "3e2w1q",
                        new Token()
                                .setAccessToken("1q2w3e")
                                .setRefreshToken("3e2w1q")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_TOKEN)
    void createSuccessTest(final String accessToken, final String refreshToken, final Token t) {
        Token token = new Token();
        t.setAccessToken(accessToken);
        t.setRefreshToken(refreshToken);

        tokenRepository.save(token);

        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findAll())).hasSize(6);
        Assertions.assertThat(token).isEqualTo(t);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(Arguments.arguments(null, null));
    }

    @ParameterizedTest
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_TOKEN)
    void createFailTest(final String accessToken, final String refreshToken) {
        Token token = new Token();
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);

        Assertions.assertThatThrownBy(() -> txUtil.txRun(() -> tokenRepository.save(token)))
                .isInstanceOf(DataIntegrityViolationException.class);
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findAll())).hasSize(5);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("123qwerty",
                        new Token()
                                .setAccessToken("123qwerty")
                                .setRefreshToken("ytrewq54321"))
        );
    }

    @ParameterizedTest
    @MethodSource("getSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_TOKEN)
    void getSuccessTest(final String accessToken, final Token t) {
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken)))
                .isPresent()
                .contains(t);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(
                Arguments.arguments((String) null),
                Arguments.arguments("wrong_access_token")
        );
    }

    @ParameterizedTest
    @MethodSource("getFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_TOKEN)
    void getFailTest(final String accessToken) {
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken)))
                .isNotPresent();
    }
}
