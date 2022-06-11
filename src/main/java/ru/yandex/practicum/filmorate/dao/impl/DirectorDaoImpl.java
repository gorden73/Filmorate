package ru.yandex.practicum.filmorate.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDaoImpl implements DirectorDao {

    private static final String SQL_CREATE = "INSERT INTO directors (name) VALUES (?)";
    private static final String SQL_SELECT_ALL = "SELECT id, name, FROM directors";
    private static final String SQL_SELECT_BY_FILM = "SELECT d.id, d.name, FROM directors AS d " +
            "JOIN film_director AS fd ON fd.director_id = d.id " +
            "JOIN films AS f ON f.film_id = fd.film_id " +
            "WHERE f.film_id = ?";
    private static final String SQL_SELECT_ONE = "SELECT id, name " +
                                        "FROM directors WHERE id = ?";
    private static final String SQL_UPDATE = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String SQL_DELETE = "DELETE FROM directors WHERE id = ?";
    private final JdbcTemplate jdbcTemplate;
    private final Logger log = LoggerFactory.getLogger(DirectorDaoImpl.class);

    @Autowired
    public DirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Director> create(Director entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SQL_CREATE,
                    new String[]{"id"});
            stmt.setString(1, entity.getName());
            return stmt;
        }, keyHolder);
        return getDirector(keyHolder.getKey().intValue());
    }

    @Override
    public Collection<Director> getAllDirectors() {
        return jdbcTemplate.query(SQL_SELECT_ALL, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public List<Director> getAllDirectorsById(Integer filmId) {
        return jdbcTemplate.query(SQL_SELECT_BY_FILM, (rs, rowNum) -> makeDirector(rs), filmId);
    }

    @Override
    public Optional<Director> getDirector(Integer id) {
        final Optional<Director> optionalDirector = jdbcTemplate
                .query(SQL_SELECT_ONE, (rs, rowNum) -> makeDirector(rs), id)
                .stream()
                .findFirst();
        if (optionalDirector.isEmpty()) {
            throw new ElementNotFoundException(String.format("Режиссер c ID %s не найден", id));
        }
        final Director director = optionalDirector.get();
        log.info("Найден режиссер: {} {}", director.getId(), director.getName());
        return Optional.of(director);
    }

    @Override
    public Optional<Director> update(Director entity) {
        int count = jdbcTemplate.update(SQL_UPDATE,
                entity.getName(),
                entity.getId());
        Director director = new Director(
                entity.getId(),
                entity.getName());
        if (count == 1) {
            return Optional.of(director);
        }
        return Optional.empty();
    }

    @Override
    public void delete(Integer id) {
        jdbcTemplate.update(SQL_DELETE, id);
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String name = rs.getString("name");
        return new Director(id, name);
    }
}
