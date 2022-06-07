package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Mpa {
    private int id;
    private TypeOfMpa mpa;

    @JsonCreator
    public Mpa(@JsonProperty("id") int id) {
        this.id = id;
        this.mpa = TypeOfMpa.values()[id-1];
    }
}
