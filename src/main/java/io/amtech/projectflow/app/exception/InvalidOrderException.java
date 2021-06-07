package io.amtech.projectflow.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidOrderException extends DomainException {
    private final String order;

    public InvalidOrderException(final String order) {
        super("Invalid order field: " + order, HttpStatus.BAD_REQUEST.value());
        this.order = order;
    }
}
