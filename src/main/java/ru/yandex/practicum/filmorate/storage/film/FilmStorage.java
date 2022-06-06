package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> allFilms();

    Film add(Film film);

    Film update(Film film);

    Integer remove(Integer id);

    Film getFilm(Integer id);

    Collection<Film> getPopularFilms(Integer count);

    Integer addLike(Integer filmId, Integer userId);

    Integer removeLike(Integer filmId, Integer userId);
}
