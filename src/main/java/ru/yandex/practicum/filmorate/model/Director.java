package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class Director {

    private Integer id;
    @NotNull(message = "Имя не может быть пустое")
    private String name;

}
