package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Map<Integer, Film> allFilms() {
        String sql = "SELECT * FROM films";
        Map<Integer, Film> filmMap = new HashMap<>();
        List<Film> filmList = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        for (Film f : filmList) {
            filmMap.put(f.getId(), f);
        }
        log.debug("Запрошен список фильмов.");
        return filmMap;
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

    @Override
    public Film add(Film film) {
        String sqlAddFilm = "INSERT INTO films(name, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddFilm, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId());
        log.debug("Добавлен новый фильм {}.", film);
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("select film_id from films where name = ? and " +
                        "description = ? and release_date = ? and duration = ? and mpa = ?", film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        if (filmRows.next()) {
            if (film.getGenres() == null) {
                return new Film(filmRows.getInt("film_id"), film.getName(), film.getDescription(),
                        film.getReleaseDate(), film.getDuration(), film.getMpa());
            } else {
                String sqlAddGenre = "INSERT INTO film_genre(film_id, genre_id) VALUES (?, ?)";
                for (Integer genre : film.getGenres()) {
                    jdbcTemplate.update(sqlAddGenre, filmRows.getInt("film_id"), genre);
                }
                return new Film(filmRows.getInt("film_id"), film.getName(), film.getDescription(),
                        film.getReleaseDate(), film.getDuration(), film.getMpa(), film.getGenres());
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlUpdateFilm = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sqlUpdateFilm, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId(), film.getId());
        log.debug("Обновлен фильм {}.", film.getId());
        return film;
    }

    @Override
    public Integer remove(Integer id) {
        String sqlDeleteFilm = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(sqlDeleteFilm, id);
        log.debug("Удален фильм {}", id);
        return id;
    }
}
