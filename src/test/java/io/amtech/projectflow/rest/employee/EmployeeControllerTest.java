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

import java.util.stream.Stream;

import static io.amtech.projectflow.domain.employee.UserPosition.DIRECTOR;
import static io.amtech.projectflow.domain.employee.UserPosition.PROJECT_LEAD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/employees";
    @Autowired
    EmployeeRepository repository;

    static Stream<Arguments> createSuccessTestArgs() {
        return Stream.of(
                Arguments.arguments("full_request.json",
                        "full_response.json",
                        new Employee()
                                .setId(1L)
                                .setName("Иван Копыто")
                                .setEmail("kopito@example.com")
                                .setPhone("+7 128 123 12 12")
                                .setPosition(PROJECT_LEAD)),
                Arguments.arguments("without_phone_request.json",
                        "without_phone_response.json",
                        new Employee()
                                .setId(1L)
                                .setName("А")
                                .setEmail("a@b.ru")
                                .setPosition(DIRECTOR))
        );
    }

    @ParameterizedTest
    @MethodSource("createSuccessTestArgs")
    @SneakyThrows
    void createSuccessTest(final String request, final String response, final Employee e) {

        final String requestJson = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/createSuccessTest/" + request);

        final String responseJson = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/createSuccessTest/" + response);

        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json(responseJson, true));

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(1);

        Assertions.assertThat(txUtil.txRun(() -> repository.findById(1L)))
                .isPresent()
                .contains(e);
    }
}
