package io.amtech.projectflow.app.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends DomainException {
    public InvalidPasswordException(String password) {
        super("Password is invalid " + password, HttpStatus.BAD_REQUEST.value());
    }
}
