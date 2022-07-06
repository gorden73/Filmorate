package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {
    Genre getGenreById(Integer id);

    Collection<Genre> getAllGenres();
}
