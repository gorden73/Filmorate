package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendDao friendDao;
    private static final String SQL_GET_USERS = "SELECT * FROM users";
    private static final String SQL_GET_FRIENDS = "SELECT friend_id FROM friends WHERE user_id = ?";
    private static final String SQL_GET_FRIEND_STATUS = "SELECT * FROM friends WHERE user_id = ?";
    private static final String SQL_GET_LIKED_FILMS = "SELECT film_id FROM likes WHERE user_id = ?";
    private static final String SQL_ADD_USERS = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String SQL_GET_USER_ID = "SELECT user_id FROM users WHERE email = ? and " +
            "login = ? and name = ? and birthday = ?";
    private static final String SQL_GET_UPDATED_USER = "UPDATE users SET email = ?, login = ?, " +
            "name = ?, birthday = ? WHERE user_id = ?";
    private static final String SQL_DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendDao friendDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendDao = friendDao;
    }

    @Override
    public Map<Integer, User> getAllUsers() {
        Map<Integer, User> userMap = new HashMap<>();
        List<User> userList = jdbcTemplate.query(SQL_GET_USERS, (rs, rowNum) -> makeUser(rs));
        for (User u : userList) {
            userMap.put(u.getId(), u);
        }
        log.debug("Запрошен список всех пользователей.");
        return userMap;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        HashSet<Integer> friends = new HashSet<>(jdbcTemplate.query(SQL_GET_FRIENDS,
                (rs1, rowNum) -> (rs1.getInt("friend_id")), id));
        HashMap<Integer, Boolean> friendStatus = new HashMap<>();
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(SQL_GET_FRIEND_STATUS, id);
        if (friendsRows.next()) {
            friendStatus.put(friendsRows.getInt("friend_id"),
                    friendsRows.getBoolean("status"));
        }
        HashSet<Integer> likedFilms = new HashSet<>(jdbcTemplate.query(SQL_GET_LIKED_FILMS,
                (rs3, rowNum) -> (rs3.getInt("film_id")), id));
        return new User(id, email, login, name, birthday, friends, friendStatus, likedFilms);
    }

    @Override
    public User addUser(User user) {
        jdbcTemplate.update(SQL_ADD_USERS, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday());
        log.debug("Добавлен новый пользователь {}.", user);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SQL_GET_USER_ID, user.getEmail(),
                user.getLogin(),
                user.getName(), user.getBirthday());
        if (userRows.next()) {
            return new User(userRows.getInt("user_id"), user.getEmail(), user.getLogin(),
                    user.getName(), user.getBirthday());
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(SQL_GET_UPDATED_USER, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        log.debug("Обновлен пользователь {}.", user.getId());
        return user;
    }

    @Override
    public Integer removeUser(Integer id) {
        jdbcTemplate.update(SQL_DELETE_USER, id);
        log.debug("Удален пользователь {}", id);
        return id;
    }

    @Override
    public User addToFriends(Integer id, Integer friendId) {
        return friendDao.addToFriends(id, friendId);
    }

    @Override
    public Integer removeFromFriends(Integer id, Integer removeFromId) {
        return friendDao.removeFromFriends(id, removeFromId);
    }

    @Override
    public Collection<User> getUserFriends(Integer id) {
        return friendDao.getUserFriends(id);
    }

    @Override
    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        return friendDao.getMutualFriends(id, id1);
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return jdbcTemplate
                .query(SQL_GET_USER_BY_ID, (rs, rowNum) -> makeUser(rs), id)
                .stream()
                .findFirst();
    }
}
