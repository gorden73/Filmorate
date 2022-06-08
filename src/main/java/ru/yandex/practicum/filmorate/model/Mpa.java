package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Mpa {
    private int id;
    private String name;

    @JsonCreator
    public Mpa(@JsonProperty("id") int id) {
        this.id = id;
        this.name = TypeOfMpa.values()[id - 1].getTitle();
    }
}
