package io.amtech.projectflow.rest.general;

import io.amtech.projectflow.app.exception.DomainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class ExceptionResolver {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    ErrorMessage handleAll(final Exception e) {
        e.printStackTrace();
        return new ErrorMessage()
                .setMessage("Internal server error");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorMessage onMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        final ErrorMessage error = new ErrorMessage();
        error.setMessage("Validation error");
        for (FieldError er : e.getFieldErrors()) {
            error.getErrors().put(er.getField(), er.getDefaultMessage());
        }
        return error;
    }

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ErrorMessage> onDomainException(final DomainException e) {
        ErrorMessage errMsg = new ErrorMessage().setMessage(e.getMessage());
        return new ResponseEntity<>(errMsg, Optional.ofNullable(HttpStatus.resolve(e.getCode()))
                .orElse(HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorMessage> onHttpMessageNotReadableException() {
        ErrorMessage errorMessage = new ErrorMessage().setMessage("Position value is not valid");
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
