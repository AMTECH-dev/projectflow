package io.amtech.projectflow.rest.project_comment;

import io.amtech.projectflow.domain.project.ProjectComment;
import io.amtech.projectflow.repository.ProjectCommentRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectCommentControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/projects/{id}/comments";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    @Autowired
    private ProjectCommentRepository commentRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectCommentSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(1L, 1L,
                        buildJson("createSuccessTest/full_request.json"),
                        new ProjectComment()
                                .setId(1L)
                                .setMessage("Comment 01")
                                .setCreateDate(Instant.ofEpochSecond(1624005449))
                                .setLogin("KiraLis39")
                ),
                Arguments.arguments(3L, 2L,
                        buildJson("createSuccessTest/full_request.json"),
                        new ProjectComment()
                                .setId(2L)
                                .setMessage("Comment 02")
                                .setCreateDate(Instant.ofEpochSecond(1624005449))
                                .setLogin("KiraLis47")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectCommentSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void createProjectCommentSuccessTest(long projectId, long commentId, final String request, final ProjectComment pc) {
        String url = BASE_URL.replace("{id}", String.valueOf(projectId));

        mvc.perform(TestUtils
                .createPost(url)
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findAll()))
                .hasSize(6);

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findById(commentId)))
                .isPresent()
                .get()
                .isEqualTo(pc);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createProjectCommentFiledTestArgs() {
        return Stream.of(
                Arguments.arguments(1L, buildJson("createFailTest/create_fail_request.json"), HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(100L, buildJson("createSuccessTest/full_request.json"), HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(-100L, buildJson("createSuccessTest/full_request.json"), HttpStatus.NOT_FOUND.value())
        );
    }

    @ParameterizedTest
    @MethodSource("createProjectCommentFiledTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void createProjectCommentFailedTest(long projectId, final String request, int expectedHttpStatus) {
        String url = BASE_URL.replace("{id}", String.valueOf(projectId));

        mvc.perform(TestUtils
                .createPost(url)
                .content(request))
                .andExpect(status().is(expectedHttpStatus));

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findAll().size()))
                .isEqualTo(5);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getProjectCommentSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1, 1,
                        buildJson("getSuccessTest/getSuccessTest.json"),
                        new ProjectComment()
                                .setId(1L)
                                .setMessage("Comment 01")
                                .setCreateDate(Instant.parse("2021-06-18T08:37:29Z"))
                                .setLogin("KiraLis39")),
                Arguments.arguments(1, 3,
                        buildJson("getSuccessTest/getSuccessTest2.json"),
                        new ProjectComment()
                                .setId(3L)
                                .setMessage("Comment 03")
                                .setCreateDate(Instant.parse("2021-06-18T08:37:29Z"))
                                .setLogin("KiraLis39")),
                Arguments.arguments(2, 2,
                        buildJson("getSuccessTest/getSuccessTest3.json"),
                        new ProjectComment()
                                .setId(2L)
                                .setMessage("Comment 02")
                                .setCreateDate(Instant.parse("2021-06-18T08:37:29Z"))
                                .setLogin("KiraLis47"))
        );
    }

    @ParameterizedTest
    @MethodSource("getProjectCommentSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void getProjectCommentSuccessTest(long projectId, long commentId, final String response, final ProjectComment comment) {
        String url = String.format(BASE_ID_URL.replace("{id}", String.valueOf(projectId)), commentId);

        mvc.perform(TestUtils
                .createGet(url))
                .andDo(print())
                .andExpect(content().json(response, true))
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findById(commentId)))
                .get()
                .isEqualTo(comment);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> getProjectCommentFailedArgs() {
        return Stream.of(
                Arguments.arguments(1, 100),
                Arguments.arguments(-1, 99999),
                Arguments.arguments(99999, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("getProjectCommentFailedArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void getProjectCommentFailedTest(long projectId, long commentId) {
        String url = String.format(BASE_ID_URL.replace("{id}", String.valueOf(projectId)), commentId);

        mvc.perform(TestUtils
                .createGet(url))
                .andExpect(status().isNotFound())
                .andDo(print());

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findById(commentId)))
                .isNotPresent();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        "",
                        buildJson("searchSuccessTest/get_all.json")),

                Arguments.arguments(1L,
                        "?limit=3&offset=2&orders=-login",
                        buildJson("searchSuccessTest/reverse_order_with_limit_and_offset.json")),

                Arguments.arguments(1L,
                        "?login=О",
                        buildJson("searchSuccessTest/filter_by_name.json")),

                Arguments.arguments(1L,
                        "?message=9&limit=50",
                        buildJson("searchSuccessTest/message_contain_9.json")),

                Arguments.arguments(1L,
                        "?createDate=1623145753839&limit=1",
                        buildJson("searchSuccessTest/createDate_known.json"))
        );
    }

    @ParameterizedTest
    @MethodSource("searchSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void searchSuccessTest(long projectId, final String url, final String response) {
        String url2 = BASE_URL.replace("{id}", String.valueOf(projectId)) + "/" + url;

        mvc.perform(TestUtils
                .createGet(url2))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> searchFailArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        "-1",
                        buildJson("searchFailTest/minus_index.json"),
                        HttpStatus.NOT_FOUND.value()),

                Arguments.arguments(1L,
                        "?orders=-some_field_name",
                        buildJson("searchFailTest/not_exists_field_order.json"),
                        HttpStatus.BAD_REQUEST.value())
        );
    }

    @ParameterizedTest
    @MethodSource("searchFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void searchFailTest(long projectId, final String url, final String response, int status) {
        String url2 = BASE_URL.replace("{id}", String.valueOf(projectId)) + "/" + url;

        mvc.perform(TestUtils
                .createGet(url2))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(content().json(response, true));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateProjectCommentSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1, 1,
                        buildJson("updateSuccessTest/updateSuccessTest.json"),
                        new ProjectComment()
                                .setId(1L)
                                .setLogin("KiraLis39")
                                .setCreateDate(Instant.parse("2021-06-18T08:37:29Z"))
                                .setMessage("new some message updated")
                ),
                Arguments.arguments(1, 3,
                        buildJson("updateSuccessTest/updateSuccessTest.json"),
                        new ProjectComment()
                                .setId(3L)
                                .setLogin("KiraLis39")
                                .setCreateDate(Instant.parse("2021-06-18T08:37:29Z"))
                                .setMessage("new some message updated")
                )
        );
    }

    @ParameterizedTest
    @MethodSource("updateProjectCommentSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void updateProjectCommentSuccessTest(long projectId, long commentId, String request, ProjectComment expected) {
        String url = String.format(BASE_ID_URL.replace("{id}", String.valueOf(projectId)), commentId);

        List<ProjectComment> comments = commentRepository.findAll();

        mvc.perform(TestUtils
                .createPut(url)
                .content(request))
                .andDo(print())
                .andExpect(status().is(200));

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findAll().size()))
                .isEqualTo(5);

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findById(commentId)))
                .get()
                .isEqualTo(expected);

        Assertions.assertThat(comments.stream().filter(comment -> comment.getId() == commentId).findFirst())
                .isPresent()
                .get()
                .isNotEqualTo(expected);

        for (ProjectComment comment : comments) {
            if (comment.getId() == commentId) {
                continue;
            }

            Assertions.assertThat(txUtil.txRun(() -> comment))
                    .isNotEqualTo(expected);
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> updateProjectCommentFailedArgs() {
        return Stream.of(
                Arguments.arguments(1, 1,
                        buildJson("updateFailTest/updateFailTest.json"),
                        new ProjectComment()
                                .setId(1L)
                                .setLogin("KiraLis39")
                                .setCreateDate(Instant.ofEpochMilli(1623063603))
                                .setMessage(""),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments(-1000, 1,
                        buildJson("updateFailTest/updateFailTest2.json"),
                        new ProjectComment()
                                .setId(1L)
                                .setLogin("KiraLis39")
                                .setCreateDate(Instant.ofEpochMilli(1623063603))
                                .setMessage(""),
                        HttpStatus.NOT_FOUND.value()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("updateProjectCommentFailedArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void updateProjectCommentFailedTest(long projectId, long commentId, String request, ProjectComment expected, int expectedCode) {
        String url = String.format(BASE_ID_URL.replace("{id}", String.valueOf(projectId)), commentId);

        mvc.perform(TestUtils
                .createPut(url)
                .content(request))
                .andDo(print())
                .andExpect(status().is(expectedCode));

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findById(commentId)))
                .get()
                .isNotEqualTo(expected);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteProjectCommentSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1, 1),
                Arguments.arguments(1, 2),
                Arguments.arguments(3, 2)
        );
    }

    @ParameterizedTest
    @MethodSource("deleteProjectCommentSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void deleteProjectCommentSuccessTest(long projectId, long commentId) {
        String url = String.format(BASE_ID_URL.replace("{id}", String.valueOf(projectId)), commentId);

        mvc.perform(TestUtils
                .createDelete(url))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findAll()))
                .hasSize(4);

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findById(commentId)))
                .isNotPresent();
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> deleteProjectCommentFailedArgs() {
        return Stream.of(
                Arguments.arguments(1000, 1000),
                Arguments.arguments(-1, -1)
        );
    }

    @ParameterizedTest
    @MethodSource("deleteProjectCommentFailedArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectCommentControllerTest/exists_projects.sql"
    })
    void deleteProjectCommentFailedTest(long projectId, long commentId) {
        String url = String.format(BASE_ID_URL.replace("{id}", String.valueOf(projectId)), commentId);

        mvc.perform(TestUtils
                .createDelete(url))
                .andDo(print())
                .andExpect(status().isNotFound());

        Assertions.assertThat(txUtil.txRun(() -> commentRepository.findAll()))
                .hasSize(5);
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/ProjectCommentControllerTest/" + resource);

        return String.format(template, args);
    }
}
