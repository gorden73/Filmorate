package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleElementNotFoundException(ElementNotFoundException e) {

    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus)
    public Map<String, String> handleThrowableException(Throwable e) {
        return
    }
}
