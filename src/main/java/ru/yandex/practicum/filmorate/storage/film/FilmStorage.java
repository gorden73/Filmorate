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

    Collection<Film> getPopularFilms(Integer count);

    Integer addLike(Integer filmId, Integer userId);

    Integer removeLike(Integer filmId, Integer userId);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId, String sort, Integer count);
}
