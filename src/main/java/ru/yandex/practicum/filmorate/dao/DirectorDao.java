package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface DirectorDao {

    Optional<Director> create(Director entity);

    Collection<Director> getAllDirectors();

    Collection<Director> getAllDirectorsById(Integer filmId);

    Optional<Director> getDirector(Integer id);

    Optional<Director> update(Director entity);

    void delete(Integer id);
}
