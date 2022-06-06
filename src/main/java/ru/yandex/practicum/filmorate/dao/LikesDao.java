package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
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

    public Collection<Film> getPopularFilms(Integer count) {
        String sqlGetTopFilms = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, f.mpa, " +
                "l.user_id " +
                "FROM likes AS l " +
                "RIGHT JOIN films AS f ON f.film_id = l.film_id " +
                "GROUP BY f.film_id, l.user_id " +
                "ORDER BY COUNT(l.user_id) DESC\n" +
                "LIMIT ?";
        Collection<Film> films = jdbcTemplate.query(sqlGetTopFilms, (rs, rowNum) -> makeFilm(rs), count);
        if (films.isEmpty()) {
            String sqlFilms = "SELECT * FROM films";
            return jdbcTemplate.query(sqlFilms, (rs, rowNum) -> makeFilm(rs), count);
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
        String sqlLikes = "SELECT user_id FROM likes WHERE film_id = ?";
        Set<Integer> likes = new HashSet<>(jdbcTemplate.query(sqlLikes,
                (rs1, rowNum1) -> (rs1.getInt("user_id")), id));
        String sqlGenres = "SELECT genre_id FROM film_genre WHERE film_id = ?";
        List<Integer> genres = new ArrayList<>(jdbcTemplate.query(sqlGenres,
                (rs2, rowNum) -> (rs2.getInt("genre_id")), id));
        return new Film(id, name, description, releaseDate, duration, new Mpa(mpa), likes, genres);
    }
}
