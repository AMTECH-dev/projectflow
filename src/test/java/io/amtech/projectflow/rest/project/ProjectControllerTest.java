package io.amtech.projectflow.rest.project;

import io.amtech.projectflow.domain.employee.Employee;
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

import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProjectControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/projects";
    private final static String MAX_NAME_VALUE = strMultiple("a", 255);

    private static final String BASE_ID_URL = BASE_URL + "/%d";
    @Autowired
    static private EmployeeRepository employeeRepository;
    @Autowired
    static private DirectionRepository directionRepository;
    @Autowired
    private ProjectRepository repository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {


        return Stream.of(
                Arguments.arguments(
                        buildJson("createSuccessTest/update_full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Project()
                                .setId(1L)
                                .setName("Mail")
                                .setProjectLead(employeeRepository.findById(1l).get())
                                .setDirection(directionRepository.findById(1l).get())
                                .setDescription("Better project")),
                Arguments.arguments(buildJson("createSuccessTest/without_description_request.json"),
                        buildJson("createSuccessTest/without_description_request.json"),
                        new Project()
                                .setName("Mail")
                                .setProjectLead(employeeRepository.findById(1l).get())
                                .setDirection(directionRepository.findById(1l).get())));

    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/createSuccessTest/exists_project.sql"
    })
    void createSuccessTest(final String request, final String response, final Project project) {

        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(3);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(project);
    }


    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {


        return Stream.of(
                Arguments.arguments(buildJson("createFailTest/name_is_missing_request.json"),
                        buildJson("createFailTest/name_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value()),

                Arguments.arguments(buildJson("createFailTest/direction_is_missing_request.json"),
                        buildJson("createFailTest/direction_is_missing_response.json"),
                        HttpStatus.BAD_REQUEST.value())

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


    static Stream<Arguments> updateSuccessArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        buildJson("updateSuccessTest/full_update.json"),
                        new Project()
                                .setName("Do")
                                .setProjectLead(employeeRepository.findById(1l).get())
                                .setDirection(directionRepository.findById(1l).get())
                                .setDescription("bad project")
                )

        );
    }

    @ParameterizedTest
    @MethodSource("updateSuccessArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/ProjectControllerTest/updateSuccessTest/update_project.sql"})
    void updateSuccessTest(final long id, final String request, final Employee expect) {
        List<Project> existEmpBefore = repository.findAll();

        mvc.perform(TestUtils
                .createPut(BASE_URL + "/" + id)
                .content(request))
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

    static Stream<Arguments> updateFailedArgs() {
        return Stream.of(
                Arguments.arguments(1L,
                        buildJson("updateFailTest/name_is_null_update_request.json"),
                        buildJson("updateFailTest/name_is_null_update_response.json"),
                        HttpStatus.BAD_REQUEST.value())

        );
    }

    @ParameterizedTest
    @MethodSource("updateFailedArgs")
    @SneakyThrows
    @Sql(scripts = {"classpath:db/ProjectControllerTest/updateSuccessTest/update_project.sql"})
    void updateFailTest(final Long id, final String request, final String response, int expectedStatus) {
        mvc.perform(TestUtils
                .createPut(BASE_URL + "/" + id)
                .content(request))
                .andExpect(status().is(expectedStatus))
                .andExpect(content().json(response, true));
    }








    static Stream<Arguments> deleteSuccessTestArgs() {
        return Stream.of(Arguments.arguments(1));
    }

    @ParameterizedTest
    @MethodSource("deleteSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/ProjectControllerTest/deleteSuccessTest/project.sql"
    })
    void deleteSuccessTest(long id) {
        List<Project> employeesBeforeDelete = repository.findAll();

        mvc.perform(TestUtils
                .createDelete(String.format(BASE_ID_URL, id)))
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> repository.existsById(id)))
                .isFalse();

        Assertions.assertThat(employeesBeforeDelete.removeIf(x -> x.getId() == id)).isTrue();
        List<Project> projectAfterDelete = txUtil.txRun(() -> repository.findAll());
        for (Project project : employeesBeforeDelete) {
            Assertions.assertThat(projectAfterDelete.stream().filter(project::equals).findFirst())
                    .isNotEmpty();
        }
    }
    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/ProjectControllerTest/" + resource);

        return String.format(template, args);
    }





}
