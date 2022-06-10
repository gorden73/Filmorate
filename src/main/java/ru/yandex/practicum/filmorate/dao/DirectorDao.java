package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorDao {

    Optional<Director> create(Director entity);

    Collection<Director> getAllFilms();

    Optional<Director> getDirector(Integer id);

    Optional<Director> update(Director entity);

    void delete(Integer id);
}
