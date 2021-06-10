package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.test.IntegrationTest;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.assertj.core.api.Assertions;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

class MilestoneRepositoryTest extends IntegrationTest {
    private static final Instant NOW = Instant.now();

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MilestoneRepository milestoneRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessArgs() {
        return Stream.of(
                Arguments.arguments("NAME", "DESC", NOW, NOW.plus(1, ChronoUnit.DAYS),
                        null, null, (short) 0, 2, new Milestone()
                                .setId(6L)
                                .setName("NAME")
                                .setDescription("DESC")
                                .setPlannedStartDate(NOW)
                                .setPlannedFinishDate(NOW.plus(1, ChronoUnit.DAYS))
                                .setProgressPercent((short) 0)),
                Arguments.arguments("Update project", null,
                        NOW.minus(2, ChronoUnit.DAYS), NOW.plus(1, ChronoUnit.DAYS),
                        NOW.plus(4, ChronoUnit.DAYS), NOW.plus(22, ChronoUnit.DAYS),
                        (short) 10, 3, new Milestone()
                                .setId(6L)
                                .setName("Update project")
                                .setPlannedStartDate(NOW.minus(2, ChronoUnit.DAYS))
                                .setPlannedFinishDate(NOW.plus(1, ChronoUnit.DAYS))
                                .setFactStartDate(NOW.plus(4, ChronoUnit.DAYS))
                                .setFactFinishDate(NOW.plus(22, ChronoUnit.DAYS))
                                .setProgressPercent((short) 10))
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/MilestoneRepositoryTest/data.sql"
    })
    @Transactional
    void createSuccessTest(final String name, final String description,
                           final Instant plannedStartDate, final Instant plannedFinishDate,
                           final Instant factStartDate, final Instant factFinishDate, short progressPercent,
                           long projectId, final Milestone m) {
        Optional<Project> p = txUtil.txRun(() -> projectRepository.findById(projectId));
        Assertions.assertThat(p)
                .isPresent();

        Milestone milestone = new Milestone()
                .setName(name)
                .setDescription(description)
                .setPlannedStartDate(plannedStartDate)
                .setPlannedFinishDate(plannedFinishDate)
                .setFactStartDate(factStartDate)
                .setFactFinishDate(factFinishDate)
                .setProgressPercent(progressPercent);
        Set<Milestone> milestones = txUtil.txRun(() -> p.get().getMilestones());
        milestones.add(milestone);
        milestoneRepository.save(milestone);

        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findAll()))
                .hasSize(6);

        Assertions.assertThat(milestone)
                .isEqualTo(m);
    }


    @SuppressWarnings("unused")
    static Stream<Arguments> createFailArgs() {
        return Stream.of(
                Arguments.arguments(null, NOW, NOW.plus(1, ChronoUnit.DAYS),
                        (short) 0, 2, DataIntegrityViolationException.class),
                Arguments.arguments("NAME", null, NOW.plus(1, ChronoUnit.DAYS),
                        (short) 0, 2, DataIntegrityViolationException.class),
                Arguments.arguments("NAME", NOW, null,
                        (short) 0, 2, DataIntegrityViolationException.class),
                Arguments.arguments("NAME", NOW, NOW.plus(1, ChronoUnit.DAYS),
                        (short) -1, 2, DataIntegrityViolationException.class),
                Arguments.arguments("NAME", NOW, NOW.plus(1, ChronoUnit.DAYS),
                        (short) 1, -1, ObjectNotFoundException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("createFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/MilestoneRepositoryTest/data.sql"
    })
    @Transactional
    void createFailTest(final String name, final Instant plannedStartDate, final Instant plannedFinishDate,
                        short progressPercent, long projectId, final Class<?> expectedExceptionClass) {
        Milestone milestone = new Milestone()
                .setName(name)
                .setPlannedStartDate(plannedStartDate)
                .setPlannedFinishDate(plannedFinishDate)
                .setProgressPercent(progressPercent);

        Assertions.assertThatThrownBy(() -> {
            Project project = txUtil.txRun(() -> projectRepository.findById(projectId))
                    .orElseThrow(() -> new ObjectNotFoundException("Project", projectId));
            project.getMilestones().add(milestone);
            txUtil.txRun(() -> milestoneRepository.save(milestone));
        }).isInstanceOf(expectedExceptionClass);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1, new Milestone()
                        .setId(1L)
                        .setName("Create dto")
                        .setPlannedStartDate(Instant.parse("2021-06-09T11:49:03.839234Z"))
                        .setPlannedFinishDate(Instant.parse("2021-06-11T11:49:03.839234Z"))),
                Arguments.arguments(2, new Milestone()
                        .setId(2L)
                        .setName("Update migration")
                        .setDescription("using flyway")
                        .setPlannedStartDate(Instant.parse("2021-05-09T11:49:03.839234Z"))
                        .setPlannedFinishDate(Instant.parse("2021-06-30T11:49:03.839234Z"))
                        .setFactStartDate(Instant.parse("2021-07-30T11:49:03.839234Z"))
                        .setFactFinishDate(Instant.parse("2021-08-30T11:49:03.839234Z"))
                        .setProgressPercent((short) 23))
        );
    }

    @ParameterizedTest
    @MethodSource("getSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/MilestoneRepositoryTest/data.sql"
    })
    void getSuccessTest(long id, final Milestone m) {
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(id)))
                .isPresent()
                .contains(m);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailArgs() {
        return Stream.of(
                Arguments.arguments(-1),
                Arguments.arguments(328048)
        );
    }

    @ParameterizedTest
    @MethodSource("getFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/repositoryTests/MilestoneRepositoryTest/data.sql"
    })
    void getFailTest(long id) {
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(id)))
                .isNotPresent();
    }
}