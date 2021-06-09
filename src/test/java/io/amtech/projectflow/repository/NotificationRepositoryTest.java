package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.Notification;
import io.amtech.projectflow.test.IntegrationTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.stream.Stream;

class NotificationRepositoryTest extends IntegrationTest {
    private static final Instant NOW = Instant.now();

    @Autowired
    private NotificationRepository notificationRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessArgs() {
        return Stream.of(
                Arguments.arguments("vb@gmail.com", "vov@gmail.com", null, null, null,
                        new Notification()
                                .setId(6L)
                                .setRecepient("vb@gmail.com")
                                .setSender("vov@gmail.com")),
                Arguments.arguments("vb@gmail.com", "vov@gmail.com", "super subject", null, null,
                        new Notification()
                                .setId(6L)
                                .setRecepient("vb@gmail.com")
                                .setSender("vov@gmail.com")
                                .setSubject("super subject")),
                Arguments.arguments("vb@gmail.com", "vov@gmail.com", "super subject", "super body", null,
                        new Notification()
                                .setId(6L)
                                .setRecepient("vb@gmail.com")
                                .setSender("vov@gmail.com")
                                .setSubject("super subject")
                                .setBody("super body")),
                Arguments.arguments("vb@gmail.com", "vov@gmail.com", "super subject", "super body", NOW,
                        new Notification()
                                .setId(6L)
                                .setRecepient("vb@gmail.com")
                                .setSender("vov@gmail.com")
                                .setSubject("super subject")
                                .setBody("super body")
                                .setCreateDate(NOW)),
                Arguments.arguments("vb@gmail.com", "vov@gmail.com", null, "super body", NOW,
                        new Notification()
                                .setId(6L)
                                .setRecepient("vb@gmail.com")
                                .setSender("vov@gmail.com")
                                .setBody("super body")
                                .setCreateDate(NOW))
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/NotificationRepositoryTest/data.sql"
    })
    void createSuccessTest(final String recepient, final String sender, final String subject, final String body,
                           final Instant createDate, final Notification n) {
        Notification notification = new Notification()
                .setRecepient(recepient)
                .setSender(sender)
                .setSubject(subject)
                .setBody(body)
                .setCreateDate(createDate);
        txUtil.txRun(() -> notificationRepository.save(notification));
        Assertions.assertThat(txUtil.txRun(() -> notificationRepository.findAll()))
                .hasSize(6);

        Assertions.assertThat(notification)
                .isEqualTo(n);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailArgs() {
        return Stream.of(
                Arguments.arguments("ewq", "vb@gmail.com"),
                Arguments.arguments("vb@gmail.com", "qwert"),
                Arguments.arguments(null, "vb@gmail.com"),
                Arguments.arguments("vb@gmail.com", null)
        );
    }

    @ParameterizedTest
    @MethodSource("createFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/NotificationRepositoryTest/data.sql"
    })
    void createFailTest(final String recepient, final String sender) {
        Assertions.assertThatThrownBy(() -> {
            Notification notification = new Notification()
                    .setRecepient(recepient)
                    .setSender(sender);
            txUtil.txRun(() -> notificationRepository.save(notification));
        }).isInstanceOf(DataIntegrityViolationException.class);

        Assertions.assertThat(txUtil.txRun(() -> notificationRepository.findAll()))
                .hasSize(5);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1,
                        new Notification()
                                .setId(1L)
                                .setRecepient("vb@gmail.com")
                                .setSender("ali@gmail.com")
                                .setSubject("Skill")),
                Arguments.arguments(2,
                        new Notification()
                                .setId(2L)
                                .setRecepient("kol@gmail.com")
                                .setSender("sim@gmail.com")
                                .setSubject("Baby don_t cry")
                                .setBody("PLS")
                                .setCreateDate(Instant.parse("2021-06-11T11:49:03.839234Z")))
        );
    }

    @ParameterizedTest
    @MethodSource("getSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/NotificationRepositoryTest/data.sql"
    })
    void getSuccessTest(long id, final Notification n) {
        Assertions.assertThat(txUtil.txRun(() -> notificationRepository.findById(id)))
                .isPresent()
                .contains(n);
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
            "classpath:db/NotificationRepositoryTest/data.sql"
    })
    void getFailTest(long id) {
        Assertions.assertThat(txUtil.txRun(() -> notificationRepository.findById(id)))
                .isNotPresent();
    }
}