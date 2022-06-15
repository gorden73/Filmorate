package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@EqualsAndHashCode
public class Review {
    private Integer reviewId;
    private Integer userId;
    private Integer filmId;
    private String content;
    @JsonProperty("isPositive")
    private boolean isPositive;
    private Integer useful;

    public Review(Integer reviewId, Integer userId, Integer filmId, String content,
                  boolean isPositive, Integer useful) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.filmId = filmId;
        this.content = content;
        this.isPositive = isPositive;
        this.useful = useful;
    }
}
