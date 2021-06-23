package io.amtech.projectflow.rest.direction;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.UserPosition;
import io.amtech.projectflow.repository.DirectionRepository;
import io.amtech.projectflow.repository.EmployeeRepository;
import io.amtech.projectflow.test.IntegrationTest;
import io.amtech.projectflow.test.TestUtils;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static io.amtech.projectflow.util.ConvertingUtil.secondToInstant;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DirectionControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/directions";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    @Autowired
    private DirectionRepository repository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final String MAX_NAME_VALUE = org.apache.commons.lang3.StringUtils.leftPad("", 255, "n");

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        Employee lead=new Employee().setId(22L).setName("Пётр Петров").setEmail("petr@gmail.com")
                .setPosition(UserPosition.DIRECTION_LEAD)
                .setPhone("222222222").setFired(false);

        return Stream.of(
                Arguments.arguments("", buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName("Основной Direction")
                                .setLead(lead)
                ),
                Arguments.arguments("empty_nullable_params",
                        buildJson("createSuccessTest/empty_nullable_params_request.json"),
                        buildJson("createSuccessTest/empty_nullable_params_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName("Основной Direction")
                                .setLead(lead)
                        ),
                Arguments.arguments("max_length",
                        buildJson("createSuccessTest/max_length_request.json"),
                        buildJson("createSuccessTest/max_length_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName(MAX_NAME_VALUE)
                                .setLead(lead)
                        )
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void createSuccessTest(@SuppressWarnings("unused") final String name, final String request, final String response, final Direction d) {
        mvc.perform(TestUtils
                .createPost(BASE_URL)//changeProjectIdInUrl(projectId))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(4);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(d);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(
                Arguments.arguments("name_is_too_long",
                        buildJson("createFailTest/name_is_too_long_request.json"),
                        buildJson("createFailTest/name_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "create_fail_empty_request",
                        buildJson("createFailTest/empty_request.json"),
                        buildJson("createFailTest/empty_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    void createFailTest(@SuppressWarnings("unused") String testName,
                        final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .isEmpty();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "get_success_first_object", 100L,
                        buildJson("getSuccessTest/get_first_object_response.json"),
                        HttpStatus.OK.value()
                ),
                Arguments.arguments(
                        "get_success_last_object", 300L,
                        buildJson("getSuccessTest/get_last_object_response.json"),
                        HttpStatus.OK.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void getSuccessTest(@SuppressWarnings("unused") final String testName, final long directionId,
                        final String response, final int httpStatus) {
        Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId))).isPresent();

        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "get_fail_wrong_direction_id", 11L, -1,
                        buildJson("getFailTest/wrong_direction_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                )/*,
                Arguments.arguments(
                        "get_fail_wrong_employee_id", 1111L, 0L,
                        buildJson("getFailTest/wrong_direction_id_response.json"),
                        buildJson("getFailTest/wrong_employee_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                )*/
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void getFailTest(@SuppressWarnings("unused") final String testName, final long employeetId,
                     final long directionId, final String response, final int httpStatus) {
        Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(employeetId).isPresent()));

        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, false));

        if (txUtil.txRun(() -> employeeRepository.findById(employeetId).isPresent()))
            Assertions.assertThat(txUtil.txRun(() -> repository.existsById(directionId)))
                    .isFalse();
        else Assertions.assertThat(txUtil.txRun(() -> repository.existsById(directionId)))
                .isFalse();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateSuccessTestArgs() {
        Employee lead=new Employee().setId(22L).setName("Пётр Петров").setEmail("petr@gmail.com")
                .setPosition(UserPosition.DIRECTION_LEAD)
                .setPhone("222222222").setFired(false);

        return Stream.of(
                Arguments.arguments(
                        "update_success_full_update", 300L, 22L,
                        buildJson("updateSuccessTest/full_update_request.json"),
                        new Direction()
                                .setId(22L)
                                .setName("Дополнительный Direction")
                                .setLead(lead)
                )/*,
                Arguments.arguments(
                        "update_success_with_null_request", 22L, 22L,
                        buildJson("updateSuccessTest/update_with_null_request.json"),
                        new Direction()
                                .setId(22L)
                                //.setName("Разработка проектной документации")
                                .setLead(lead)
                )*/
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("updateSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void updateSuccessTest(@SuppressWarnings("unused") final String testName, final long directionId,
                           final long employeeId,final String request, final Direction directionAfterUpdate) {
        Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(employeeId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId))).isPresent();

        List<Direction> directionsBeforeUpdate = repository.findAll();

        /*mvc.perform(TestUtils
                .createPut(String.format(BASE_ID_URL, directionId))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());

        /*for (Direction directionBeforeUpdate : directionsBeforeUpdate) {
            if (directionBeforeUpdate.getId() == directionId)
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId)))
                        .get()
                        .isEqualTo(directionAfterUpdate);
            else
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionBeforeUpdate.getId())))
                        .get()
                        .isNotEqualTo(directionAfterUpdate);
        }*/
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateFailTestArgs() {
        return Stream.of(
                /*Arguments.arguments(
                        "update_fail_strings_are_too_long", 300L,
                        buildJson("updateFailTest/strings_are_too_long_request_update.json"),
                        buildJson("updateFailTest/strings_are_too_long_response_update.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                /*Arguments.arguments(
                        "update_fail_without_not_nullable_params", 300L,
                        buildJson("updateFailTest/without_not_nullable_params_request.json"),
                        buildJson("updateFailTest/without_not_nullable_params_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),*/
                Arguments.arguments(
                        "update_fail_wrong_id", 1111L,
                        buildJson("updateFailTest/wrong_id_request.json"),
                        buildJson("updateFailTest/wrong_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("updateFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void updateFailTest(@SuppressWarnings("unused") final String testName, final long directionId,
                        final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPut(String.format(BASE_ID_URL, directionId))
                .content(request))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    @Test
    @DisplayName("delete_success_test")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void deleteSuccessTest() {
        final long directionId = 300L;
        final long employeeId = 66L;

        Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(employeeId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(3);

        List<Direction> milestonesBeforeDelete = repository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(directionId))).isFalse();
        Assertions.assertThat(milestonesBeforeDelete.removeIf(m -> m.getId() == directionId)).isTrue();

        List<Direction> milestonesAfterDelete = txUtil.txRun(() -> repository.findAll());
        Assertions.assertThat(milestonesAfterDelete).hasSize(2);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteFailTestArgs() {
        return Stream.of(
                Arguments.arguments("delete_fail_zero_id", 22L, 0L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_negative_id", 22L, -1L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_unknown_id", 22L, 1111L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_wrong_employee_id", 1111L, 1111L, HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("deleteFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void deleteFailTest(@SuppressWarnings("unused") final String testName, final long employeeId,
                        final long directionId, final int httpStatus) {
        if (txUtil.txRun(() -> employeeRepository.findById(employeeId).isPresent())) {
            Assertions.assertThat(txUtil.txRun(() -> repository.existsById(directionId))).isFalse();
            Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(3);
        }

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().is(httpStatus));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(3);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "search_success_all", 22L,
                        "",
                        buildJson("searchSuccessTest/all_directions_response.json")
                ),
                Arguments.arguments(
                        "search_success_all_with_limit_and_offset", 33L,
                        "?limit=3&offset=1&orders=-name",
                        buildJson("searchSuccessTest/reverse_order_with_limit_and_offset_response.json")
                ),
                Arguments.arguments(
                        "search_success_by_name", 55L,
                        "?name=А",
                        buildJson("searchSuccessTest/filter_by_name_response.json")
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("searchSuccessTestArgs")
    @SneakyThrows
    @Sql("classpath:db/" +
            "DirectionControllerTest/insert_directions_for_custom_search.sql")
    void searchSuccessTest(@SuppressWarnings("unused") final String testName, final long employeeId,
                           final String url, final String response) {
        Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(employeeId))).isPresent();

        mvc.perform(TestUtils.createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchFailTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "search_fail_wrong_field_name", 11L,
                        "?orders=wrong_field_name",
                        buildJson("searchFailTest/invalid_order_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "search_fail_wrong_employee_id", 1111L,
                        "?orders=-name",
                        buildJson("searchFailTest/wrong_employee_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("searchFailTestArgs")
    @SneakyThrows
    @Sql("classpath:db/" +
            "DirectionControllerTest/insert_directions_for_custom_search.sql")
    void searchFailTest(@SuppressWarnings("unused") final String testName, final long employeeId,
                        final String url, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/DirectionControllerTest/" + resource);

        return String.format(template, args);
    }

    /*private String changeProjectIdInUrl(final long projectId) {
        return DirectionControllerTest.BASE_URL.replace("{Id}", String.valueOf(projectId));
    }*/
    /*private String putIdInUrl(final String url, final long projectId) {
        return url.replace("{projectId}", String.valueOf(projectId));
    }*/
}