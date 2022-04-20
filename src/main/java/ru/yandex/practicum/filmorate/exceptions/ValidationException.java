package ru.yandex.practicum.filmorate.exceptions;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String s) {
        super(s);
    }
}
