package io.amtech.projectflow.rest.employee;

import io.amtech.projectflow.persistence.employee.EmployeeRepository;
import io.amtech.projectflow.test.IntegrationTest;
import io.amtech.projectflow.test.TestUtils;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmployeeControllerTest extends IntegrationTest {

    private static final String BASE_URL = "/employees";
    @Autowired
    EmployeeRepository repository;

    @Test
    @SneakyThrows
    void createSuccessTest() {

        final String requestJson = TestUtils.readClassPathResourceAsString(
                "json/EmployeeControllerTest/createSuccessTest/success.json");

        mvc.perform(TestUtils
                .createPost(BASE_URL)
                .content(requestJson))
                .andExpect(status().isOk());

        Assertions.assertThat(txUtil.txRun(() -> repository.findAll()))
                .hasSize(1);

    }
}
