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
    private String content;
    @JsonProperty("isPositive")
    private boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private Integer useful;

    public Review(Integer reviewId, String content, boolean isPositive,
                  Integer userId, Integer filmId, Integer useful) {
        this.reviewId = reviewId;
        this.content = content;
        this.isPositive = isPositive;
        this.userId = userId;
        this.filmId = filmId;
        this.useful = useful;
    }
}
