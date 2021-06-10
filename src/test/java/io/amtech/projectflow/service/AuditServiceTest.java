package io.amtech.projectflow.service;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.domain.project.ProjectJournal;
import io.amtech.projectflow.repository.DirectionRepository;
import io.amtech.projectflow.repository.EmployeeRepository;
import io.amtech.projectflow.repository.ProjectJournalRepository;
import io.amtech.projectflow.repository.ProjectRepository;
import io.amtech.projectflow.service.audit.AuditService;
import io.amtech.projectflow.test.IntegrationTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

class AuditServiceTest extends IntegrationTest {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DirectionRepository directionRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private AuditService auditService;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1, 1, new Project()
                        .setName("EWQ")
                        .setCreateDate(Instant.now()), "Vlad")
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/AuditServiceTest/createSuccessTest/data.sql"
    })
    @Transactional
    void createSuccessTest(long projectLeadId, long directionId, final Project project,
                           final String login) {
        Optional<Employee> lead = txUtil.txRun(() -> employeeRepository.findById(projectLeadId));
        Assertions.assertThat(lead)
                .isPresent();

        Optional<Direction> direction = txUtil.txRun(() -> directionRepository.findById(directionId));
        Assertions.assertThat(direction)
                .isPresent();

        project.setDirection(direction.get())
                .setProjectLead(lead.get());

        txUtil.txRun(() -> projectRepository.save(project));
        txUtil.txRun(() -> auditService.save(login, project));
        Optional<Project> savedProject = txUtil.txRun(() -> projectRepository.findById(project.getId()));
        Assertions.assertThat(savedProject)
                .isPresent();

        Assertions.assertThat(txUtil.txRun(() -> savedProject.get().getHistory()))
                .hasSize(1);
    }
}
