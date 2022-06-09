package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Repository
@Slf4j
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_GET_MPA_BY_ID = "SELECT id FROM mpa WHERE id = ?";
    private static final String SQL_GET_ALL_MPA = "SELECT id FROM mpa";

    @Autowired
    public MpaDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Mpa getMpaById(Integer id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(SQL_GET_MPA_BY_ID, id);
        if (rowSet.next()) {
            log.debug("Запрошен рейтинг {}.", id);
            return new Mpa(rowSet.getInt("id"));
        }
        return new Mpa(0);
    }

    public Collection<Mpa> getAllMpa() {
        log.debug("Запрошены все рейтинги.");
        return jdbcTemplate.query(SQL_GET_ALL_MPA, (rs, rowNum) -> new Mpa(rs.getInt("id")));
    }
}
