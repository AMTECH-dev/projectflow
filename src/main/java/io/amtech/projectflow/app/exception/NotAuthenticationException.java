package io.amtech.projectflow.app.exception;

import io.amtech.projectflow.app.exception.DomainException;
import org.springframework.http.HttpStatus;

public class NotAuthenticationException extends DomainException {
    public NotAuthenticationException() {
        super("User is not authenticate", HttpStatus.UNAUTHORIZED.value());
    }
}
