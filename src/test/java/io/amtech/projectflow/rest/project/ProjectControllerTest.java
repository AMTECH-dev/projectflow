package io.amtech.projectflow.rest.project;

import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.repository.ProjectRepository;
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

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Stream;


import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/projects";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    @Autowired
    private ProjectRepository repository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
 Instant instantNow= Instant.now();
 Instant instantNew= Instant.now().plusSeconds(30);
        Duration duration= Duration.between(instantNow, instantNew);

        return Stream.of(
                Arguments.arguments(
                        buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),

                        new Project()
                                .setId(1L)
                                .setName("Mail")
                                .setDescription("Better project")
                                .setCreateDate(Instant.now())));


    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/createSuccessTest/exists_project.sql"})
    void createSuccessTest(final String request, final String response, final Project project, Instant instantNow, Instant instantNew) {

        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll().size()))
               .isEqualTo(3);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent().get().isEqualTo(project);

        Assertions.assertThat(instantNow).isCloseTo(instantNew, within(30, ChronoUnit.SECONDS));

    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(
                Arguments.arguments(buildJson("createFailTest/name_is_missing_request.json"),
                        buildJson("createFailTest/name_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value()),

                Arguments.arguments(buildJson("createFailTest/direction_is_missing_request.json"),
                        buildJson("createFailTest/direction_is_missing_response.json"),
                        HttpStatus.INTERNAL_SERVER_ERROR.value()),

                 Arguments.arguments(buildJson("createFailTest/without_description_request.json"),
                        buildJson("createFailTest/without_description_response.json"),
                        HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    void createFailTest(final String request, final String response, int httpStatus) {

        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));


        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .isEmpty();
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static Stream<Arguments> updateSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        buildJson("updateSuccessTest/update_full_request.json"),
                        new Project()
                                .setId(1L)
                                .setName("Do")
                                .setDescription("bad project")
                                .setCreateDate(Instant.ofEpochSecond(1623239343))));
    }

    @ParameterizedTest
    @MethodSource("updateSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/ProjectControllerTest/updateSuccessTest/update_project.sql"})
    void updateSuccessTest(final long id, final String request, final Project expect) {
        List<Project> existEmpBefore = repository.findAll();

        mvc.perform(TestUtils
                .createPut(String.format(BASE_ID_URL, id))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());


        for (Project before : existEmpBefore) {
            if (before.getId() == id) {
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(id)))
                        .isPresent()
                        .get()
                        .isEqualTo(expect);
            } else {
                Assertions.assertThat(txUtil.txRun(() -> repository.findById(before.getId())))
                        .isPresent()
                        .get()
                        .isEqualTo(before);
            }
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static Stream<Arguments> updateFailedArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        buildJson("updateFailTest/name_is_null_update_request.json"),
                        buildJson("updateFailTest/name_is_null_update_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(2L,
                        buildJson("updateFailTest/only_name_update_request.json"),
                        buildJson("updateFailTest/only_name_update_response.json"),
                        HttpStatus.BAD_REQUEST.value()));
    }

    @ParameterizedTest
    @MethodSource("updateFailedArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/ProjectControllerTest/updateSuccessTest/update_project.sql"})
    void updateFailTest(final Long id, final String request, final String response, int expectedStatus) {
        mvc.perform(TestUtils
                .createPut(BASE_URL + "/" + id)
                .content(request))
                .andDo(print())
                .andExpect(status().is(expectedStatus))
                .andExpect(content().json(response, true));
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    static Stream<Arguments> deleteSuccessTestArgs() {
        return Stream.of(Arguments.arguments(1));
    }



    @ParameterizedTest
    @MethodSource("deleteSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/deleteSuccessTest/project.sql"})

    void deleteSuccessTest(long id) {
        List<Project> projectBeforeDelete = repository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id)))
                .isTrue();

        Assertions.assertThat(projectBeforeDelete.removeIf(x -> x.getId() == id)).isTrue();

        List<Project> projectAfterDelete = txUtil.txRun(() -> repository.findAll());
        for (Project project : projectBeforeDelete) {
            Assertions.assertThat(projectAfterDelete.stream().filter(project::equals).findFirst())
                    .isNotEmpty();
        }
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static Stream<Arguments> deleteFailTestArgs() {
        return Stream.of(Arguments.arguments(0, HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(99, HttpStatus.NOT_FOUND.value()));
    }

    @ParameterizedTest
    @MethodSource("deleteFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/deleteSuccessTest/project.sql"
    })
    void deleteFailTest(long id, int httpStatus) {
        List<Project> employeesBeforeDelete = repository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andExpect(status().is(httpStatus));

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id)))
                .isFalse();

        List<Project> projectAfterDelete = txUtil.txRun(() -> repository.findAll());
        for (Project project : projectAfterDelete) {
            Assertions.assertThat(employeesBeforeDelete.stream().filter(project::equals).findFirst())
                    .isNotEmpty();
        }

    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    static Stream<Arguments> getSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        buildJson("getSuccessTest/get_by_id_project.json"),
                        HttpStatus.OK.value())
                );
    }

    @ParameterizedTest
    @MethodSource("getSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/getTest/get_project.sql" })
    void getSuccessTest(final long id, final String response, int httpStatus) {
        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL,id)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
            .andExpect(content().json(response, false));
    }
///////////////////////////////////////////////////////////////////////////////////////////
    static Stream<Arguments> getFailTestArgs() {
        return Stream.of(
                Arguments.arguments(99, buildJson("getFailTest/wrong_response.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(0, buildJson("getFailTest/wrong_response.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(-1, buildJson("getFailTest/wrong_response.json"),
                        HttpStatus.NOT_FOUND.value()));
    }

    @ParameterizedTest
    @MethodSource("getFailTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/getTest/get_project.sql"
    })
    void getFailTest(final long id, final String response, int httpStatus) {
        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL,id)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, false));

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id)))
                .isFalse();
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings("unused")
    static Stream<Arguments> searchSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("",
                        buildJson("searchSuccessTest/all.json")),
                Arguments.arguments("?limit=3&offset=2&orders=-name",
                       buildJson("searchSuccessTest/reverse_order_with_limit_and_offset.json"))

        );
    }

    @ParameterizedTest
    @MethodSource("searchSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/searchSuccessTest/search_project.sql"
    })
    void searchSuccessTest(final String url, final String response) {
        // setup
        mvc.perform(TestUtils.createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused")
    static Stream<Arguments> searchFailArgs() {
        return Stream.of(
                Arguments.arguments("?orders=-some_field_name",
                        buildJson("searchFailTest/invalid_order_response.json"),
                        HttpStatus.BAD_REQUEST.value())
              );
    }


    @ParameterizedTest
    @MethodSource("searchFailArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/searchFailTest/data.sql"
    })
    void searchFailTest(final String url, final String response, int status) {
        mvc.perform(TestUtils.createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().is(status))
                .andExpect(content().json(response, true));
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/ProjectControllerTest/" + resource);

        return String.format(template, args);
    }
}

