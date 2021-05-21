package io.amtech.projectflow.rest.health;

import io.amtech.projectflow.test.IntegrationTest;
import io.amtech.projectflow.test.TestUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthControllerTest extends IntegrationTest {

    @Test
    @SneakyThrows
    void someTest() {

        mvc.perform(TestUtils
                .createGet("/health"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"message\":\"OK\"}"));
    }
}
