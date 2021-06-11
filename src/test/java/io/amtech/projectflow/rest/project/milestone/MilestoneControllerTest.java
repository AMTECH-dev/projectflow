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
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MilestoneControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/projects/{projectId}/milestones";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    @Autowired
    private MilestoneRepository repository;

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(11L, buildJson("createSuccessTest/full_request.json"),
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
                )
        );
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/MilestoneControllerTest/" + resource);

        return String.format(template, args);
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/clean.sql",
            "classpath:db/MilestoneControllerTest/createSuccessTest/milestones_are_exist.sql"
    })
    void createSuccessTest(final long projectId, final String request, final String response, final Milestone m) {
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

    private String changeProjectIdInUrl(final long projectId) {
        return MilestoneControllerTest.BASE_URL.replace("{projectId}", String.valueOf(projectId));
    }
}
