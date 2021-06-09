package io.amtech.projectflow.repository;

import io.amtech.projectflow.app.exception.ObjectNotFoundException;
import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.test.IntegrationTest;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;
import java.util.stream.Stream;

class DirectionRepositoryTest extends IntegrationTest {
    @Autowired
    private DirectionRepository directionRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessArgs() {
        return Stream.of(
                Arguments.arguments("dev", 1, new Direction()
                        .setId(1L)
                        .setName("dev")),
                Arguments.arguments("test", 10, new Direction()
                        .setId(1L)
                        .setName("test"))
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionRepositoryTest/data.sql"
    })
    void createSuccessTest(final String name, long leadId, final Direction d) {
        Optional<Employee> optionalEmployee = txUtil.txRun(() -> employeeRepository.findById(leadId));
        Assertions.assertThat(optionalEmployee)
                .isPresent();

        Direction direction = new Direction()
                .setName(name)
                .setLead(optionalEmployee.get());
        txUtil.txRun(() -> directionRepository.save(direction));

        Assertions.assertThat(txUtil.txRun(() -> directionRepository.findAll()))
                .hasSize(3);
        Assertions.assertThat(d)
                .isEqualTo(direction);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailArgs() {
        return Stream.of(
                Arguments.arguments("HELLO WORLD!", -1, ObjectNotFoundException.class),
                Arguments.arguments(null, 1, DataIntegrityViolationException.class)
        );
    }

    @ParameterizedTest
    @MethodSource("createFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionRepositoryTest/data.sql"
    })
    void createFailTest(final String name, long leadId, final Class<?> expectedExceptionClass) {
        Direction direction = new Direction()
                .setName(name);
        Assertions.assertThatThrownBy(() -> {
            Employee lead = txUtil.txRun(() -> employeeRepository.findById(leadId))
                    .orElseThrow(() -> new ObjectNotFoundException("Employee", leadId));
            direction.setLead(lead);
            txUtil.txRun(() -> directionRepository.save(direction));
        }).isInstanceOf(expectedExceptionClass);

        Assertions.assertThat(txUtil.txRun(() -> directionRepository.findAll()))
                .hasSize(2);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessArgs() {
        return Stream.of(
                Arguments.arguments(2,
                        new Direction()
                                .setId(2L)
                                .setName("dev")),
                Arguments.arguments(10, new Direction()
                        .setId(10L)
                        .setName("test"))
        );
    }

    @ParameterizedTest
    @MethodSource("getSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionRepositoryTest/data.sql"
    })
    void getSuccessTest(long id, final Direction d) {
        Assertions.assertThat(txUtil.txRun(() -> directionRepository.findById(id)))
                .isPresent()
                .contains(d);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailArgs() {
        return Stream.of(
                Arguments.arguments(0),
                Arguments.arguments(-1)
        );
    }

    @ParameterizedTest
    @MethodSource("getFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionRepositoryTest/data.sql"
    })
    void getFailTest(long id) {
        Assertions.assertThat(txUtil.txRun(() -> directionRepository.findById(id)))
                .isNotPresent();
    }
}