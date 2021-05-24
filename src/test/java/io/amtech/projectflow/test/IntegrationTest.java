package io.amtech.projectflow.test;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@ContextConfiguration(initializers = {IntegrationTest.Initializer.class})
public abstract class IntegrationTest {

    @Autowired
    protected MockMvc mvc;

    protected TransactionalUtils txUtil = new TransactionalUtils();

    private static final GenericContainer<?> dbContainer = new GenericContainer<>("postgres:13.3")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_PASSWORD", "test")
            .withEnv("POSTGRES_USER", "test")
            .withEnv("POSTGRES_DB", "projectflow");


    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            dbContainer.start();
            final String jdbcUrl = "jdbc:postgresql://localhost:"+dbContainer.getMappedPort(5432)+"/projectflow?currentSchema=pf";
            TestPropertyValues.of("db.jdbcUrl=" + jdbcUrl)
                    .applyTo(configurableApplicationContext.getEnvironment());

            Flyway flyway = Flyway
                    .configure()
                    .locations("classpath:/db/migration")
                    .dataSource(jdbcUrl, "test", "test")
                    .load();
            flyway.migrate();
        }
    }

}
