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
public class Genre {
    private int id;
    private String name;

    @JsonCreator
    public Genre(@JsonProperty("id") int id) {
        this.id = id;
        this.name = TypeOfGenre.values()[id - 1].getTitle();
    }
}
