package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class Review {
    private Integer id;
    @NotNull
    private String content;
    @NotNull
    @JsonProperty("isPositive")
    private Boolean isPositive;
    @NotNull
    private Integer userId;
    @NotNull
    private Integer filmId;
    private Integer useful;

    public Review(Integer id, String content, Boolean isPositive,
                  Integer userId, Integer filmId, Integer useful) {
        this.id = id;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
