package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Map;

public interface FilmStorage {
    Map<Integer, Film> allFilms();

    Film add(Film film);

    Film update(Film film);

    void remove(Integer id);
}
