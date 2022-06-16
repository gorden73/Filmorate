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
    @JsonProperty("isPositive")
    private Boolean isPositive;
    @NotNull
    @JsonProperty("userId")
    private Integer userId;
    @NotNull
    @JsonProperty("filmId")
    private Integer filmId;
    @JsonProperty("useful")
    private Integer useful;

    public Review() {
        super();
    }

    @JsonCreator
    public Review(String content, Boolean isPositive,
                  Integer userId, Integer filmId, Integer useful) {
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }

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
