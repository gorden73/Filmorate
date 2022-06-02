package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikesDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer addLike(Integer filmId, Integer userId) {
        String sqlAddLike = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddLike, userId, filmId);
        return filmId;
    }
}
