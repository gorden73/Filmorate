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
        log.info(String.format("Добавлен пользователь %s", director));
        return storage.create(director);
    }

    public Optional<Director> updateDirector(Director director) {
        findDirectorById(director.getId());
        log.info(String.format("Обновлен директор %s", director));
        return storage.update(director);
    }

    public Collection<Director> getAllDirectors() {
        return storage.getAllDirectors();
    }

    public Optional<Director> findDirectorById(Integer id) {
        final Optional<Director> optionalDirector = storage.getDirector(id);
        if (optionalDirector.isEmpty()) {
            throw new ElementNotFoundException(String.format("Директор c ID %s не найден", id));
        }
        return storage.getDirector(id);
    }

    public void removeDirector(Integer id) {
        findDirectorById(id);
        storage.delete(id);
    }
}
