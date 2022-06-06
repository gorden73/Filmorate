package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
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

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendDao friendDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendDao = friendDao;
    }

    @Override
    public Map<Integer, User> allUsers() {
        String sql = "SELECT * FROM users";
        Map<Integer, User> userMap = new HashMap<>();
        List<User> userList = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
        for (User u : userList) {
            userMap.put(u.getId(), u);
        }
        log.debug("Запрошен список пользователей.");
        return userMap;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        String sqlFriends = "SELECT friend_id FROM friends WHERE user_id = ?";
        HashSet<Integer> friends = new HashSet<>(jdbcTemplate.query(sqlFriends,
                (rs1, rowNum) -> (rs1.getInt("friend_id")), id));
        HashMap<Integer, Boolean> friendStatus = new HashMap<>();
        String sqlFriendStatus = "SELECT * FROM friends WHERE user_id = ?";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlFriendStatus, id);
        if (friendsRows.next()) {
            friendStatus.put(friendsRows.getInt("friend_id"), friendsRows.getBoolean("status"));
        }
        String sqlLikedFilms = "SELECT film_id FROM likes WHERE user_id = ?";
        HashSet<Integer> likedFilms = new HashSet<>(jdbcTemplate.query(sqlLikedFilms,
                (rs3, rowNum) -> (rs3.getInt("film_id")), id));
        return new User(id, email, login, name, birthday, friends, friendStatus, likedFilms);
    }

    @Override
    public User add(User user) {
        String sqlAddUser = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddUser, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        log.debug("Добавлен новый пользователь {}.", user);
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select user_id from users where email = ? and login = ?" +
                        "and name = ? and birthday = ?", user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday());
        if (userRows.next()) {
            return new User(userRows.getInt("user_id"), user.getEmail(), user.getLogin(),
                    user.getName(), user.getBirthday());
        }
        return user;
    }

    @Override
    public User update(User user) {
        String sqlUpdateUser = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sqlUpdateUser, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(),
                user.getId());
        log.debug("Обновлен пользователь {}.", user.getId());
        return user;
    }

    @Override
    public Integer remove(Integer id) {
        String sqlDeleteUser = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sqlDeleteUser, id);
        log.debug("Удален пользователь {}", id);
        return id;
    }

    public User addToFriends(Integer id, Integer friendId) {
        return friendDao.addToFriends(id, friendId);
    }

    public Integer removeFromFriends(Integer id, Integer removeFromId) {
        return friendDao.removeFromFriends(id, removeFromId);
    }

    public Collection<User> getUserFriends(Integer id) {
        return friendDao.getUserFriends(id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        return friendDao.getMutualFriends(id, id1);
    }

    public User getUser(Integer id) {
        String sqlGetUser = "SELECT * FROM users WHERE user_id = ?";
        return jdbcTemplate.query(sqlGetUser, (rs, rowNum) -> makeUser(rs), id).get(0);
    }
}
