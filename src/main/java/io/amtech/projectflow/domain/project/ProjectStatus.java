package io.amtech.projectflow.domain.project;

import io.amtech.projectflow.app.exception.DomainException;
import io.amtech.projectflow.domain.employee.UserPosition;
import org.springframework.http.HttpStatus;

import java.util.Arrays;

public enum ProjectStatus {
    UNAPPROVED,
    ON_PL_PLANNING,
    ON_DL_APPROVING,
    ON_DIRECTOR_APPROVING,
    DIRECTOR_APPROVED,
    DONE;

    public static ProjectStatus getByName(String name) {
        return Arrays.stream(values())
                .filter(value -> value.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new DomainException("Invalid status: " + name, HttpStatus.BAD_REQUEST.value()));
    }
}