package ru.yandex.practicum.filmorate.dao;

public interface LikesDao {
    Integer addLike(Integer filmId, Integer userId);

    Integer removeLike(Integer filmId, Integer userId);
}
