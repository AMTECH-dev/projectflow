package io.amtech.projectflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;

import java.time.Instant;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
public class ProjectFlowApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectFlowApplication.class, args);
    }
}
