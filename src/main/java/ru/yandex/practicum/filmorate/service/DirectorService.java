package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

@Service
@Slf4j
public class DirectorService {
    private final DirectorDao storage;

    @Autowired
    public DirectorService(DirectorDao storage) {
        this.storage = storage;
    }

    public Director createDirector(Director director) {
        log.info(String.format("Создан режиссер %s", director.getName()));
        return storage.create(director).orElseThrow(() -> (new ElementNotFoundException(
                String.format("Режиссер c ID %s не найден", director.getId()))));
    }

    public Director updateDirector(Director director) {
        findDirectorById(director.getId());
        log.info(String.format("Обновлен режиссер %s", director.getName()));
        return storage.update(director).orElseThrow(() -> (new ElementNotFoundException(
                String.format("Режиссер c ID %s не найден", director.getId()))));
    }

    public Collection<Director> getAllDirectors() {
        Collection<Director> directorList = storage.getAllDirectors();
        log.info(String.format("Количество режиссеров %d", directorList.size()));
        return directorList;
    }

    public Director findDirectorById(Integer id) {
        return storage.getDirector(id).orElseThrow(() -> (new ElementNotFoundException(
                String.format("Режиссер c ID %s не найден", id))));
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
