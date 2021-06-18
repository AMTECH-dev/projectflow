package io.amtech.projectflow.rest.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.repository.MilestoneRepository;
import io.amtech.projectflow.repository.ProjectRepository;
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

import java.util.List;
import java.util.stream.Stream;

import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static io.amtech.projectflow.util.DateUtil.secondsToInstant;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MilestoneControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/projects/{projectId}/milestones";
    private static final String BASE_ID_URL = BASE_URL + "/%d";
    private static final String INSERT_MILESTONES_QUERY = "classpath:db/" +
            "MilestoneControllerTest/insert_milestones.sql";
    private static final String INSERT_MILESTONES_FOR_CUSTOM_SEARCH_QUERY = "classpath:db/" +
            "MilestoneControllerTest/insert_milestones_for_custom_search.sql";

    @Autowired
    private MilestoneRepository milestoneRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        final String maxNameValue = strMultiple("n", 255);
        final String maxDescriptionValue = strMultiple("d", 2048);

        return Stream.of(
                Arguments.arguments(
                        "create_success_full_request", 11L,
                        buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Milestone()
                                .setId(1L)
                                .setName("Разработка проектной документации")
                                .setDescription("Разработана документация проекта")
                                .setPlannedStartDate(secondsToInstant(1623320100L))
                                .setPlannedFinishDate(secondsToInstant(1623420101L))
                                .setFactStartDate(secondsToInstant(1623320100L))
                                .setFactFinishDate(secondsToInstant(1623420101L))
                                .setProgressPercent((short) 23)
                ),
                Arguments.arguments(
                        "create_success_empty_nullable_params", 11L,
                        buildJson("createSuccessTest/empty_nullable_params_request.json"),
                        buildJson("createSuccessTest/empty_nullable_params_response.json"),
                        new Milestone()
                                .setId(1L)
                                .setName("Печать комплекта документов")
                                .setPlannedStartDate(secondsToInstant(1623320100L))
                                .setPlannedFinishDate(secondsToInstant(1623420101L))
                                .setProgressPercent((short) 13)
                ),
                Arguments.arguments(
                        "create_success_max_length", 11L,
                        buildJson("createSuccessTest/max_length_request.json"),
                        buildJson("createSuccessTest/max_length_response.json"),
                        new Milestone()
                                .setId(1L)
                                .setName(maxNameValue)
                                .setDescription(maxDescriptionValue)
                                .setPlannedStartDate(secondsToInstant(1623320100L))
                                .setPlannedFinishDate(secondsToInstant(1623420101L))
                                .setProgressPercent((short) 13)
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void createSuccessTest(@SuppressWarnings("unused") final String testName, final long projectId,
                           final String request, final String response, final Milestone m) {
        mvc.perform(TestUtils
                .createPost(putIdInUrl(BASE_URL, projectId))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId)))
                .isPresent();
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findAll()))
                .hasSize(6);
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(1L)))
                .isPresent()
                .contains(m);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "create_fail_name_is_too_long", 11L,
                        buildJson("createFailTest/name_is_too_long_request.json"),
                        buildJson("createFailTest/name_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "create_fail_description_is_too_long", 11L,
                        buildJson("createFailTest/description_is_too_long_request.json"),
                        buildJson("createFailTest/description_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "create_fail_without_start_date", 11L,
                        buildJson("createFailTest/without_start_date_request.json"),
                        buildJson("createFailTest/without_start_date_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "create_fail_without_finish_date", 11L,
                        buildJson("createFailTest/without_finish_date_request.json"),
                        buildJson("createFailTest/without_finish_date_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "create_fail_empty_request", 11L,
                        buildJson("createFailTest/empty_request.json"),
                        buildJson("createFailTest/empty_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "create_fail_int_instead_short", 11L,
                        buildJson("createFailTest/int_instead_short_request.json"),
                        buildJson("createFailTest/int_instead_short_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "create_fail_wrong_project_id", 1111L,
                        buildJson("createFailTest/wrong_project_id_request.json"),
                        buildJson("createFailTest/wrong_project_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    void createFailTest(@SuppressWarnings("unused") final String testName, final long projectId,
                        final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPost(putIdInUrl(BASE_URL, projectId))
                .content(request))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findAll()))
                .isEmpty();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "get_success_first_object", 11L, 11L,
                        buildJson("getSuccessTest/get_first_object_response.json"),
                        HttpStatus.OK.value()
                ),
                Arguments.arguments(
                        "get_success_last_object", 55L, 55L,
                        buildJson("getSuccessTest/get_last_object_response.json"),
                        HttpStatus.OK.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void getSuccessTest(@SuppressWarnings("unused") final String testName, final long projectId,
                        final long milestoneId, final String response, final int httpStatus) {
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneId))).isPresent();

        mvc.perform(TestUtils
                .createGet(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "get_fail_wrong_milestone_id", 11L, -1,
                        buildJson("getFailTest/wrong_milestone_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                ),
                Arguments.arguments(
                        "get_fail_wrong_project_id", 1111, 0,
                        buildJson("getFailTest/wrong_project_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("getFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void getFailTest(@SuppressWarnings("unused") final String testName, final long projectId,
                     final long milestoneId, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createGet(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, false));

        if (txUtil.txRun(() -> projectRepository.findById(projectId).isPresent()))
            Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.existsById(milestoneId)))
                    .isFalse();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "update_success_full_update", 22L, 22L,
                        buildJson("updateSuccessTest/full_update_request.json"),
                        new Milestone()
                                .setId(22L)
                                .setName("Разработка проектной документации")
                                .setDescription("Разработана документация проекта")
                                .setPlannedStartDate(secondsToInstant(1623320100L))
                                .setPlannedFinishDate(secondsToInstant(1623420101L))
                                .setFactStartDate(secondsToInstant(1623320100L))
                                .setFactFinishDate(secondsToInstant(1623420101L))
                                .setProgressPercent((short) 50)
                ),
                Arguments.arguments(
                        "update_success_with_null_request", 22L, 22L,
                        buildJson("updateSuccessTest/update_with_null_request.json"),
                        new Milestone()
                                .setId(22L)
                                .setName("Разработка проектной документации")
                                .setPlannedStartDate(secondsToInstant(1623320100L))
                                .setPlannedFinishDate(secondsToInstant(1623420101L))
                                .setProgressPercent((short) 40)
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("updateSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void updateSuccessTest(@SuppressWarnings("unused") final String testName, final long projectId,
                           final long milestoneId, final String request, final Milestone milestoneAfterUpdate) {
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneId))).isPresent();

        List<Milestone> milestonesBeforeUpdate = milestoneRepository.findAll();

        mvc.perform(TestUtils
                .createPut(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());

        for (Milestone milestoneBeforeUpdate : milestonesBeforeUpdate) {
            if (milestoneBeforeUpdate.getId() == milestoneId)
                Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneId)))
                        .get()
                        .isEqualTo(milestoneAfterUpdate);
            else
                Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneBeforeUpdate.getId())))
                        .get()
                        .isNotEqualTo(milestoneAfterUpdate);
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateFailTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "update_fail_strings_are_too_long", 11L, 11L,
                        buildJson("updateFailTest/strings_are_too_long_request_update.json"),
                        buildJson("updateFailTest/strings_are_too_long_response_update.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "update_fail_without_not_nullable_params", 11L, 11L,
                        buildJson("updateFailTest/without_not_nullable_params_request.json"),
                        buildJson("updateFailTest/without_not_nullable_params_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(
                        "update_fail_wrong_id", 11L, 1111L,
                        buildJson("updateFailTest/wrong_id_request.json"),
                        buildJson("updateFailTest/wrong_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                ),
                Arguments.arguments(
                        "update_fail_percent_out_of_range", 11L, 11L,
                        buildJson("updateFailTest/percent_out_of_range_request.json"),
                        buildJson("updateFailTest/percent_out_of_range_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("updateFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void updateFailTest(@SuppressWarnings("unused") final String testName, final long projectId, final long milestoneId,
                        final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPut(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId))
                .content(request))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    @Test
    @DisplayName("update_progress_success")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void updateProgressSuccessTest() {
        final long projectId = 11L;
        final long milestoneId = 11L;
        final String request = buildJson("updateProgressSuccessTest/update_progress_success_request.json");
        final Milestone milestoneAfterUpdate = new Milestone()
                .setId(11L)
                .setName("do sth really important 1")
                .setDescription("")
                .setPlannedStartDate(secondsToInstant(1623320100L))
                .setPlannedFinishDate(secondsToInstant(1623420101L))
                .setProgressPercent((short) 45);

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneId))).isPresent();

        List<Milestone> milestonesBeforeUpdate = milestoneRepository.findAll();

        mvc.perform(TestUtils
                .createPatch(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());

        for (Milestone milestoneBeforeUpdate : milestonesBeforeUpdate) {
            if (milestoneBeforeUpdate.getId() == milestoneId)
                Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneId)))
                        .get()
                        .isEqualTo(milestoneAfterUpdate);
            else
                Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneBeforeUpdate.getId())))
                        .get()
                        .isNotEqualTo(milestoneAfterUpdate);
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateProgressFailTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "update_progress_fail_wrong_id", 11L, 1111L,
                        buildJson("updateProgressFailTest/wrong_id_request.json"),
                        buildJson("updateProgressFailTest/wrong_id_response.json"),
                        HttpStatus.NOT_FOUND.value()
                ),
                Arguments.arguments(
                        "update_progress_fail_negative_number", 11L, 11L,
                        buildJson("updateProgressFailTest/negative_number_request.json"),
                        buildJson("updateProgressFailTest/negative_number_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("updateProgressFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void updateProgressFailTest(@SuppressWarnings("unused") final String testName, final long projectId, final long milestoneId,
                                final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPatch(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId))
                .content(request))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));
    }

    @Test
    @DisplayName("delete_success_test")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void deleteSuccessTest() {
        final long projectId = 11L;
        final long milestoneId = 11L;

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findById(milestoneId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findAll())).hasSize(5);

        List<Milestone> milestonesBeforeDelete = milestoneRepository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId)))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.existsById(milestoneId))).isFalse();
        Assertions.assertThat(milestonesBeforeDelete.removeIf(m -> m.getId() == milestoneId)).isTrue();

        List<Milestone> milestonesAfterDelete = txUtil.txRun(() -> milestoneRepository.findAll());
        Assertions.assertThat(milestonesAfterDelete).hasSize(4);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteFailTestArgs() {
        return Stream.of(
                Arguments.arguments("delete_fail_zero_id", 11L, 0L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_negative_id", 11L, -1L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_unknown_id", 11L, 1111L, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments("delete_fail_wrong_project_id", 1111L, 1111L, HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("deleteFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_QUERY)
    void deleteFailTest(@SuppressWarnings("unused") final String testName, final long projectId,
                        final long milestoneId, final int httpStatus) {
        if (txUtil.txRun(() -> projectRepository.findById(projectId).isPresent())) {
            Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.existsById(milestoneId))).isFalse();
            Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findAll())).hasSize(5);
        }

        mvc.perform(TestUtils
                .createDelete(String.format(putIdInUrl(BASE_ID_URL, projectId), milestoneId)))
                .andDo(print())
                .andExpect(status().is(httpStatus));

        Assertions.assertThat(txUtil.txRun(() -> milestoneRepository.findAll())).hasSize(5);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        "search_success_all", 11L,
                        "",
                        buildJson("searchSuccessTest/all_milestones_response.json")
                ),
                Arguments.arguments(
                        "search_success_all_with_limit_and_offset", 11L,
                        "?limit=3&offset=1&orders=-name",
                        buildJson("searchSuccessTest/reverse_order_with_limit_and_offset_response.json")
                ),
                Arguments.arguments(
                        "search_success_by_name", 11L,
                        "?name=А",
                        buildJson("searchSuccessTest/filter_by_name_response.json")
                ),
                Arguments.arguments(
                        "search_success_by_progress_percent", 11L,
                        "?progressPercent=50",
                        buildJson("searchSuccessTest/filter_by_progress_percent_response.json")
                ),
                Arguments.arguments(
                        "search_success_by_start_date", 11L,
                        "?plannedStartDateFrom=1623320100" +
                                "&plannedStartDateTo=1623420101" +
                                "&limit=2",
                        buildJson("searchSuccessTest/filter_by_start_date_response.json")
                ),
                Arguments.arguments(
                        "search_success_by_finish_date", 11L,
                        "?plannedFinishDateFrom=1623520100" +
                                "&plannedFinishDateTo=1623920101",
                        buildJson("searchSuccessTest/filter_by_finish_date_response.json")
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("searchSuccessTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_FOR_CUSTOM_SEARCH_QUERY)
    void searchSuccessTest(@SuppressWarnings("unused") final String testName, final long projectId,
                           final String url, final String response) {
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(projectId))).isPresent();

        mvc.perform(TestUtils.createGet(putIdInUrl(BASE_URL, projectId) + url))
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
                        "search_fail_wrong_field_name", -1L,
                        "?orders=-name",
                        null,
                        HttpStatus.NOT_FOUND.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("searchFailTestArgs")
    @SneakyThrows
    @Sql(INSERT_MILESTONES_FOR_CUSTOM_SEARCH_QUERY)
    void searchFailTest(@SuppressWarnings("unused") final String testName, final long projectId,
                        final String url, final String response, final int httpStatus) {
        if (response != null)
            mvc.perform(TestUtils
                    .createGet(putIdInUrl(BASE_URL, projectId) + url))
                    .andDo(print())
                    .andExpect(status().is(httpStatus))
                    .andExpect(content().json(response, true));

        mvc.perform(TestUtils
                .createGet(putIdInUrl(BASE_URL, projectId) + url))
                .andDo(print())
                .andExpect(status().is(httpStatus));
    }

    private String putIdInUrl(final String url, final long projectId) {
        return url.replace("{projectId}", String.valueOf(projectId));
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/MilestoneControllerTest/" + resource);

        return String.format(template, args);
    }
}
