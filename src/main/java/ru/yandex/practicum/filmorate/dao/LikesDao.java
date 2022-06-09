package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class LikesDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_ADD_LIKE = "INSERT INTO likes(user_id, film_id) VALUES (?, ?)";
    private static final String SQL_REMOVE_LIKE = "DELETE FROM likes WHERE user_id = ? AND film_id = ?";
    private static final String SQL_GET_TOP_FILMS = "SELECT f.film_id, f.name, f.description, f.release_date, " +
            "f.duration, f.mpa, l.user_id FROM likes AS l RIGHT JOIN films AS f ON f.film_id = l.film_id " +
            "GROUP BY f.film_id, l.user_id ORDER BY COUNT(l.user_id) DESC LIMIT ?";
    private static final String SQL_GET_FILMS = "SELECT * FROM films";
    private static final String SQL_GET_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String SQL_GET_GENRES = "SELECT genre_id FROM film_genre WHERE film_id = ?";
    private static final String SQL_GET_RECOMMENDATION_FILM = "SELECT * FROM films WHERE film_id IN (SELECT film_id " +
            "FROM likes WHERE user_id IN (SELECT user_id FROM likes WHERE user_id IN (SELECT user_id FROM " +
            "(SELECT user_id, film_id, COUNT(film_id) AS count FROM likes WHERE film_id NOT IN (SELECT film_id " +
            "FROM likes WHERE user_id = ?) AND user_id != ? GROUP BY user_id, film_id ) GROUP BY user_id " +
            "ORDER BY count DESC) GROUP BY user_id ORDER BY COUNT(film_id) DESC LIMIT 1) AND film_id NOT IN " +
            "(SELECT film_id FROM likes WHERE user_id = ?))";

    @Autowired
    public LikesDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer addLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_ADD_LIKE, userId, filmId);
        log.debug("Пользователь {} поставил like фильму {}", userId, filmId);
        return userId;
    }

    public Integer removeLike(Integer filmId, Integer userId) {
        jdbcTemplate.update(SQL_REMOVE_LIKE, userId, filmId);
        log.debug("Пользователь {} удалил свой like фильму {}", userId, filmId);
        return userId;
    }

    public Collection<Film> getPopularFilms(Integer count) {
        Collection<Film> films = jdbcTemplate.query(SQL_GET_TOP_FILMS, (rs, rowNum) -> makeFilm(rs), count);
        if (films.isEmpty()) {
            return jdbcTemplate.query(SQL_GET_FILMS, (rs, rowNum) -> makeFilm(rs), count);
        }
        return films;
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        int mpa = rs.getInt("mpa");
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(SQL_GET_LIKES, (rs1, rowNum1) ->
                (rs1.getInt("user_id")), id));
        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(SQL_GET_GENRES,
                (rs2, rowNum) -> (new Genre(rs2.getInt("genre_id"))), id));
        return new Film(id, name, description, releaseDate, duration, new Mpa(mpa), likes, genres);
    }

    public Collection<Film> getRecommendations(Integer userId, Integer from, Integer size) {
        Collection<Film> films = jdbcTemplate.query(SQL_GET_RECOMMENDATION_FILM, (rs, rowNum) -> makeFilm(rs), userId,
                userId, userId).stream().skip(from).limit(size).collect(Collectors.toList());
        if (films.isEmpty()) {
            throw new ElementNotFoundException("фильм/фильмы, рекомендованные к просмотру. Похоже Вы уже посмотрели " +
                    "все наиболее популярные фильмы.");
        }
        return films;
    }
}
