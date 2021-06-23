package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.Token;
import io.amtech.projectflow.test.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

public class TokenRepositoryTest extends IntegrationTest {
    private static final String INSERT_TOKEN = "classpath:db/repositoryTests/TokenRepositoryTest/insert_token.sql";

    @Autowired
    private TokenRepository tokenRepository;

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

    @ParameterizedTest(name = "createSuccessTest")
    @MethodSource("createSuccessTestArgs")
    @Sql(INSERT_TOKEN)
    void createSuccessTest(final String accessToken, final String refreshToken, final Token t) {
        final Token token = new Token()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);

        txUtil.txRun(() -> tokenRepository.save(token));

        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findAll())).hasSize(6);
        Assertions.assertThat(token).isEqualTo(t);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(
                Arguments.arguments(null, null),
                Arguments.arguments(null, "ytrewq54321"),
                Arguments.arguments("123qwerty", null),
                Arguments.arguments("newToken", null)
        );
    }

    @ParameterizedTest(name = "{index} createFailTest")
    @MethodSource("createFailTestArgs")
    @Sql(INSERT_TOKEN)
    void createFailTest(final String accessToken, final String refreshToken) {
        final Token token = new Token()
                .setAccessToken(accessToken)
                .setRefreshToken(refreshToken);

        Assertions.assertThatThrownBy(() -> txUtil.txRun(() -> tokenRepository.save(token)))
                .isInstanceOf(DataAccessException.class);
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

    @ParameterizedTest(name = "getSuccessTest")
    @MethodSource("getSuccessTestArgs")
    @Sql(INSERT_TOKEN)
    void getSuccessTest(final String accessToken, final Token t) {
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken)))
                .isPresent()
                .contains(t);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(Arguments.arguments("wrongAccessToken"));
    }

    @ParameterizedTest(name = "getFailTest")
    @MethodSource("getFailTestArgs")
    @Sql(INSERT_TOKEN)
    void getFailTest(final String accessToken) {
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken)))
                .isNotPresent();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteSuccessTestArgs() {
        return Stream.of(Arguments.arguments("123qwerty"));
    }

    @ParameterizedTest(name = "deleteSuccessTest")
    @MethodSource("deleteSuccessTestArgs")
    @Sql(INSERT_TOKEN)
    void deleteSuccessTest(final String accessToken) {
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken))).isPresent();

        txUtil.txRun(() -> tokenRepository.deleteById(accessToken));

        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findById(accessToken))).isNotPresent();
        Assertions.assertThat(txUtil.txRun(() -> tokenRepository.findAll())).hasSize(4);
    }
}
