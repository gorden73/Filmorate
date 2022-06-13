package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
public class Director {

    private Integer id;
    @NotNull(message = "Имя не может быть пустое")
    private String name;

}
