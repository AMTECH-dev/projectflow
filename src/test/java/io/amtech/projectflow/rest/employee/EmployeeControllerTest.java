package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.UserPosition;
import io.amtech.projectflow.repository.EmployeeRepository;
import io.amtech.projectflow.test.IntegrationTest;
import io.amtech.projectflow.test.TestUtils;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.List;
import java.util.stream.Stream;

import static io.amtech.projectflow.domain.employee.UserPosition.DIRECTOR;
import static io.amtech.projectflow.domain.employee.UserPosition.PROJECT_LEAD;
import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/employees";
    private static final String BASE_ID_URL = BASE_URL + "/%d";
    @Autowired
    EmployeeRepository repository;

    static Stream<Arguments> createSuccessTestArgs() {
        final String fakeName = strMultiple("a", 255);
        final String fakeEmail = strMultiple("b", 38) + "@example.com";
        final String fakePhone = strMultiple("1", 50);
        return Stream.of(
                Arguments.arguments(buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("Иван Копыто")
                                .setEmail("kopito@example.com")
                                .setPhone("+7 128 123 12 12")
                                .setPosition(PROJECT_LEAD)),
                Arguments.arguments(buildJson("createSuccessTest/without_phone_request.json"),
                        buildJson("createSuccessTest/without_phone_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("А")
                                .setEmail("a@b.ru")
                                .setPosition(DIRECTOR)),
                Arguments.arguments(buildJson("default_request.json.template", fakeName,
                        fakeEmail,
                        fakePhone,
                        PROJECT_LEAD.name()),
                        buildJson("createSuccessTest/default_response.json.template", 1, fakeName,
                                fakeEmail,
                                fakePhone,
                                PROJECT_LEAD.name()),
                        new Employee()
                                .setId(1L)
                                .setName(fakeName)
                                .setEmail(fakeEmail)
                                .setPhone(fakePhone)
                                .setPosition(PROJECT_LEAD))
                );
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/createSuccessTest/exists_employees.sql"
    })
    void createSuccessTest(final String request, final String response, final Employee e) {
        // setup

        // when
        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        // then
        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(3);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(e);
    }

    static Stream<Arguments> createFailTestArgs() {
        final String fakeName = strMultiple("a", 256);
        return Stream.of(
                Arguments.arguments(buildJson("createFailTest/name_is_missing_request.json"),
                        buildJson("createFailTest/name_is_missing_response.json"),
                        400),
                Arguments.arguments(buildJson("default_request.json.template",
                        fakeName, "sd@mail.com", "293 3993 93939", PROJECT_LEAD),
                        buildJson("createFailTest/name_is_too_long_response.json"),
                        400)
        );
    }

    static Stream<Arguments> deleteSuccessTestArgs() {
        return Stream.of(Arguments.arguments(1, 1000), Arguments.arguments(2000));
    }

    @ParameterizedTest
    @MethodSource("deleteSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/deleteTest/create_employee.sql"
    })
    void deleteSuccessTest(long id) {
        List<Employee> employeesBeforeDelete = repository.findAll();

        // setup
        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andExpect(status().isOk());

        // then
        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id)))
                .isFalse();

        Assertions.assertThat(employeesBeforeDelete.removeIf(x -> x.getId() == id)).isTrue();
        List<Employee> employeesAfterDelete = txUtil.txRun(() -> repository.findAll());
        for (Employee employee : employeesBeforeDelete) {
            Assertions.assertThat(employeesAfterDelete.stream().filter(employee::equals).findFirst())
                    .isNotEmpty();
        }
    }

    static Stream<Arguments> deleteFailTestArgs() {
        return Stream.of(Arguments.arguments(0, HttpStatus.NOT_FOUND.value()), Arguments.arguments(99, HttpStatus.NOT_FOUND.value()));
    }

    @ParameterizedTest
    @MethodSource("deleteFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/deleteTest/create_employee.sql"
    })
    void deleteFailTest(long id, int httpStatus) {
        List<Employee> employeesBeforeDelete = repository.findAll();

        // setup
        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andExpect(status().is(httpStatus));

        // then
        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id)))
                .isFalse();

        List<Employee> employeesAfterDelete = txUtil.txRun(() -> repository.findAll());
        for (Employee employee : employeesAfterDelete) {
            Assertions.assertThat(employeesBeforeDelete.stream().filter(employee::equals).findFirst())
                    .isNotEmpty();
        }
    }

    static Stream<Arguments> getSuccessTestArgs() {
        return Stream.of(Arguments.arguments(1, buildJson("getSuccessTest/full_response.json"), 200));
    }

    @ParameterizedTest
    @MethodSource("getSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/getTest/create_employee.sql"
    })
    void getSuccessTest(final long id, final String response, int httpStatus) {
        mvc.perform(TestUtils
                .createGet(BASE_URL+"/"+id))
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(Arguments.arguments(99, buildJson("getSuccessTest/full_response.json"), 404));
    }

    @ParameterizedTest
    @MethodSource("getFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/getTest/create_employee.sql"
    })
    void getFailTest(final long id, final String response, int httpStatus) {
        mvc.perform(TestUtils
                .createGet(BASE_URL + "/" + id))
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, false));
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/" + resource);

        return String.format(template, args);
    }
}
