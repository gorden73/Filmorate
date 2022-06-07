package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.TypeOfGenre;

import java.util.Collection;

@Service
public class GenreService {
    private GenreDao genreDao;

    @Autowired
    public GenreService(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    public Genre getGenreById(Integer id) {
        if (id > TypeOfGenre.values().length - 1 || id < 1) {
            throw new ElementNotFoundException(String.format("id %d", id));
        }
        return genreDao.getGenreById(id);
    }

    public Collection<Genre> getAllGenres() {
        return genreDao.getAllGenres();
    }
}
