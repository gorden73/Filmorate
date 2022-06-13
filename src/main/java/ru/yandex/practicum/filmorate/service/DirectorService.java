package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class DirectorService {
    private final DirectorDao storage;

    @Autowired
    public DirectorService(DirectorDao storage) {
        this.storage = storage;
    }

    public Optional<Director> createDirector(Director director) {
        log.info(String.format("Создан режиссер %s", director));
        return storage.create(director);
    }

    public Optional<Director> updateDirector(Director director) {
        findDirectorById(director.getId());
        log.info(String.format("Обновлен режиссер %s", director));
        return storage.update(director);
    }

    public Collection<Director> getAllDirectors() {
        Collection<Director> directorList = storage.getAllDirectors();
        log.info(String.format("Количество режиссеров %d", directorList.size()));
        return directorList;
    }

    public Collection<Director> getAllDirectorsById(Integer id) {
        Collection<Director> directorList = storage.getAllDirectorsById(id);
        log.info(String.format("Количество режиссеров %d у фильма %d", directorList.size(), id));
        return directorList;
    }

    public Optional<Director> findDirectorById(Integer id) {
        final Optional<Director> optionalDirector = storage.getDirector(id);
        if (optionalDirector.isEmpty()) {
            throw new ElementNotFoundException(String.format("Режиссер c ID %s не найден", id));
        }
        return storage.getDirector(id);
    }

    public Integer removeDirector(Integer id) {
        findDirectorById(id);
        log.info("Удален режиссер: {}", id);
        return storage.delete(id);
    }

    public Integer addFilmDirector(Integer directorId, Integer filmId) {
        return storage.addFilmDirector(directorId, filmId);
    }
}
