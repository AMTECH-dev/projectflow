package io.amtech.projectflow.rest.direction;

import io.amtech.projectflow.domain.Direction;
import io.amtech.projectflow.repository.DirectionRepository;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DirectionControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/directions";
    private static final String BASE_ID_URL = BASE_URL + "/%d";

    @Autowired
    private DirectionRepository repository;

    private static final String MAX_NAME_VALUE = org.apache.commons.lang3.StringUtils.leftPad("", 255, "n");

    @SuppressWarnings("unused")
    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments(11L, buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName("Основной Direction")
                                //.setLead()
                ),
                Arguments.arguments("empty_nullable_params", 11L,
                        buildJson("createSuccessTest/empty_nullable_params_request.json"),
                        buildJson("createSuccessTest/empty_nullable_params_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName("Основной Direction")
                                //.setLead()
                        ),
                Arguments.arguments("max_length", 11L,
                        buildJson("createSuccessTest/max_length_request.json"),
                        buildJson("createSuccessTest/max_length_response.json"),
                        new Direction()
                                .setId(1L)
                                .setName(MAX_NAME_VALUE)
                        )
        );
    }

    private static String buildJson(final String resource, Object... args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/DirectionControllerTest/" + resource);

        return String.format(template, args);
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/clean.sql",
            "classpath:db/DirectionControllerTest/createSuccessTest/directions_are_exist.sql"
    })
    void createSuccessTest(final long projectId, final String request, final String response, final Direction d) {
        mvc.perform(TestUtils
                .createPost(changeProjectIdInUrl(projectId))
                .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(6);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(d);
    }

    private String changeProjectIdInUrl(final long projectId) {
        return DirectionControllerTest.BASE_URL.replace("{Id}", String.valueOf(projectId));
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> createFailTestArgs() {
        return Stream.of(
                Arguments.arguments("name_is_too_long", 11L,
                        buildJson("createFailTest/name_is_too_long_request.json"),
                        buildJson("createFailTest/name_is_too_long_response.json"),
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
}