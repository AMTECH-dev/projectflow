package io.amtech.projectflow.app.exception;

import lombok.Getter;

@Getter
public class DomainException extends RuntimeException {
    private final int code;

    public DomainException(String message, int code) {
        super(message);
        this.code = code;
    }
}
