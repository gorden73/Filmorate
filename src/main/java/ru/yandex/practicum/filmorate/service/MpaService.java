package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.TypeOfMpa;

import java.util.Collection;

@Service
public class MpaService {
    private MpaDao mpaDao;

    @Autowired
    public MpaService(MpaDao mpaDao) {
        this.mpaDao = mpaDao;
    }

    public Mpa getMpaById(Integer id) {
        if (id > TypeOfMpa.values().length - 1 || id < 1) {
            throw new ElementNotFoundException(String.format("id %d", id));
        }
        return mpaDao.getMpaById(id);
    }

    public Collection<Mpa> getAllMpa() {
        return mpaDao.getAllMpa();
    }
}
