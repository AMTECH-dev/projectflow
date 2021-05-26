package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.domain.employee.Employee;
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

import java.util.List;
import java.util.stream.Stream;

import static io.amtech.projectflow.domain.employee.UserPosition.*;
import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/employees";
    private static final String BASE_ID_URL = BASE_URL + "/%d";
    @Autowired
    EmployeeRepository repository;

    private final static String MAX_NAME_VALUE = strMultiple("a", 255);
    private final static String MAX_EMAIL_VALUE = strMultiple("b", 38) + "@example.com";
    private final static String MAX_PHONE_VALUE = strMultiple("1", 50);

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
        final String longName = MAX_NAME_VALUE + 1;
        final String longEmail = 1 + MAX_EMAIL_VALUE;
        final String longPhone = MAX_PHONE_VALUE + 1;

        return Stream.of(
                Arguments.arguments(buildJson("createFailTest/name_is_missing_request.json"),
                        buildJson("createFailTest/name_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(buildJson("default_request.json.template",
                        longName, MAX_EMAIL_VALUE, MAX_PHONE_VALUE, PROJECT_LEAD),
                        buildJson("createFailTest/name_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()),

                Arguments.arguments(buildJson("createFailTest/invalid_email_format_request.json"),
                        buildJson("createFailTest/invalid_email_format_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(buildJson("default_request.json.template",
                        MAX_NAME_VALUE, longEmail, MAX_PHONE_VALUE, DIRECTION_LEAD),
                        buildJson("createFailTest/email_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(buildJson("createFailTest/email_is_missing_request.json"),
                        buildJson("createFailTest/email_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value()),

                Arguments.arguments(buildJson("default_request.json.template",
                        MAX_NAME_VALUE, MAX_EMAIL_VALUE, longPhone, DIRECTION_LEAD),
                        buildJson("createFailTest/phone_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()),

                Arguments.arguments(buildJson("createFailTest/without_position_request.json"),
                        buildJson("createFailTest/without_position_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(buildJson("createFailTest/wrong_position_request.json"),
                        buildJson("createFailTest/wrong_position_response.json"),
                        HttpStatus.BAD_REQUEST.value()),

                Arguments.arguments(buildJson("createFailTest/empty_with_email_request.json"),
                        buildJson("createFailTest/empty_response.json"),
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    void createFailTest(final String request, final String response, int httpStatus) {
        // setup
        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));

        // then
        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .isEmpty();
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

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/" + resource);

        return String.format(template, args);
    }
}
