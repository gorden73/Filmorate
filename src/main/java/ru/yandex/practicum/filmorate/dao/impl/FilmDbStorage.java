package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.LikesDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final LikesDao likesDao;
    private final DirectorDao directorDao;
    private static final String SQL_GET_FILMS = "SELECT film_id, name, description, release_date," +
            " duration, mpa  FROM films";
    private static final String SQL_GET_LIKES = "SELECT user_id FROM likes WHERE film_id = ?";
    private static final String SQL_GET_GENRES = "SELECT genre_id FROM film_genre WHERE " +
            "film_id = ?";
    private static final String SQL_ADD_FILM = "INSERT INTO films(name, description, " +
            "release_date, duration, mpa) VALUES (?, ?, ?, ?, ?)";
    private static final String SQL_GET_FILM_ID = "SELECT film_id FROM films WHERE name = ? AND " +
            "description = ? AND release_date = ? AND duration = ? AND mpa = ?";
    private static final String SQL_ADD_GENRE = "INSERT INTO film_genre(film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String SQL_UPDATE_FILM = "UPDATE films SET name = ?, description = ?, " +
            "release_date = ?, " +
            "duration = ?, mpa = ? WHERE film_id = ?";
    private static final String SQL_DELETE_GENRE = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String SQL_UPDATE_GENRE = "INSERT INTO film_genre(film_id, genre_id) " +
            "VALUES(?, ?)";
    private static final String SQL_DELETE_FILM = "DELETE FROM films WHERE film_id = ?";
    private static final String SQL_COMMON_FILMS = "SELECT l.film_id, f.name, description, " +
            "   release_date, duration, mpa " +
            "    FROM likes AS l " +
            "    JOIN films AS f ON f.film_id = l.film_id " +
            "    JOIN mpa AS m ON f.mpa = m.id " +
            "    LEFT JOIN film_genre AS fg ON l.film_id = fg.film_id " +
            "    LEFT JOIN genres AS g ON g.genre_id = fg.genre_id " +
            "    WHERE l.film_id IN ( " +
            "        SELECT film_id FROM likes WHERE user_id = ? " +
            "        INTERSECT " +
            "        SELECT film_id FROM likes WHERE user_id = ? ) " +
            "GROUP BY l.film_id " +
            "ORDER BY COUNT(DISTINCT likes_id) DESC";
    private static final String SQL_GET_FILM = "SELECT * FROM films AS f LEFT JOIN likes AS l " +
            "ON f.film_id = l.film_id WHERE f.film_id = ? GROUP BY f.film_id, l.likes_id";

    private static final String SQL_GET_RECOMMENDATION_FILM = "SELECT film_id, name, description," +
            " release_date, duration, mpa FROM films WHERE film_id IN (SELECT film_id FROM likes" +
            " WHERE user_id IN (SELECT user_id FROM likes WHERE user_id IN (SELECT user_id FROM" +
            " (SELECT user_id, film_id, COUNT(film_id) AS count FROM likes WHERE film_id NOT IN" +
            " (SELECT film_id FROM likes WHERE user_id = ?) AND user_id != ? GROUP BY user_id, " +
            "film_id ) GROUP BY user_id ORDER BY count DESC) GROUP BY user_id ORDER BY " +
            "COUNT(film_id) DESC LIMIT 1) AND film_id NOT IN (SELECT film_id FROM likes WHERE " +
            "user_id = ?))";
    private static final String SQL_SEARCH_FILM = "SELECT f.film_id, f.name, f.description," +
            " f.release_date, f.duration, f.mpa, COUNT(DISTINCT l.user_id) AS amount_likes" +
            " FROM films AS f " +
            " LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            " LEFT JOIN film_director AS fd ON fd.film_id = f.film_id " +
            " LEFT JOIN directors AS d ON d.id = fd.director_id " +
            " WHERE %s " +
            " GROUP BY f.film_id " +
            " ORDER BY amount_likes DESC";
    private static final String SQL_POPULAR_FILM = "SELECT f.film_id AS id, f.name, f.description, " +
            "f.release_date, f.duration AS duration, f.mpa AS mpa " +
            "FROM films AS f " +
            "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
            "LEFT JOIN film_genre AS fg ON fg.film_id = f.film_id " +
            " WHERE %s " +
            " GROUP BY f.film_id " +
            " ORDER BY COUNT(DISTINCT l.user_id) DESC";

    private static final String SQL_GET_FILMS_BY_YEAR = "SELECT f.film_id, f.name, " +
            "f.description, f.release_date AS year, f.duration, f.mpa, d.id FROM films AS f " +
            "JOIN film_director AS fd ON fd.film_id = f.film_id " +
            "JOIN directors AS d ON d.id = fd.director_id " +
            "WHERE d.id = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY year DESC";
    private static final String SQL_GET_FILMS_BY_LIKES = "SELECT f.film_id, f.name, " +
            "f.description, f.release_date, f.duration, f.mpa, d.id, COUNT(l.likes_id) AS likes " +
            "FROM films AS f " +
            "JOIN film_director AS fd ON fd.film_id = f.film_id " +
            "JOIN directors AS d ON d.id = fd.director_id " +
            "LEFT JOIN likes AS l ON l.film_id = f.film_id " +
            "WHERE d.id = ? " +
            "GROUP BY f.film_id " +
            "ORDER BY likes DESC";

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, LikesDao likesDao, DirectorDao directorDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.likesDao = likesDao;
        this.directorDao = directorDao;
    }

    @Override
    public Map<Integer, Film> getAllFilms() {
        Map<Integer, Film> filmMap = new HashMap<>();
        List<Film> filmList = jdbcTemplate.query(SQL_GET_FILMS, (rs, rowNum) -> makeFilm(rs));
        for (Film f : filmList) {
            filmMap.put(f.getId(), f);
        }
        log.debug("Запрошен список всех фильмов.");
        return filmMap;
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
        Set<Genre> genres = new HashSet<>(jdbcTemplate.query(SQL_GET_GENRES, (rs2, rowNum) ->
                (new Genre(rs2.getInt("genre_id"))), id));
        Set<Director> directors = new HashSet<>(directorDao.getAllDirectorsById(id));
        if (genres.isEmpty()) {
            return new Film(id, name, description, releaseDate, duration, new Mpa(mpa), likes,
                    null, directors);
        }
        return new Film(id, name, description, releaseDate, duration, new Mpa(mpa), likes, genres, directors);
    }

    @Override
    public Film addFilm(Film film) {
        jdbcTemplate.update(SQL_ADD_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId());
        log.debug("Добавлен новый фильм {}.", film);
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(SQL_GET_FILM_ID, film.getName(),
                film.getDescription(), film.getReleaseDate(), film.getDuration(),
                film.getMpa().getId());
        if (filmRows.next()) {
            if (film.getGenres() == null) {
                return new Film(filmRows.getInt("film_id"), film.getName(),
                        film.getDescription(), film.getReleaseDate(), film.getDuration(),
                        film.getMpa(), new HashSet<>(), null, null);
            } else {
                for (Genre genre : film.getGenres()) {
                    jdbcTemplate.update(SQL_ADD_GENRE, filmRows.getInt("film_id"),
                            genre.getId());
                }
                return new Film(filmRows.getInt("film_id"), film.getName(),
                        film.getDescription(), film.getReleaseDate(), film.getDuration(),
                        film.getMpa(), film.getGenres());
            }
        }
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(SQL_UPDATE_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());
        log.debug("Обновлен фильм {}.", film.getId());
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            jdbcTemplate.update(SQL_DELETE_GENRE, film.getId());
            return film;
        }
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(SQL_DELETE_GENRE, film.getId());
        }
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(SQL_UPDATE_GENRE, film.getId(), genre.getId());
        }
        return film;
    }

    @Override
    public Integer removeFilm(Integer id) {
        jdbcTemplate.update(SQL_DELETE_FILM, id);
        jdbcTemplate.update(SQL_DELETE_GENRE, id);
        log.debug("Удален фильм {}.", id);
        return id;
    }

    @Override
    public Film getFilm(Integer id) {
        log.debug("Получен фильм {}.", id);
        return jdbcTemplate.query(SQL_GET_FILM, (rs, rowNum) -> makeFilm(rs), id).get(0);
    }

    @Override
    public Collection<Film> getPopularFilms() {
        String formattedSql = String.format(SQL_POPULAR_FILM, "1 = 1");
        return jdbcTemplate.query(formattedSql,
                (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Collection<Film> getPopularFilms(Integer genreId, Integer year) {
        if (genreId != null && year != null) {
            String formattedSql = String.format(SQL_POPULAR_FILM,
                    "fg.genre_id = ? AND YEAR (f.release_date) = ?");
            return jdbcTemplate.query(formattedSql,
                    (rs, rowNum) -> makeFilm(rs), genreId, year);
        }
        if (genreId != null) {
            String formattedSql = String.format(SQL_POPULAR_FILM, "fg.genre_id = ?");
            return jdbcTemplate.query(formattedSql,
                    (rs, rowNum) -> makeFilm(rs), genreId);
        }
        if (year != null) {
            String formattedSql = String.format(SQL_POPULAR_FILM,
                    "YEAR (f.release_date) = ?");
            return jdbcTemplate.query(formattedSql,
                    (rs, rowNum) -> makeFilm(rs), year);
        }
        return getPopularFilms();
    }

    @Override
    public Collection<Film> getFilmsBySearch(String query, String by) {
        if (by.contains("director") && by.contains("title")) {
            String formattedSql = String.format(SQL_SEARCH_FILM,
                    "REPLACE (LOWER (f.name), ' ', '') LIKE ? " +
                            "OR REPLACE (LOWER (d.name), ' ', '') LIKE ? ");
            return jdbcTemplate.query(formattedSql,
                    (rs, rowNum) -> makeFilm(rs), "%%" + query + "%%",
                    "%%" + query + "%%");
        }
        if (by.equals("director")) {
            String formattedSql = String.format(SQL_SEARCH_FILM,
                    "REPLACE (LOWER (d.name), ' ', '') LIKE ? ");
            return jdbcTemplate.query(formattedSql,
                    (rs, rowNum) -> makeFilm(rs), "%%" + query + "%%");
        }
        if (by.equals("title")) {
            String formattedSql = String.format(SQL_SEARCH_FILM,
                    "REPLACE (LOWER (f.name), ' ', '') LIKE ? ");
            return jdbcTemplate.query(formattedSql,
                    (rs, rowNum) -> makeFilm(rs), "%%" + query + "%%");
        }
        return List.of();
    }

    @Override
    public Integer addLike(Integer filmId, Integer userId) {
        return likesDao.addLike(filmId, userId);
    }

    @Override
    public Integer removeLike(Integer filmId, Integer userId) {
        return likesDao.removeLike(filmId, userId);
    }

    @Override
    public Collection<Film> getRecommendations(Integer userId, Integer from, Integer size) {
        Collection<Film> films = jdbcTemplate.query(SQL_GET_RECOMMENDATION_FILM, (rs, rowNum) ->
                        makeFilm(rs), userId, userId, userId).stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
        if (films.isEmpty()) {
            throw new ElementNotFoundException("фильм/фильмы, рекомендованные к просмотру. " +
                    "Похоже Вы уже посмотрели все наиболее популярные фильмы.");
        }
        log.debug("Запрошены рекомендации фильмов пользователю {} в размере {} результатов.",
                userId, size);
        return films;
    }

    @Override
    public Collection<Film> getFilmsByDirectorByYear(Integer directorId) {
        return jdbcTemplate.query(SQL_GET_FILMS_BY_YEAR,
                (rs, rowNum) -> makeFilm(rs), directorId);
    }

    @Override
    public Collection<Film> getFilmsByDirectorByLikes(Integer directorId) {
        return jdbcTemplate.query(SQL_GET_FILMS_BY_LIKES,
                (rs, rowNum) -> makeFilm(rs), directorId);
    }

    @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId, Integer count) {
        return jdbcTemplate.query(SQL_COMMON_FILMS,
                (rs, rowNum) -> makeFilm(rs), userId, friendId);
    }
}
