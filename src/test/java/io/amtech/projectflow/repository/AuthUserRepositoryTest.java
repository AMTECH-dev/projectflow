package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.employee.AuthUser;
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

class AuthUserRepositoryTest extends IntegrationTest {
    @Autowired
    private AuthUserRepository authUserRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessArgs() {
        return Stream.of(
                Arguments.arguments(8, "SUPER LOG", "1234", false,
                        new AuthUser()
                                .setId(8L)
                                .setLogin("SUPER LOG")
                                .setPassword("1234")
                                .setLocked(false)),
                Arguments.arguments(10, "MEGA LOGIN", "123456", true,
                        new AuthUser()
                                .setId(10L)
                                .setLogin("MEGA LOGIN")
                                .setPassword("123456")
                                .setLocked(true))
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/AuthUserRepositoryTest/data.sql"
    })
    void createSuccessTest(long employeeId, final String login, final String password, boolean isLocked,
                           final AuthUser u) {
        Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(employeeId)))
                .isPresent();

        AuthUser authUser = new AuthUser()
                .setId(employeeId)
                .setLogin(login)
                .setPassword(password)
                .setLocked(isLocked);
        txUtil.txRun(() -> authUserRepository.save(authUser));

        Assertions.assertThat(txUtil.txRun(() -> authUserRepository.findAll()))
                .hasSize(4);

        Assertions.assertThat(authUser)
                .isEqualTo(u);
    }


    @SuppressWarnings("unused")
    static Stream<Arguments> createFailArgs() {
        return Stream.of(
                Arguments.arguments(-1, "username", "12345"),
                Arguments.arguments(10, null, "12345"),
                Arguments.arguments(10, "username", null),
                Arguments.arguments(10, "gmailgmail", "324")
        );
    }

    @ParameterizedTest
    @MethodSource("createFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/AuthUserRepositoryTest/data.sql"
    })
    void createFailTest(long employeeId, final String login, final String password) {
        AuthUser authUser = new AuthUser()
                .setId(employeeId)
                .setLogin(login)
                .setPassword(password);
        Assertions.assertThatThrownBy(() -> txUtil.txRun(() -> authUserRepository.save(authUser)))
                .isInstanceOf(DataIntegrityViolationException.class);

        Assertions.assertThat(txUtil.txRun(() -> authUserRepository.findAll()))
                .hasSize(3);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1,
                        new AuthUser()
                                .setId(1L)
                                .setLogin("gmailgmail")
                                .setPassword("{bcrypt}hash_super_pass")),
                Arguments.arguments(2,
                        new AuthUser()
                                .setId(2L)
                                .setLogin("super@gmail.com")
                                .setPassword("{bcrypt}hash_super_pass"))
        );
    }

    @ParameterizedTest
    @MethodSource("getSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/AuthUserRepositoryTest/data.sql"
    })
    void getSuccessTest(long id, final AuthUser u) {
        Assertions.assertThat(txUtil.txRun(() -> authUserRepository.findById(id)))
                .isPresent()
                .contains(u);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailArgs() {
        return Stream.of(
                Arguments.arguments(-1),
                Arguments.arguments(0)
        );
    }

    @ParameterizedTest
    @MethodSource("getFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/AuthUserRepositoryTest/data.sql"
    })
    void getFailTest(long id) {
        Assertions.assertThat(txUtil.txRun(() -> authUserRepository.findById(id)))
                .isNotPresent();
    }
}