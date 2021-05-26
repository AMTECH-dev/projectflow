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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static io.amtech.projectflow.domain.employee.UserPosition.DIRECTOR;
import static io.amtech.projectflow.domain.employee.UserPosition.PROJECT_LEAD;
import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/employees";
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


    // success update:
    static Stream<Arguments> updateSuccessArgs() {
        return Stream.of(
                Arguments.arguments(
                        buildJson("updateSuccessTest/full_update_request.json")
                ),
                Arguments.arguments(
                        buildJson("updateSuccessTest/mail_fired_update_request.json")
                ),
                Arguments.arguments(
                        buildJson("updateSuccessTest/phone_position_update_request.json")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("updateSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/EmployeeControllerTest/updateSuccessTest/createEmployees.sql"})
    void updateSuccessTest(final String request) {
        List<Employee> existEmpBefore = repository.findAll();

        ArrayList<Long> ids = new ArrayList<Long>();
        for (Employee employee : existEmpBefore) {
            ids.add(employee.getId());
        }

        for (Long idSuccess : ids) {
            final Optional<Employee> BEFORE = repository.findById(idSuccess);

            mvc.perform(TestUtils
                    .createPut(BASE_URL + "/" + idSuccess)
                    .content(request))
                    .andExpect(status().isOk());

            final Optional<Employee> AFTER = repository.findById(idSuccess);

            Assertions.assertThat(BEFORE).isNotEqualTo(AFTER);
        }
    }


    // failed update:
    static Stream<Arguments> updateFailedArgs() {
        return Stream.of(
                Arguments.arguments(
                        buildJson("updateFailTest/update_request_fired_is_null.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(
                        buildJson("updateFailTest/update_request_empty_failed.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(
                        buildJson("updateFailTest/unexisting_id_call_failed.json"),
                        HttpStatus.NOT_FOUND.value())
        );
    }
    static int testCounter = 0;

    @ParameterizedTest
    @MethodSource("updateFailedArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/EmployeeControllerTest/updateSuccessTest/createEmployees.sql"})
    void updateFailTest(final String request, int expectedStatus) {
        List<Employee> existEmpBefore = repository.findAll();

        ArrayList<Long> ids = new ArrayList<Long>();
        for (Employee employee : existEmpBefore) {
            ids.add(employee.getId());
        }

        testCounter++;
        System.out.println("#" + testCounter);
        for (Long idFail : ids) {
            final Optional<Employee> BEFORE = repository.findById(idFail);

            mvc.perform(TestUtils
                    .createPut(BASE_URL + "/" + (testCounter == 3 ? idFail + 99 : idFail))
                    .content(request))
                    .andExpect(status().is(expectedStatus));

            final Optional<Employee> AFTER = repository.findById(idFail);

            Assertions.assertThat(BEFORE).isEqualTo(AFTER);
        }
    }

    private static String buildJson(final String resource, Object...args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/" + resource);

        return String.format(template, args);
    }
}