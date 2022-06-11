package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Integer removeFilm(Integer id);

    Film getFilm(Integer id);

    Collection<Film> getPopularFilms();

    Collection<Film> getPopularFilms(Integer genreId, Integer year);

    Integer addLike(Integer filmId, Integer userId);

    Integer removeLike(Integer filmId, Integer userId);

    Collection<Film> getRecommendations(Integer userId, Integer from, Integer size);

    Collection<Film> getFilmsByDirectorByYear(Integer directorId);

    Collection<Film> getFilmsByDirectorByLikes(Integer directorId);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId, Integer count);
}
