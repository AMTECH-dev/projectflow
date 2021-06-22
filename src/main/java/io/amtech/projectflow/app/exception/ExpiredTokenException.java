package io.amtech.projectflow.app.exception;

import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends DomainException {
    public ExpiredTokenException(String token) {
        super(String.format("Token %s is expired", token), HttpStatus.UNAUTHORIZED.value());
    }
}
