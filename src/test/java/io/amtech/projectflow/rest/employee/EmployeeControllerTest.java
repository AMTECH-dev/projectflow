package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.domain.employee.Employee;
import io.amtech.projectflow.repository.EmployeeRepository;
import io.amtech.projectflow.test.IntegrationTest;
import io.amtech.projectflow.test.TestUtils;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.stream.Stream;

import static io.amtech.projectflow.domain.employee.UserPosition.DIRECTOR;
import static io.amtech.projectflow.domain.employee.UserPosition.PROJECT_LEAD;
import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/employees";
    @Autowired
    EmployeeRepository repository;

    static Stream<Arguments> createSuccessTestArgs() {
        final String fakeName = strMultiple("a", 255);
        final String fakeEmail = strMultiple("b", 38) + "@example.com";
        final String fakePhone = strMultiple("1", 50);
        return Stream.of(
                Arguments.arguments(buildJson("createSuccessTest/full_request.json"),
                        buildJson("createSuccessTest/full_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("Иван Копыто")
                                .setEmail("kopito@example.com")
                                .setPhone("+7 128 123 12 12")
                                .setPosition(PROJECT_LEAD)),
                Arguments.arguments(buildJson("createSuccessTest/without_phone_request.json"),
                        buildJson("createSuccessTest/without_phone_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("А")
                                .setEmail("a@b.ru")
                                .setPosition(DIRECTOR)),
                Arguments.arguments(buildJson("default_request.json.template", fakeName,
                        fakeEmail,
                        fakePhone,
                        PROJECT_LEAD.name()),
                        buildJson("createSuccessTest/default_response.json.template", 1, fakeName,
                                fakeEmail,
                                fakePhone,
                                PROJECT_LEAD.name()),
                        new Employee()
                                .setId(1L)
                                .setName(fakeName)
                                .setEmail(fakeEmail)
                                .setPhone(fakePhone)
                                .setPosition(PROJECT_LEAD))
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/createSuccessTest/exists_employees.sql"
    })
    void createSuccessTest(final String request, final String response, final Employee e) {
        // setup

        // when
        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        // then
        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(3);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(e);
    }

    static Stream<Arguments> createFailTestArgs() {
        final String fakeName = strMultiple("a", 256);
        return Stream.of(
                Arguments.arguments(buildJson("createFailTest/name_is_missing_request.json"),
                        buildJson("createFailTest/name_is_missing_response.json"),
                        400),
                Arguments.arguments(buildJson("default_request.json.template",
                        fakeName, "sd@mail.com", "293 3993 93939", PROJECT_LEAD),
                        buildJson("createFailTest/name_is_too_long_response.json"),
                        400)
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

    static Stream<Arguments> searchSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("",
                        buildJson("searchSuccessTest/all.json")),
                Arguments.arguments("?limit=3&offset=2&orders=-name",
                        buildJson("searchSuccessTest/reverse_order_with_limit_and_offset.json")),
                Arguments.arguments("?name=О",
                        buildJson("searchSuccessTest/filter_by_name.json")),
                Arguments.arguments("?email=safe",
                        buildJson("searchSuccessTest/email_contain_safe.json")),
                Arguments.arguments("?phone=9&limit=50",
                        buildJson("searchSuccessTest/phone_contain_9.json")),
                Arguments.arguments("?position=director&limit=1",
                        buildJson("searchSuccessTest/position_director.json"))
        );
    }

    @ParameterizedTest
    @MethodSource("searchSuccessTestArgs")
    @SneakyThrows
    @Sql(scripts = {
            "classpath:db/EmployeeControllerTest/searchSuccessTest/data.sql"
    })
    void searchSuccessTest(final String url, final String response) {
        // setup
        mvc.perform(TestUtils.createGet(BASE_URL + url))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));
    }

    private static String buildJson(final String resource, Object...args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/" + resource);

        return String.format(template, args);
    }
}
