package ru.yandex.practicum.filmorate.dao;

public interface ReviewLikeDao {
    Integer addLike(Integer userId, Integer reviewId);

    Integer addDislike(Integer userId, Integer reviewId);

    Integer delete(Integer userId, Integer reviewId);
}
