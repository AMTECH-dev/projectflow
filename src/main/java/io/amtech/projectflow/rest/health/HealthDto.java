package io.amtech.projectflow.rest.health;

import lombok.Value;

import java.time.Instant;

@Value
public class HealthDto {
    String message;
    Instant timestamp;

    public HealthDto() {
        this.message = "OK";
        this.timestamp = Instant.now();
    }
}
