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

import static io.amtech.projectflow.domain.employee.UserPosition.DIRECTOR;
import static io.amtech.projectflow.domain.employee.UserPosition.PROJECT_LEAD;
import static io.amtech.projectflow.test.TestUtils.strMultiple;
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
                Arguments.arguments(buildJson("default_request.json.template",
                        fakeName,
                        fakeEmail,
                        fakePhone,
                        PROJECT_LEAD.name()),
                        buildJson("default_response.json.template",
                                1,
                                fakeName,
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
            "classpath:db/clean.sql",
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
                Arguments.arguments(1,
                        buildJson("updateSuccessTest/full_update_request.json"),
                        new Employee()
                                .setId(1L)
                                .setName("mr.Propper")
                                .setEmail("poshta@mail.ru")
                                .setPhone("+70002410182")
                                .setPosition(PROJECT_LEAD)
                                .setFired(true)
                ),
                Arguments.arguments(2,
                        buildJson("updateSuccessTest/mail_fired_update_request.json"),
                        new Employee()
                                .setId(2L)
                                .setName("Кирилл")
                                .setEmail("some-email@example.com")
                                .setPhone("89992410182")
                                .setPosition(DIRECTOR)
                                .setFired(true)
                ),
                Arguments.arguments(3,
                        buildJson("updateSuccessTest/phone_position_update_request.json"),
                        new Employee()
                                .setId(3L)
                                .setName("Кирилл")
                                .setEmail("kira@example.com")
                                .setPhone("89991112233")
                                .setPosition(PROJECT_LEAD)
                                .setFired(false)
                )
        );
    }

    @ParameterizedTest
    @MethodSource("updateSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/EmployeeControllerTest/updateSuccessTest/createEmployees.sql"})
    void updateSuccessTest(final long id, final String request, final Employee expect) {
        List<Employee> existEmpBefore = repository.findAll();

        mvc.perform(TestUtils
                .createPut(BASE_URL + "/" + id)
                .content(request))
                .andExpect(status().isOk());


        for (Employee before : existEmpBefore) {
            if (before.getId() == id) {

                Assertions.assertThat(repository.findById(id))
                        .isPresent()
                        .get()
                        .isEqualTo(expect);

                for (Employee employee : repository.findAll()) {
                    if (employee.getId() != id) {
                        Assertions.assertThat(repository.findById(employee.getId()))
                                .isPresent()
                                .get()
                                .isNotEqualTo(expect);
                    }
                }

            }
        }
    }


    // failed update:
    static Stream<Arguments> updateFailedArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        buildJson("updateFailTest/update_request_fired_is_null.json"),
                        buildJson("updateFailTest/update_fired_is_null_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(2L,
                        buildJson("updateFailTest/update_request_empty_failed.json"),
                        buildJson("updateFailTest/update_empty_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(102L,
                        buildJson("updateFailTest/unexisting_id_call_failed.json"),
                        buildJson("updateFailTest/unexisting_id_call_failed_response.json"),
                        HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest
    @MethodSource("updateFailedArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/EmployeeControllerTest/updateSuccessTest/createEmployees.sql"})
    void updateFailTest(final Long id, final String request, final String response, int expectedStatus) {
        List<Employee> existEmpBefore = repository.findAll();

        mvc.perform(TestUtils
                .createPut(BASE_URL + "/" + id)
                .content(request))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().json(response, true));
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/" + resource);

        return String.format(template, args);
    }
}