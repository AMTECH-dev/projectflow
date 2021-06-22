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

import java.util.List;
import java.util.stream.Stream;

import static io.amtech.projectflow.domain.employee.UserPosition.*;
import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/employees";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    private final static String PATH_TO_CONTROLLER_TEST = "classpath:db/EmployeeControllerTest/";
    private final static String INSERT_EMPLOYEES_FOR_UPDATE = PATH_TO_CONTROLLER_TEST +
            "updateTest/create_employee.sql";
    private final static String INSERT_EMPLOYEES_FOR_GET = PATH_TO_CONTROLLER_TEST +
            "getTest/create_employee.sql";
    private final static String INSERT_EMPLOYEES_FOR_SEARCH = PATH_TO_CONTROLLER_TEST +
            "insert_for_search.sql";
    private final static String INSERT_EMPLOYEES_FOR_DELETE = PATH_TO_CONTROLLER_TEST +
            "deleteTest/create_employee.sql";

    private final static String MAX_NAME_VALUE = strMultiple("a", 255);
    private final static String MAX_EMAIL_VALUE = strMultiple("b", 38) + "@example.com";
    private final static String MAX_PHONE_VALUE = strMultiple("1", 50);

    @Autowired
    private EmployeeRepository repository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("Иван Копыто")
                                .setEmail("kopito@example.com")
                                .setPhone("+7 128 123 12 12")
                                .setPosition(PROJECT_LEAD)
                ),
                Arguments.arguments(buildJson("createSuccessTest/without_phone_request.json"),
                        buildJson("createSuccessTest/without_phone_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("А")
                                .setEmail("a@b.ru")
                                .setPosition(DIRECTOR)
                ),
                Arguments.arguments(buildJson("default_request.json.template",
                        MAX_NAME_VALUE,
                        MAX_EMAIL_VALUE,
                        MAX_PHONE_VALUE,
                        PROJECT_LEAD.name()),
                        buildJson("default_response.json.template",
                                1,
                                MAX_NAME_VALUE,
                                MAX_EMAIL_VALUE,
                                MAX_PHONE_VALUE,
                                PROJECT_LEAD.name()),
                        new Employee()
                                .setId(1L)
                                .setName(MAX_NAME_VALUE)
                                .setEmail(MAX_EMAIL_VALUE)
                                .setPhone(MAX_PHONE_VALUE)
                                .setPosition(PROJECT_LEAD)
                )
        );
    }

    @ParameterizedTest(name = "{index} createSuccessTest")
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    void createSuccessTest(final String request, final String response, final Employee e) {
        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(1);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(e);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        final String longName = "extra" + MAX_NAME_VALUE;
        final String longEmail = "extra" + MAX_EMAIL_VALUE;
        final String longPhone = "extra" + MAX_PHONE_VALUE;

        return Stream.of(
                Arguments.arguments(buildJson("createFailTest/name_is_missing_request.json"),
                        buildJson("createFailTest/name_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("default_request.json.template",
                        longName, MAX_EMAIL_VALUE, MAX_PHONE_VALUE, PROJECT_LEAD),
                        buildJson("createFailTest/name_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("createFailTest/invalid_email_format_request.json"),
                        buildJson("createFailTest/invalid_email_format_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("default_request.json.template",
                        MAX_NAME_VALUE, longEmail, MAX_PHONE_VALUE, DIRECTION_LEAD),
                        buildJson("createFailTest/email_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("createFailTest/email_is_missing_request.json"),
                        buildJson("createFailTest/email_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("default_request.json.template",
                        MAX_NAME_VALUE, MAX_EMAIL_VALUE, longPhone, DIRECTION_LEAD),
                        buildJson("createFailTest/phone_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("createFailTest/without_position_request.json"),
                        buildJson("createFailTest/without_position_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("createFailTest/wrong_position_request.json"),
                        buildJson("createFailTest/wrong_position_response.json.template",
                                "I love my job", UserPosition.class.getSimpleName()),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(buildJson("createFailTest/empty_with_email_request.json"),
                        buildJson("createFailTest/empty_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} createFailTest")
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    void createFailTest(final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .isEmpty();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("",
                        buildJson("searchSuccessTest/all.json")),
                Arguments.arguments("?limit=3&offset=2&orders=-name",
                        buildJson("searchSuccessTest/reverse_order_with_limit_and_offset.json")),
                Arguments.arguments("?name=О",
                        buildJson("searchSuccessTest/filter_by_name.json")),
                Arguments.arguments("?email=safe",
                        buildJson("searchSuccessTest/email_contain_safe.json")),
                Arguments.arguments("?phone=9&limit=50",
                        buildJson("searchSuccessTest/phone_contain_9.json")),
                Arguments.arguments("?position=director&limit=1",
                        buildJson("searchSuccessTest/position_director.json"))
        );
    }

    @ParameterizedTest(name = "{index} searchSuccessTest")
    @MethodSource("searchSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_SEARCH)
    void searchSuccessTest(final String url, final String response) {
        mvc.perform(TestUtils.createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchFailArgs() {
        return Stream.of(
                Arguments.arguments("?orders=-some_field_name",
                        buildJson("searchFailTest/invalid_order_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments("?position=MEGA",
                        buildJson("searchFailTest/invalid_position_response.json"),
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest(name = "{index} searchFailTest")
    @MethodSource("searchFailArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_SEARCH)
    void searchFailTest(final String url, final String response, final int status) {
        mvc.perform(TestUtils.createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteSuccessTestArgs() {
        return Stream.of(Arguments.arguments(1, 1000), Arguments.arguments(2000));
    }

    @ParameterizedTest(name = "{index} deleteSuccessTest")
    @MethodSource("deleteSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_DELETE)
    void deleteSuccessTest(final long id) {
        List<Employee> employeesBeforeDelete = repository.findAll();

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(id))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(4);

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id))).isFalse();
        Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(3);
        Assertions.assertThat(employeesBeforeDelete.removeIf(x -> x.getId() == id)).isTrue();

        List<Employee> employeesAfterDelete = txUtil.txRun(() -> repository.findAll());
        for (Employee employee : employeesBeforeDelete)
            Assertions.assertThat(employeesAfterDelete.stream().filter(employee::equals).findFirst())
                    .isNotEmpty();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteFailTestArgs() {
        return Stream.of(
                Arguments.arguments(0, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(99, HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest(name = "{index} deleteFailTest")
    @MethodSource("deleteFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_DELETE)
    void deleteFailTest(final long id, final int httpStatus) {
        List<Employee> employeesBeforeDelete = repository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andDo(print())
                .andExpect(status().is(httpStatus));

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id))).isFalse();

        List<Employee> employeesAfterDelete = txUtil.txRun(() -> repository.findAll());
        for (Employee employee : employeesAfterDelete)
            Assertions.assertThat(employeesBeforeDelete.stream().filter(employee::equals).findFirst())
                    .isNotEmpty();

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(4);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(1, buildJson("getSuccessTest/first_employee_response.json"),
                        HttpStatus.OK.value()),
                Arguments.arguments(7, buildJson("getSuccessTest/7th_employee_response.json"),
                        HttpStatus.OK.value())
        );
    }

    @ParameterizedTest(name = "{index} getSuccessTest")
    @MethodSource("getSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_GET)
    void getSuccessTest(final long id, final String response, final int httpStatus) {
        Assertions.assertThat(txUtil.txRun(() -> repository.findById(id))).isPresent();

        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL, id)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(
                Arguments.arguments(99, buildJson("getFailTest/wrong_response.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(0, buildJson("getFailTest/wrong_response.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(-1, buildJson("getFailTest/wrong_response.json"),
                        HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest(name = "{index} getFailTest")
    @MethodSource("getFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_GET)
    void getFailTest(final long id, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL, id)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, false));

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id)))
                .isFalse();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        buildJson("updateSuccessTest/full_update_request.json"),
                        new Employee()
                                .setId(1L)
                                .setName("mr.Propper")
                                .setEmail("poshta@mail.ru")
                                .setPhone("+70002410182")
                                .setPosition(PROJECT_LEAD)
                                .setFired(true)
                ),
                Arguments.arguments(2L,
                        buildJson("updateSuccessTest/mail_fired_update_request.json"),
                        new Employee()
                                .setId(2L)
                                .setName("Кирилл")
                                .setEmail("some-email@example.com")
                                .setPhone("89992410182")
                                .setPosition(DIRECTOR)
                                .setFired(true)
                ),
                Arguments.arguments(3L,
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

    @ParameterizedTest(name = "{index} updateSuccessTest")
    @MethodSource("updateSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_UPDATE)
    void updateSuccessTest(final long id, final String request, final Employee employeeAfterUpdate) {
        Assertions.assertThat(txUtil.txRun(() -> repository.findById(id))).isPresent();

        List<Employee> employeesBeforeUpdate = repository.findAll();

        mvc.perform(TestUtils
                .createPut(String.format(BASE_ID_URL, id))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());

        for (Employee employeeBeforeUpdate : employeesBeforeUpdate) {
            if (employeeBeforeUpdate.getId() == id)
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(id)))
                        .get()
                        .isEqualTo(employeeAfterUpdate);
            else
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(employeeBeforeUpdate.getId())))
                        .get()
                        .isNotEqualTo(employeeAfterUpdate);
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateFailTestArgs() {
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
                        buildJson("updateFailTest/wrong_id_request.json"),
                        buildJson("updateFailTest/wrong_id_response.json"),
                        HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest(name = "{index} updateFailTest")
    @MethodSource("updateFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_EMPLOYEES_FOR_UPDATE)
    void updateFailTest(final long id, final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPut(String.format(BASE_ID_URL, id))
                .content(request))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/" + resource);

        return String.format(template, args);
    }
}
