package io.amtech.projectflow.domain.employee;

import io.amtech.projectflow.app.exception.DomainException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum UserPosition {
    PROJECT_LEAD, DIRECTION_LEAD, DIRECTOR;

    public static UserPosition getByName(String name) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new DomainException("Invalid position: " + name, HttpStatus.BAD_REQUEST.value()));
    }
}
