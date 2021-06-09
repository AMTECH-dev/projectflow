package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.test.IntegrationTest;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

class ProjectJournalRepositoryTest extends IntegrationTest {
    @Autowired
    private ProjectJournalRepository projectJournalRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> getProjectJournalSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1),
                Arguments.arguments(2),
                Arguments.arguments(10)
        );
    }

    @ParameterizedTest
    @MethodSource("getProjectJournalSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalRepositoryTest/getProjectJournalSuccessTest/data.sql"
    })
    void getProjectJournalSuccessTest(long id) {
        Assertions.assertThat(txUtil.txRun(() -> projectJournalRepository.findById(id)))
                .isPresent();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getProjectJournalFailArgs() {
        return Stream.of(
                Arguments.arguments(1000),
                Arguments.arguments(-1)
        );
    }

    @ParameterizedTest
    @MethodSource("getProjectJournalFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalRepositoryTest/getProjectJournalSuccessTest/data.sql"
    })
    void getProjectJournalFailTest(long id) {
        Assertions.assertThat(txUtil.txRun(() -> projectJournalRepository.findById(id)))
                .isNotPresent();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectJournalSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1, "fedor", Instant.now()),
                Arguments.arguments(1, "OLEG", LocalDateTime.now().toInstant(ZoneOffset.ofHours(5)))
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectJournalSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalRepositoryTest/createProjectJournalSuccessTest/data.sql"
    })
    void createProjectJournalSuccessTest(long projectId, final String login, final Instant updateDate) {
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId)))
                .isPresent();
        Project project = txUtil.txRun(() -> projectRepository.findById(projectId))
                .orElseThrow(RuntimeException::new);

        ProjectJournal projectJournal = new ProjectJournal()
                .setLogin(login)
                .setUpdateDate(updateDate)
                .setProject(project);

        txUtil.txRun(() -> projectJournalRepository.save(projectJournal));
        Assertions.assertThat(txUtil.txRun(() -> projectJournalRepository.findAll().size()))
                .isEqualTo(15);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectJournalFailArgs() {
        return Stream.of(
                Arguments.arguments(-1),
                Arguments.arguments(3352135)
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectJournalFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalRepositoryTest/createProjectJournalSuccessTest/data.sql"
    })
    void createProjectJournalFailTest(long projectId) {
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId)))
                .isNotPresent();
    }
}
