package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectStatus;
import io.amtech.projectflow.test.IntegrationTest;
import io.amtech.projectflow.test.TestUtils;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

class ProjectRepositoryTest extends IntegrationTest {
    private static final Instant NOW = Instant.now();

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DirectionRepository directionRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessArgs() {

        return Stream.of(
                Arguments.arguments(TestUtils.strMultiple("test", 2), null,
                        NOW, 4, 1,
                        new Project()
                                .setId(6L)
                                .setName(TestUtils.strMultiple("test", 2))
                                .setCreateDate(NOW)),
                Arguments.arguments(TestUtils.strMultiple("t", 3), TestUtils.strMultiple("ab", 5),
                        Instant.parse("2021-06-09T11:49:03.839234Z"), 4, 1,
                        new Project()
                                .setId(6L)
                                .setName(TestUtils.strMultiple("t", 3))
                                .setDescription(TestUtils.strMultiple("ab", 5))
                                .setCreateDate(Instant.parse("2021-06-09T11:49:03.839234Z")))
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectRepositoryTest/data.sql"
    })
    void createSuccessTest(final String name, final String desc, final Instant createDate, long leadId, long directionId,
                           final Project newProject) {
        Optional<Employee> optionalEmployee = txUtil.txRun(() -> employeeRepository.findById(leadId));
        Assertions.assertThat(optionalEmployee)
                .isPresent();

        Optional<Direction> optionalDirection = txUtil.txRun(() -> directionRepository.findById(directionId));
        Assertions.assertThat(optionalDirection)
                .isPresent();

        Project project = new Project()
                .setName(name)
                .setDescription(desc)
                .setCreateDate(createDate)
                .setProjectLead(optionalEmployee.get())
                .setDirection(optionalDirection.get());

        txUtil.txRun(() -> projectRepository.save(project));

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.count()))
                .isEqualTo(6);

        Assertions.assertThat(newProject)
                .isEqualTo(project);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailArgs() {
        return Stream.of(
                Arguments.arguments(null, NOW, 1, 10, DataIntegrityViolationException.class),
                Arguments.arguments("Project name", null, 1, 10, DataIntegrityViolationException.class),
                Arguments.arguments("Project name", NOW, -1, 10, ObjectNotFoundException.class),
                Arguments.arguments("Project name", NOW, 1, -1, ObjectNotFoundException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("createFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectRepositoryTest/data.sql"
    })
    void createFailTest(final String name, final Instant createDate, long leadId, long directionId,
                        final Class<?> expectedExceptionClass) {
        Project project = new Project()
                .setName(name)
                .setCreateDate(createDate);

        Assertions.assertThatThrownBy(() -> {
            Employee employee = txUtil.txRun(() -> employeeRepository.findById(leadId))
                    .orElseThrow(() -> new ObjectNotFoundException("Employee", leadId));
            Direction direction = txUtil.txRun(() -> directionRepository.findById(directionId))
                    .orElseThrow(() -> new ObjectNotFoundException("Direction", directionId));

            project.setProjectLead(employee);
            project.setDirection(direction);
            txUtil.txRun(() -> projectRepository.save(project));
        }).isInstanceOf(expectedExceptionClass);

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findAll()))
                .hasSize(5);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessArgs() {
        return Stream.of(
                Arguments.arguments(2, new Project()
                        .setId(2L)
                        .setName("DROP DATABASE")
                        .setDescription("")
                        .setCreateDate(Instant.parse("2021-06-09T11:49:03.839234Z"))
                        .setProjectStatus(ProjectStatus.ON_PL_PLANNING)),
                Arguments.arguments(4,
                        new Project()
                                .setId(4L)
                                .setName("SUPER PROJECT ")
                                .setCreateDate(Instant.parse("2010-02-09T11:49:03.839234Z"))
                                .setProjectStatus(ProjectStatus.ON_DL_APPROVING))
        );
    }

    @ParameterizedTest
    @MethodSource("getSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectRepositoryTest/data.sql"
    })
    void getSuccessTest(long id, final Project p) {
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(id)))
                .isPresent()
                .contains(p);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailArgs() {
        return Stream.of(
                Arguments.arguments(-1),
                Arguments.arguments(345678)
        );
    }

    @ParameterizedTest
    @MethodSource("getFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectRepositoryTest/data.sql"
    })
    void getFailTest(long projectId) {
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId)))
                .isNotPresent();
    }

}
