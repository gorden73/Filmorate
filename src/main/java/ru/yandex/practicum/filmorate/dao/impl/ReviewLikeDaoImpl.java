package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewLikeDao;

@Slf4j
@Repository
public class ReviewLikeDaoImpl implements ReviewLikeDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String ADD_LIKE_QUERY =
            "MERGE INTO review_like (user_id, review_id, is_like) " +
                    "VALUES (?, ?, true);";
    private final static String ADD_DISLIKE_QUERY =
            "MERGE INTO review_like (user_id, review_id, is_like) " +
                    "VALUES (?, ?, false);";
    private final static String DELETE_QUERY =
            "DELETE FROM review_like WHERE user_id = ? AND review_id = ?;";

    @Autowired
    public ReviewLikeDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Integer addLike(Integer userId, Integer reviewId) {
        jdbcTemplate.update(ADD_LIKE_QUERY, userId, reviewId);
        log.debug("Пользователь {} поставил like отзыву {}.", userId, reviewId);
        return reviewId;
    }

    @Override
    public Integer addDislike(Integer userId, Integer reviewId) {
        jdbcTemplate.update(ADD_DISLIKE_QUERY, userId, reviewId);
        log.debug("Пользователь {} поставил dislike отзыву {}.", userId, reviewId);
        return reviewId;
    }

    @Override
    public Integer delete(Integer userId, Integer reviewId) {
        jdbcTemplate.update(DELETE_QUERY, userId, reviewId);
        log.debug("Пользователь {} удалил свой like отзыву {}.", userId, reviewId);
        return reviewId;
    }
}
