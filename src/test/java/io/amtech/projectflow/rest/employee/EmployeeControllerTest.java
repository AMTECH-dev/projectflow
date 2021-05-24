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
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.stream.Stream;

import static io.amtech.projectflow.domain.employee.UserPosition.DIRECTOR;
import static io.amtech.projectflow.domain.employee.UserPosition.PROJECT_LEAD;
import static io.amtech.projectflow.test.TestUtils.strMultiple;
import static org.springframework.test.context.jdbc.SqlMergeMode.MergeMode.MERGE;
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
                Arguments.arguments(buildJson("full_request.json"),
                        buildJson("full_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("Иван Копыто")
                                .setEmail("kopito@example.com")
                                .setPhone("+7 128 123 12 12")
                                .setPosition(PROJECT_LEAD)),
                Arguments.arguments(buildJson("without_phone_request.json"),
                        buildJson("without_phone_response.json"),
                        new Employee()
                                .setId(1L)
                                .setName("А")
                                .setEmail("a@b.ru")
                                .setPosition(DIRECTOR)),
                Arguments.arguments(buildJson("default_request.json.template", fakeName,
                        fakeEmail,
                        fakePhone,
                        PROJECT_LEAD.name()),
                        buildJson("default_response.json.template", 1, fakeName,
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

        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(content().json(response, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(3);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(e);
    }

    private static String buildJson(final String resource, Object...args) {
        String template = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/createSuccessTest/" + resource);

        return String.format(template, args);
    }
}
