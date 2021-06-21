package io.amtech.projectflow.rest.project;

import io.amtech.projectflow.domain.project.Project;
import io.amtech.projectflow.repository.DirectionRepository;
import io.amtech.projectflow.repository.EmployeeRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/projects";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private DirectionRepository directionRepository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(
                        buildJson("createSuccessTest/full_request.json"),
                        new Project()
                                .setId(1L)
                                .setName("Mail")
                                .setDescription("Better project")
                                .setCreateDate(Instant.now()),
                        1L, 1L
                ),
                Arguments.arguments(
                        buildJson("createSuccessTest/without_description_request.json"),
                        new Project()
                                .setId(1L)
                                .setName("Mail")
                                .setCreateDate(Instant.now()),
                        1L, 1L
                )
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql("classpath:db/ProjectControllerTest/createSuccessTest/exists_project.sql")
    void createSuccessTest(final String request, final Project project,
                           final long projectLeadId, final long directionId) {
        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(projectLeadId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> directionRepository.findById(directionId))).isPresent();
        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(1L)))
                .isPresent()
                .get()
                .isEqualTo(project);

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findAll().size()))
                .isEqualTo(3);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(
                Arguments.arguments(buildJson("createFailTest/name_is_missing_request.json"),
                        buildJson("createFailTest/name_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(buildJson("createFailTest/without_description_request.json"),
                        buildJson("createFailTest/without_description_response.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(buildJson("createFailTest/direction_is_missing_request.json"),
                        buildJson("createFailTest/direction_is_missing_response.json"),
                        HttpStatus.NOT_FOUND.value()),
                Arguments.arguments(buildJson("createFailTest/projectLead_is_missing_request.json"),
                        buildJson("createFailTest/projectLead_is_missing_response.json"),
                        HttpStatus.NOT_FOUND.value()));
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

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.findAll()))
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
                                .setDescription("bad project"),
                        1L,
                        1L
                )
        );
    }

    @ParameterizedTest
    @MethodSource("updateSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/ProjectControllerTest/updateSuccessTest/update_project.sql"})
    void updateSuccessTest(final long id, final String request, final Project expect, final long projectLeadId, final long directionId) {
        List<Project> existEmpBefore = projectRepository.findAll();

        mvc.perform(TestUtils
                .createPut(String.format(BASE_ID_URL, id))
                .content(request))
                .andDo(print())
                .andExpect(status().isOk());


        for (Project before : existEmpBefore) {
            if (before.getId() == id) {
                Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(projectLeadId))).isPresent();
                Assertions.assertThat(txUtil.txRun(() -> directionRepository.findById(directionId))).isPresent();
                Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(id)))
                        .isPresent()
                        .get()
                        .isEqualTo(expect);
            } else {
                Assertions.assertThat(txUtil.txRun(() -> employeeRepository.findById(projectLeadId))).isPresent();
                Assertions.assertThat(txUtil.txRun(() -> directionRepository.findById(directionId))).isPresent();
                Assertions.assertThat(txUtil.txRun(() -> projectRepository.findById(before.getId())))
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
                Arguments.arguments(1L,
                        buildJson("updateFailTest/only_name_update_request.json"),
                        buildJson("updateFailTest/only_name_update_response.json"),
                        HttpStatus.BAD_REQUEST.value()),
                Arguments.arguments(1L,
                        buildJson("updateFailTest/only_leadId_update_request.json"),
                        buildJson("updateFailTest/only_leadId_update_response.json"),
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
        return Stream.of(Arguments.arguments(1L));
    }


    @ParameterizedTest
    @MethodSource("deleteSuccessTestArgs")
    @SneakyThrows
    @Sql( "classpath:db/ProjectControllerTest/deleteSuccessTest/project.sql")
    void deleteSuccessTest(long id) {
        List<Project> projectBeforeDelete = projectRepository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andDo(print())
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.existsById(id)))
                .isFalse();

        Assertions.assertThat(projectBeforeDelete.removeIf(x -> x.getId() == id)).isTrue();

        List<Project> projectAfterDelete = txUtil.txRun(() -> projectRepository.findAll());
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
    @Sql("classpath:db/ProjectControllerTest/deleteSuccessTest/project.sql")
    void deleteFailTest(long id, int httpStatus) {
        List<Project> employeesBeforeDelete = projectRepository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andExpect(status().is(httpStatus));

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.existsById(id)))
                .isFalse();

        List<Project> projectAfterDelete = txUtil.txRun(() -> projectRepository.findAll());
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
            "classpath:db/ProjectControllerTest/getTest/get_project.sql"})
    void getSuccessTest(final long id, final String response, int httpStatus) {
        mvc.perform(TestUtils
                .createGet(String.format(BASE_ID_URL, id)))
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
                .createGet(String.format(BASE_ID_URL, id)))
                .andDo(print())
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, false));

        Assertions.assertThat(txUtil.txRun(() -> projectRepository.existsById(id)))
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

