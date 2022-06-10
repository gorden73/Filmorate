package ru.yandex.practicum.filmorate.dao.impl;

import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public class DirectorDaoImpl implements DirectorDao {

    @Override
    public Optional<Director> create(Director entity) {
        return Optional.empty();
    }

    @Override
    public Collection<Director> getAllFilms() {
        return null;
    }

    @Override
    public Optional<Director> getDirector(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<Director> update(Director entity) {
        return Optional.empty();
    }

    @Override
    public void delete(Integer id) {

    }
}
