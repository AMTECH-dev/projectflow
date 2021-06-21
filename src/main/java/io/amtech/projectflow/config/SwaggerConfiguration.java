package io.amtech.projectflow.config;

import io.amtech.projectflow.app.SwaggerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SwaggerConfiguration {
    @Bean
    public Docket api(final SwaggerProperties swaggerProperties) {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfo(swaggerProperties.getTitle(),
                        swaggerProperties.getDescription(),
                        swaggerProperties.getVersion(),
                        "urn:tos",
                        new Contact(swaggerProperties.getContact().getName(),
                                swaggerProperties.getContact().getUrl(),
                                swaggerProperties.getContact().getEmail()),
                        "Apache 2.0",
                        "https://www.apache.org/licenses/LICENSE-2.0",
                        new ArrayList<>()))
                .select()
                .apis(RequestHandlerSelectors.basePackage("io.amtech.projectflow.rest"))
                .paths(PathSelectors.any())
                .build()
                .enable(swaggerProperties.isEnabled());
    }
}
