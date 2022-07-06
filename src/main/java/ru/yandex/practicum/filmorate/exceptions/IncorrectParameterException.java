package ru.yandex.practicum.filmorate.exceptions;

public class IncorrectParameterException extends RuntimeException {

    private final String param;

    public IncorrectParameterException(String param) {
        this.param = param;
    }

    public String getParam() {
        return param;
    }

}
