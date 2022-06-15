package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Collection;

@Slf4j
@Repository
public class ReviewDao {
    private final JdbcTemplate jdbcTemplate;
    private final static String GET_REVIEW_BY_ID_QUERY =
            "SELECT r.id, " +
            "       r.user_id, " +
            "       r.film_id, " +
            "       r.content, " +
            "       r.is_positive, " +
            "       SUM(CASE WHEN rl.is_like = true THEN 1 ELSE 0 END) - " +
            "       SUM(CASE WHEN rl.is_like = false THEN 1 ELSE 0 END) AS useful " +
            "FROM reviews AS r " +
            "LEFT JOIN review_like AS rl ON r.id = rl.review_id " +
            "WHERE r.id = ? " +
            "GROUP BY r.id;";
    private final static String GET_ALL_REVIEWS_QUERY =
            "SELECT r.id, " +
            "       r.user_id, " +
            "       r.film_id, " +
            "       r.content, " +
            "       r.is_positive, " +
            "       SUM(CASE WHEN rl.is_like = true THEN 1 ELSE 0 END) - " +
            "       SUM(CASE WHEN rl.is_like = false THEN 1 ELSE 0 END) AS useful " +
            "FROM reviews AS r " +
            "LEFT JOIN review_like AS rl ON r.id = rl.review_id " +
            "GROUP BY r.id " +
            "ORDER BY useful DESC " +
            "LIMIT ?;";
    private final static String GET_REVIEWS_BY_FILM_QUERY =
            "SELECT r.id, " +
            "       r.user_id, " +
            "       r.film_id, " +
            "       r.content, " +
            "       r.is_positive, " +
            "       SUM(CASE WHEN rl.is_like = true THEN 1 ELSE 0 END) - " +
            "       SUM(CASE WHEN rl.is_like = false THEN 1 ELSE 0 END) AS useful " +
            "FROM reviews AS r " +
            "LEFT JOIN review_like AS rl ON r.id = rl.review_id " +
            "WHERE r.film_id = ? " +
            "GROUP BY r.id " +
            "ORDER BY useful DESC " +
            "LIMIT ?;";
    private final static String ADD_REVIEW_QUERY =
            "INSERT INTO reviews (user_id, film_id, content, is_positive) " +
            "VALUES (?, ?, ?, ?);";
    private final static String UPDATE_REVIEW_QUERY =
            "UPDATE reviews " +
            "SET content = ?, " +
            "    is_positive = ? " +
            "WHERE id = ?;";
    private final static String DELETE_REVIEW_BY_ID_QUERY = "DELETE FROM reviews WHERE id = ?;";

    @Autowired
    public ReviewDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Review getReviewById(Integer id) {
        try {
            return jdbcTemplate.queryForObject(GET_REVIEW_BY_ID_QUERY, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            log.warn("Не найден отзыв {}.", id);
            throw new ElementNotFoundException("отзыв " + id);
        }
    }

    public Collection<Review> getAllReviews(Integer count) {
        return jdbcTemplate.query(GET_ALL_REVIEWS_QUERY, this::mapRowToReview, count);
    }

    public Collection<Review> getReviewsByFilmId(Integer filmId, Integer count) {
        return jdbcTemplate.query(GET_REVIEWS_BY_FILM_QUERY, this::mapRowToReview, filmId, count);
    }

    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(ADD_REVIEW_QUERY,
                    new String[]{"id"});
            statement.setInt(1, review.getUserId());
            statement.setInt(2, review.getFilmId());
            statement.setString(3, review.getContent());
            statement.setBoolean(4, review.getIsPositive());
            return statement;
        }, keyHolder);
        int id = keyHolder.getKey().intValue();
        log.debug("Добавлен новый отзыв {}.", id);
        return getReviewById(id);
    }

    public Review updateReview(Review newReview) {
        jdbcTemplate.update(UPDATE_REVIEW_QUERY, newReview.getContent(),
                            newReview.getIsPositive(), newReview.getId());
        log.debug("Обновлен отзыв {}.", newReview.getId());
        return getReviewById(newReview.getId());
    }

    public Integer deleteReviewById(Integer id) {
        jdbcTemplate.update(DELETE_REVIEW_BY_ID_QUERY, id);
        log.debug("Удален отзыв {}.", id);
        return id;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("id");
        int userId = resultSet.getInt("user_id");
        int filmId = resultSet.getInt("film_id");
        String content = resultSet.getString("content");
        boolean isPositive = resultSet.getBoolean("is_positive");
        int useful = resultSet.getInt("useful");
        return new Review(id, content, isPositive, userId, filmId, useful);
    }
}
