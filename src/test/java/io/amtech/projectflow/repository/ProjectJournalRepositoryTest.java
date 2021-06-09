package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.test.IntegrationTest;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

class ProjectJournalRepositoryTest extends IntegrationTest {
    private static final Instant NOW = Instant.now();

    @Autowired
    private ProjectJournalRepository projectJournalRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectJournalSuccessArgs() {
        Map<String, Object> empty = Collections.emptyMap();
        Map<String, Object> state = new HashMap<>() {{
            put("name", "HELLO WORLD!");
            put("desc", "SUPER DESCRIPTION");
            put("date", NOW);
        }};
        return Stream.of(
                Arguments.arguments(1, "fedor", NOW, empty,
                        new ProjectJournal()
                                .setId(15L)
                                .setLogin("fedor")
                                .setUpdateDate(NOW)),
                Arguments.arguments(1, "OLEG", NOW.minus(6, ChronoUnit.DAYS), empty,
                        new ProjectJournal()
                                .setId(15L)
                                .setLogin("OLEG")
                                .setUpdateDate(NOW.minus(6, ChronoUnit.DAYS))),
                Arguments.arguments(1, "Ivan", NOW.minus(6, ChronoUnit.DAYS), state,
                        new ProjectJournal()
                                .setId(15L)
                                .setLogin("Ivan")
                                .setUpdateDate(NOW.minus(6, ChronoUnit.DAYS))
                                .setCurrentState(state))
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectJournalSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalRepositoryTest/createProjectJournalSuccessTest/data.sql"
    })
    @Transactional
    void createProjectJournalSuccessTest(long projectId, final String login, final Instant updateDate,
                                         final Map<String, Object> state, final ProjectJournal p) {
        Optional<Project> optionalProject = txUtil.txRun(() -> projectRepository.findById(projectId));
        Assertions.assertThat(optionalProject)
                .isPresent();
        Project project = optionalProject.get();

        ProjectJournal projectJournal = new ProjectJournal()
                .setLogin(login)
                .setUpdateDate(updateDate)
                .setCurrentState(state);
        project.getHistory().add(projectJournal);

        txUtil.txRun(() -> projectJournalRepository.save(projectJournal));
        Assertions.assertThat(txUtil.txRun(() -> projectJournalRepository.findAll()))
                .hasSize(15);

        Assertions.assertThat(projectJournal)
                .isEqualTo(p);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectJournalFailArgs() {
        return Stream.of(
                Arguments.arguments(-1, "HELLO", NOW, ObjectNotFoundException.class),
                Arguments.arguments(1, null, NOW, DataIntegrityViolationException.class),
                Arguments.arguments(1, "HELLO", null, DataIntegrityViolationException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectJournalFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalRepositoryTest/createProjectJournalSuccessTest/data.sql"
    })
    void createProjectJournalFailTest(long projectId, final String login, final Instant updateDate,
                                      final Class<?> expectedExceptionClass) {
        ProjectJournal projectJournal = new ProjectJournal()
                .setLogin(login)
                .setUpdateDate(updateDate);

        Assertions.assertThatThrownBy(() -> {
            txUtil.txRun(() -> projectRepository.findById(projectId))
                    .orElseThrow(() -> new ObjectNotFoundException("Project", projectId));
            txUtil.txRun(() -> projectJournalRepository.save(projectJournal));
        }).isInstanceOf(expectedExceptionClass);

        Assertions.assertThat(projectJournalRepository.findAll())
                .hasSize(14);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getProjectJournalSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1, new ProjectJournal()),
                Arguments.arguments(2, new ProjectJournal()),
                Arguments.arguments(10, new ProjectJournal())
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
}
