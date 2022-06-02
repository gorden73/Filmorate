package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikesDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer addLike(Integer filmId, Integer userId) {
        String sqlAddLike = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlAddLike, userId, filmId);
        log.debug("Пользователь {} поставил like фильму {}", userId, filmId);
        return userId;
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        String sqlRemoveLike = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(sqlRemoveLike, userId, filmId);
        log.debug("Пользователь {} удалил свой like фильму {}", userId, filmId);
        return userId;
    }
}
