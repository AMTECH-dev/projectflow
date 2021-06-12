package io.amtech.projectflow.rest.project_journal;

import io.amtech.projectflow.repository.ProjectJournalRepository;
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

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectJournalControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/projects/{projectId}/projectJournals";
    private static final String BASE_ID_URL = BASE_URL + "/%d";
    @Autowired
    private ProjectJournalRepository projectJournalRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> searchSuccessArgs() {
        return Stream.of(
                Arguments.arguments(2, "?", buildJson("searchSuccessTest/all.json"), HttpStatus.OK.value()),
                Arguments.arguments(3, "?login=I&orders=-id",
                        buildJson("searchSuccessTest/contains_i_with_order_id_desc.json"), HttpStatus.OK.value()),
                Arguments.arguments(2, "?login=anna", buildJson("searchSuccessTest/contains_anna.json"),
                        HttpStatus.OK.value()),
                Arguments.arguments(2, "?login=a&updateDateFrom=1173440943&updateDateTo=1607514543&orders=login",
                        buildJson("searchSuccessTest/contains_a_between_date_with_order_login.json"),
                        HttpStatus.OK.value()),
                Arguments.arguments(1, "?",
                        buildJson("searchSuccessTest/empty_journal_in_project.json"),
                        HttpStatus.OK.value()),
                Arguments.arguments(2, "?offset=1&limit=1&login=o",
                        buildJson("searchSuccessTest/login_contains_o_offset_1_and_limit_1.json"),
                        HttpStatus.OK.value())
        );
    }

    @ParameterizedTest
    @MethodSource("searchSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalControllerTest/searchSuccessTest/data.sql"
    })
    void searchSuccessTest(long projectId, final String url, final String response, int status) {
        String finalUrlAddress = replaceProjectIdInURLOn(BASE_URL, projectId) + url;
        mvc.perform(TestUtils
                .createGet(finalUrlAddress))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchFailArgs() {
        return Stream.of(
                Arguments.arguments(-1, "", buildJson("searchFailTest/project_not_found.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(1, "?orders=fieldname", buildJson("searchFailTest/invalid_order_response.json"),
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest
    @MethodSource("searchFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalControllerTest/searchFailTest/data.sql"
    })
    void searchFailTest(long projectId, final String url, final String response, int status) {
        String finalUrlAddress = replaceProjectIdInURLOn(BASE_URL, projectId) + url;
        mvc.perform(TestUtils
                .createGet(finalUrlAddress))
                .andDo(print())
                .andExpect(content().json(response, true))
                .andExpect(status().is(status));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getSuccessArgs() {
        return Stream.of(
                Arguments.arguments(2, 3, buildJson("getSuccessTest/second_project_3th_journal_response.json"),
                        HttpStatus.OK.value()),
                Arguments.arguments(1, 5, buildJson("getSuccessTest/first_project_5th_journal_response.json"),
                        HttpStatus.OK.value())
        );
    }

    @ParameterizedTest
    @MethodSource("getSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalControllerTest/getSuccessTest/data.sql"
    })
    void getSuccessTest(long projectId, long projectJournalId, final String response, int status) {
        Assertions.assertThat(projectJournalRepository.findById(projectJournalId))
                .isPresent();

        String finishUrlAddress = createUrlWithProjectIdAndJournalId(projectId, projectJournalId);
        mvc.perform(TestUtils
                .createGet(finishUrlAddress))
                .andDo(print())
                .andExpect(content().json(response, true))
                .andExpect(status().is(status));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getFailArgs() {
        return Stream.of(
                Arguments.arguments(-100, 28147, buildJson("getFailTest/project_not_found.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(1, 28147, buildJson("getFailTest/project_journal_not_found_response.json"),
                        HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest
    @MethodSource("getFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectJournalControllerTest/getFailTest/data.sql"
    })
    void getFailTest(long projectId, long projectJournalId, final String response, int status) {
        Assertions.assertThat(projectJournalRepository.findById(projectJournalId))
                .isNotPresent();

        String finishUrlAddress = createUrlWithProjectIdAndJournalId(projectId, projectJournalId);
        mvc.perform(TestUtils
                .createGet(finishUrlAddress))
                .andDo(print())
                .andExpect(content().json(response, true))
                .andExpect(status().is(status));
    }

    private String createUrlWithProjectIdAndJournalId(long projectId, long journalId) {
        String url = replaceProjectIdInURLOn(BASE_ID_URL, projectId);
        return String.format(url, journalId);
    }

    private String replaceProjectIdInURLOn(final String url, long projectId) {
        return url.replace("{projectId}", String.valueOf(projectId));
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/ProjectJournalControllerTest/" + resource);

        return String.format(template, args);
    }
}
