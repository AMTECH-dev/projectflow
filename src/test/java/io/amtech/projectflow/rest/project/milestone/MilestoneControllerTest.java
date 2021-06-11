package io.amtech.projectflow.rest.project.milestone;

import io.amtech.projectflow.domain.project.Milestone;
import io.amtech.projectflow.repository.MilestoneRepository;
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
import java.util.stream.Stream;

import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MilestoneControllerTest extends IntegrationTest {
    private static final String BASE_URL = "/projects/{projectId}/milestones";
//    private static final String BASE_ID_URL = BASE_URL + "/%d";


    @Autowired
    private MilestoneRepository repository;

    private static final String MAX_NAME_VALUE = strMultiple("n", 255);
    private static final String MAX_DESCRIPTION_VALUE = strMultiple("d", 2048);

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("full_request", 11L,
                        buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Milestone()
                                .setId(1L)
                                .setName("Разработка проектной документации")
                                .setDescription("Разработана документация проекта")
                                .setPlannedStartDate(Instant.ofEpochMilli(1623330100111L))
                                .setPlannedFinishDate(Instant.ofEpochMilli(1623330101111L))
                                .setFactStartDate(Instant.ofEpochMilli(1623330100211L))
                                .setFactFinishDate(Instant.ofEpochMilli(1623330101211L))
                                .setProgressPercent((short) 23)
                ),
                Arguments.arguments("empty_nullable_params", 11L,
                        buildJson("createSuccessTest/empty_nullable_params_request.json"),
                        buildJson("createSuccessTest/empty_nullable_params_response.json"),
                        new Milestone()
                                .setId(1L)
                                .setName("Печать комплекта документов")
                                .setPlannedStartDate(Instant.ofEpochMilli(1623320100111L))
                                .setPlannedFinishDate(Instant.ofEpochMilli(1623320101111L))
                                .setProgressPercent((short) 13)
                ),
                Arguments.arguments("max_length", 11L,
                        buildJson("createSuccessTest/max_length_request.json"),
                        buildJson("createSuccessTest/max_length_response.json"),
                        new Milestone()
                                .setId(1L)
                                .setName(MAX_NAME_VALUE)
                                .setDescription(MAX_DESCRIPTION_VALUE)
                                .setPlannedStartDate(Instant.ofEpochMilli(1623320100111L))
                                .setPlannedFinishDate(Instant.ofEpochMilli(1623320101111L))
                                .setProgressPercent((short) 13)
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/MilestoneControllerTest/createSuccessTest/milestones_are_exist.sql"
    })
    void createSuccessTest(@SuppressWarnings("unused") String testName,
                           final long projectId, final String request, final String response, final Milestone m) {
        mvc.perform(TestUtils
                .createPost(changeProjectIdInUrl(projectId))
                .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(6);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(m);
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(
                Arguments.arguments("name_is_too_long", 11L,
                        buildJson("createFailTest/name_is_too_long_request.json"),
                        buildJson("createFailTest/name_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments("description_is_too_long", 11L,
                        buildJson("createFailTest/description_is_too_long_request.json"),
                        buildJson("createFailTest/description_is_too_long_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments("without_start_date", 11L,
                        buildJson("createFailTest/without_start_date_request.json"),
                        buildJson("createFailTest/without_start_date_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments("without_finish_date", 11L,
                        buildJson("createFailTest/without_finish_date_request.json"),
                        buildJson("createFailTest/without_finish_date_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments("without_progress_percent", 11L,
                        buildJson("createFailTest/without_progress_percent_request.json"),
                        buildJson("createFailTest/without_progress_percent_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments("empty_request", 11L,
                        buildJson("createFailTest/empty_request.json"),
                        buildJson("createFailTest/empty_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                ),
                Arguments.arguments("int_instead_short", 11L,
                        buildJson("createFailTest/int_instead_short_request.json"),
                        buildJson("createFailTest/int_instead_short_response.json"),
                        HttpStatus.BAD_REQUEST.value()
                )
        );
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("createFailTestArgs")
    @SneakyThrows
    void createFailTest(@SuppressWarnings("unused") String testName, final long projectId,
                        final String request, final String response, final int httpStatus) {
        mvc.perform(TestUtils
                .createPost(changeProjectIdInUrl(projectId))
                .content(request))
                .andExpect(status().is(httpStatus))
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .isEmpty();
    }

    //==================================

    private String changeProjectIdInUrl(final long projectId) {
        return MilestoneControllerTest.BASE_URL.replace("{projectId}", String.valueOf(projectId));
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/MilestoneControllerTest/" + resource);

        return String.format(template, args);
    }
}
