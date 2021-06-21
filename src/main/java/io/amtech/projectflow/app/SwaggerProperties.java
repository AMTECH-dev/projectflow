package io.amtech.projectflow.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "swagger")
@PropertySource("classpath:application.yml")
public class SwaggerProperties {
    private boolean enabled;
    private String title;
    private String description;
    private String version;
    private ContactInfo contact;

    @Getter
    @Setter
    public static class ContactInfo {
        private String name;
        private String url;
        private String email;
    }
}
