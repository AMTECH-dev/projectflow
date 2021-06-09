package io.amtech.projectflow.repository;

import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectComment;
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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

class ProjectCommentRepositoryTest extends IntegrationTest {
    @Autowired
    private ProjectCommentRepository projectCommentRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectCommentSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(1,
                        "Comment 01",
                        Instant.parse("2021-06-09T11:49:03.839234Z"),
                        "KiraLis39",
                        new ProjectComment()
                                .setId(6L)
                                .setMessage("Comment 01")
                                .setCreateDate(Instant.parse("2021-06-09T11:49:03.839234Z"))
                                .setLogin("KiraLis39")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectCommentSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentTest/exists_projects.sql"
    })
    @Transactional
    void createProjectCommentSuccessTest(long projectId, final String message, final Instant instant, final String login,
                                         final ProjectComment comment) {
        Optional<Project> optionalProject = txUtil.txRun(() -> projectRepository.findById(projectId));
        Assertions.assertThat(optionalProject)
                .isPresent();
        Project project = optionalProject.get();

        ProjectComment pc = new ProjectComment()
                .setMessage(message)
                .setCreateDate(instant)
                .setLogin(login);
        txUtil.txRun(() -> {
            Set<ProjectComment> projectComments = project.getProjectComments();
            projectComments.add(pc);
            projectCommentRepository.save(pc);
        });

        Assertions.assertThat(txUtil.txRun(() -> projectCommentRepository.findAll().size()))
                .isEqualTo(6);

        Assertions.assertThat(pc)
                .isEqualTo(comment);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectCommentFiledTestArgs() {
        return Stream.of(
                Arguments.arguments(new ProjectComment()
                        .setMessage("HELLO WORLD")
                        .setCreateDate(Instant.parse("2021-06-09T11:49:23.839234Z"))
                        .setLogin(null), DataIntegrityViolationException.class),
                Arguments.arguments(new ProjectComment()
                        .setMessage(null)
                        .setCreateDate(Instant.parse("2021-06-09T11:49:23.839234Z"))
                        .setLogin("EWQ"), DataIntegrityViolationException.class),
                Arguments.arguments(new ProjectComment()
                        .setMessage("ewq")
                        .setCreateDate(null)
                        .setLogin("EWQ"), DataIntegrityViolationException.class),
                Arguments.arguments(new ProjectComment()
                        .setMessage("ewq")
                        .setCreateDate(Instant.parse("2021-06-09T11:49:23.839234Z"))
                        .setLogin("EWQ"), DataIntegrityViolationException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectCommentFiledTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentTest/exists_projects.sql"
    })
    void createProjectCommentFailedTest(final ProjectComment comment, final Class<?> exceptionClass) {
        Assertions.assertThatThrownBy(() -> txUtil.txRun(() -> projectCommentRepository.save(comment)))
                .isInstanceOf(exceptionClass);

        Assertions.assertThat(txUtil.txRun(() -> projectCommentRepository.findAll().size()))
                .isEqualTo(5);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getProjectCommentSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1,
                        new ProjectComment()
                                .setId(1L)
                                .setMessage("Comment 01")
                                .setCreateDate(Instant.parse("2021-06-09T11:49:03.839234Z"))
                                .setLogin("KiraLis39")),
                Arguments.arguments(2,
                        new ProjectComment()
                                .setId(2L)
                                .setMessage("Comment 02")
                                .setCreateDate(Instant.parse("2021-06-08T09:49:13.839234Z"))
                                .setLogin("KiraLis47")),
                Arguments.arguments(3,
                        new ProjectComment()
                                .setId(3L)
                                .setMessage("Comment 03")
                                .setCreateDate(Instant.parse("2021-06-07T11:00:03.839234Z"))
                                .setLogin("KiraLis39"))
        );
    }


    @ParameterizedTest
    @MethodSource("getProjectCommentSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentTest/exists_projects.sql"
    })
    void getProjectCommentSuccessTest(long id, final ProjectComment comment) {
        Assertions.assertThat(txUtil.txRun(() -> projectCommentRepository.findById(id)))
                .isPresent()
                .contains(comment);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getProjectCommentFailedArgs() {
        return Stream.of(
                Arguments.arguments(199),
                Arguments.arguments(9999)
        );
    }

    @ParameterizedTest
    @MethodSource("getProjectCommentFailedArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentTest/exists_projects.sql"
    })
    void getProjectCommentFailedTest(long id) {
        Assertions.assertThat(txUtil.txRun(() -> projectCommentRepository.findById(id)))
                .isNotPresent();
    }
}
