package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode
public class Review {
    @JsonProperty("id")
    private Integer id;
    @NotNull
    @JsonProperty("content")
    private String content;
    @NotNull
    @JsonProperty("is_positive")
    private boolean isPositive;
    @JsonProperty("user_id")
    private Integer userId;
    @NotNull
    @JsonProperty("film_id")
    private Integer filmId;
    @JsonProperty("useful")
    private Integer useful;

    public Review() {
        super();
    }

    @JsonCreator
    public Review(String content, boolean isPositive,
                  Integer userId, Integer filmId, Integer useful) {
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }

    public Review(Integer id, String content, boolean isPositive,
                  Integer userId, Integer filmId, Integer useful) {
        this.id = id;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
