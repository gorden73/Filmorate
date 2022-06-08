package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Repository
@Slf4j
public class GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_GENRE_BY_ID = "SELECT genre_id FROM genres WHERE genre_id = ?";
    private static final String SQL_GET_ALL_GENRES = "SELECT genre_id FROM genres";

    public GenreDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Genre getGenreById(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_GENRE_BY_ID, id);
        if (rowSet.next()) {
            log.debug("Запрошен жанр {}.", id);
            return new Genre(rowSet.getInt("genre_id"));
        }
        return new Genre(0);
    }

    public Collection<Genre> getAllGenres() {
        log.debug("Запрошены все жанры.");
        return jdbcTemplate.query(SQL_GET_ALL_GENRES, (rs, rowNum) -> new Genre(rs.getInt("genre_id")));
    }
}
