package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorDao {

    Optional<Director> create(Director entity);

    Collection<Director> getAllDirectors();

    Collection<Director> getAllDirectorsById(Integer filmId);

    Optional<Director> getDirector(Integer id);

    Integer addDirector(Integer directorId, Integer filmId);

    Optional<Director> update(Director entity);

    Integer delete(Integer id);

}
