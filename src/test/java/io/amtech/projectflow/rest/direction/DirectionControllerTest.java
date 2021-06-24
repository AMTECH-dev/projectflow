package io.amtech.projectflow.rest.direction;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.domain.employee.UserPosition;
import io.amtech.projectflow.repository.DirectionRepository;
import io.amtech.projectflow.test.IntegrationTest;
import io.amtech.projectflow.test.TestUtils;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DirectionControllerTest extends IntegrationTest {
    private static final String MAX_NAME_VALUE = TestUtils.strMultiple("n", 255);
    private static final Employee LEAD = new Employee()
            .setId(22L)
            .setName("Пётр Петров")
            .setEmail("petr@gmail.com")
            .setPosition(UserPosition.DIRECTION_LEAD)
            .setPhone("222222222").setFired(false);
    private static final Employee NEW_LEAD = new Employee()
            .setId(33L)
            .setName("Сидор Сидоров")
            .setEmail("sidor@gmail.com")
            .setPosition(UserPosition.DIRECTION_LEAD)
            .setPhone("333333333").setFired(false);

    private static final String BASE_URL = "/directions";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    @Autowired
    private DirectionRepository repository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("full_params", buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName("Основной Direction")
                                .setLead(LEAD)),
                Arguments.arguments("empty_nullable_params",
                        buildJson("createSuccessTest/some_params_request.json"),
                        buildJson("createSuccessTest/some_params_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName("Основной Direction")
                                .setLead(LEAD)),
                Arguments.arguments("max_length",
                        buildJson("createSuccessTest/max_length_request.json"),
                        buildJson("createSuccessTest/max_length_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName(MAX_NAME_VALUE)
                                .setLead(LEAD))
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/directions_are_exist.sql"
    })
    void createSuccessTest(@SuppressWarnings("unused") final String name, final String request, final String response, final Direction d) {
        mvc.perform(TestUtils
                .createPost(BASE_URL)
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
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments("lead_not_found",
                        buildJson("createFailTest/lead_not_found_request.json"),
                        buildJson("createFailTest/lead_not_found_response.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("lead_is_null",
                        buildJson("createFailTest/lead_is_null_request.json"),
                        buildJson("createFailTest/lead_is_null_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(
                        "create_fail_empty_request",
                        buildJson("createFailTest/empty_request.json"),
                        buildJson("createFailTest/empty_response.json"),
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    void createFailTest(@SuppressWarnings("unused") String testName, final String request, final String response, final int httpStatus) {
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
                        HttpStatus.OK.value()),
                Arguments.arguments(
                        "get_success_last_object", 300L,
                        buildJson("getSuccessTest/get_last_object_response.json"),
                        HttpStatus.OK.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/directions_are_exist.sql"
    })
    void getSuccessTest(@SuppressWarnings("unused") final String testName, final long directionId,
                        final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId)))
                .isPresent();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(
                Arguments.arguments("get_fail_wrong_direction_id", -1,
                        buildJson("getFailTest/wrong_direction_id_response.json"),
                        HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/directions_are_exist.sql"
    })
    void getFailTest(@SuppressWarnings("unused") final String testName, final long directionId, final String response,
                     final int httpStatus) {
        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, false));

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId)))
                .isNotPresent();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("update_success_full_update", 300L,
                        buildJson("updateSuccessTest/full_update_request.json"),
                        new Direction()
                                .setId(300L)
                                .setName("Дополнительный Direction")
                                .setLead(LEAD)),
                Arguments.arguments("update_leadId_success_full_update", 300L,
                        buildJson("updateSuccessTest/full_update_leadId_request.json"),
                        new Direction()
                                .setId(300L)
                                .setName("Дополнительный Direction")
                                .setLead(NEW_LEAD))
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("updateSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/directions_are_exist.sql"
    })
    void updateSuccessTest(@SuppressWarnings("unused") final String testName, final long directionId,
                           final String request, final Direction directionAfterUpdate) {
        Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId)))
                .isPresent();

        List<Direction> directionsBeforeUpdate = repository.findAll();

        mvc.perform(TestUtils
                .createPut(String.format(BASE_ID_URL, directionId))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());

        for (Direction directionBeforeUpdate : directionsBeforeUpdate) {
            if (directionBeforeUpdate.getId() == directionId)
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId)))
                        .isPresent()
                        .contains(directionAfterUpdate);
            else
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionBeforeUpdate.getId())))
                        .isPresent()
                        .get()
                        .isNotEqualTo(directionAfterUpdate);
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateFailTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "update_fail_strings_are_too_long", 300L,
                        buildJson("updateFailTest/strings_are_too_long_request_update.json"),
                        buildJson("updateFailTest/strings_are_too_long_response_update.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "update_fail_empty_params", 300L,
                        buildJson("updateFailTest/empty_request.json"),
                        buildJson("updateFailTest/empty_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "update_fail_wrong_id", 300L,
                        buildJson("updateFailTest/wrong_lead_id_request.json"),
                        buildJson("updateFailTest/wrong_lead_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                ),
                Arguments.arguments(
                        "update_fail_null_lead_id", 300L,
                        buildJson("updateFailTest/null_lead_id_request.json"),
                        buildJson("updateFailTest/null_lead_id_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("updateFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/directions_are_exist.sql"
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

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("delete_one_direction", 100L, HttpStatus.OK.value()),
                Arguments.arguments("delete_one_direction", 200L, HttpStatus.OK.value()),
                Arguments.arguments("delete_one_direction", 300L, HttpStatus.OK.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("deleteSuccessTestArgs")
    @DisplayName("delete_success_test")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/directions_are_exist.sql"
    })
    void deleteSuccessTest(@SuppressWarnings("unused") final String testName, final long directionId, final int httpStatus) {
        Assertions.assertThat(txUtil.txRun(() -> repository.findById(directionId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(3);

        List<Direction> directionsBeforeDelete = repository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().is(httpStatus));

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(directionId))).isFalse();
        Assertions.assertThat(directionsBeforeDelete.removeIf(m -> m.getId() == directionId)).isTrue();

        List<Direction> directionsAfterDelete = txUtil.txRun(() -> repository.findAll());
        Assertions.assertThat(directionsAfterDelete).hasSize(2);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteFailTestArgs() {
        return Stream.of(
                Arguments.arguments("delete_fail_zero_id", 0L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_negative_id", -1L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_unknown_id", 1111L, HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("deleteFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/DirectionControllerTest/directions_are_exist.sql"
    })
    void deleteFailTest(@SuppressWarnings("unused") final String testName, final long directionId, final int httpStatus) {
        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, directionId)))
                .andDo(print())
                .andExpect(status().is(httpStatus));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll())).hasSize(3);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("search_success_all", "",
                        buildJson("searchSuccessTest/all_directions_response.json")),
                Arguments.arguments("search_success_all_with_limit_and_offset", "?limit=3&offset=1&orders=-name",
                        buildJson("searchSuccessTest/reverse_order_with_limit_and_offset_response.json")),
                Arguments.arguments("search_success_by_name", "?name=W",
                        buildJson("searchSuccessTest/filter_by_name_response.json"))
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("searchSuccessTestArgs")
    @SneakyThrows
    @Sql("classpath:db/DirectionControllerTest/insert_directions_for_custom_search.sql")
    void searchSuccessTest(@SuppressWarnings("unused") final String testName,
                           final String url, final String response) {

        mvc.perform(TestUtils.createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchFailTestArgs() {
        return Stream.of(
                Arguments.arguments("search_fail_wrong_field_name", "?orders=wrong_field_name",
                        buildJson("searchFailTest/invalid_order_response.json"),
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("searchFailTestArgs")
    @SneakyThrows
    @Sql("classpath:db/DirectionControllerTest/insert_directions_for_custom_search.sql")
    void searchFailTest(@SuppressWarnings("unused") final String testName,
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
}