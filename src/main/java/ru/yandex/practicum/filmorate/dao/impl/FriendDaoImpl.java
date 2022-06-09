package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.FriendDao;
import ru.yandex.practicum.filmorate.exceptions.ElementNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
@Slf4j
public class FriendDaoImpl implements FriendDao {
    private final JdbcTemplate jdbcTemplate;
    private static final String SQL_ADD_TO_FRIENDS = "INSERT INTO friends(user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String SQL_CHANGE_STATUS = "UPDATE friends SET status = ? WHERE " +
            "user_id = ? AND friend_id = ?";
    private static final String SQL_GET_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String SQL_REMOVE_LIKE = "DELETE FROM friends WHERE user_id = ? AND " +
            "friend_id = ?";
    private static final String SQL_CHECK_FRIEND = "SELECT friend_id FROM friends WHERE " +
            "user_id = ? AND friend_id = ?";
    private static final String SQL_GET_USER_FRIENDS = "SELECT f.friend_id, u.user_id, u.email, " +
            "u.login, u.name, u.birthday FROM friends AS f INNER JOIN users AS u ON " +
            "f.friend_id = u.user_id  WHERE f.user_id = ? GROUP BY f.friend_id";
    private static final String SQL_GET_MUTUAL_FRIENDS = "SELECT f.friend_id, u.user_id, u.email," +
            " u.login, u.name, u.birthday FROM friends AS f LEFT JOIN friends AS fr ON " +
            "f.friend_id = fr.friend_id INNER JOIN users AS u ON f.friend_id = u.user_id WHERE " +
            "f.user_id = ? AND fr.user_id = ? GROUP BY f.friend_id";
    private static final String SQL_GET_FRIENDS = "SELECT friend_id FROM friends WHERE user_id = ?";
    private static final String SQL_GET_FRIENDS_STATUS = "SELECT * FROM friends WHERE user_id = ?";
    private static final String SQL_GET_LIKED_FILMS = "SELECT film_id FROM likes WHERE user_id = ?";

    @Autowired
    public FriendDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User addToFriends(Integer id, Integer friendId) {
        jdbcTemplate.update(SQL_ADD_TO_FRIENDS, id, friendId);
        if (checkFriend(id, friendId)) {
            jdbcTemplate.update(SQL_CHANGE_STATUS, true, id, friendId);
            jdbcTemplate.update(SQL_CHANGE_STATUS, true, friendId, id);
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet(SQL_GET_USER_BY_ID, friendId);
        if (userRows.next()) {
            log.debug("Пользователь {} отправил заявку в друзья пользователю {}.", id, friendId);
            return new User(userRows.getInt("user_id"), userRows.getString("email"),
                    userRows.getString("login"), userRows.getString("name"),
                    userRows.getDate("birthday").toLocalDate());
        }
        return null;
    }

    public Integer removeFromFriends(Integer id, Integer friendId) {
        if (!checkFriend(friendId, id)) {
            throw new ElementNotFoundException("пользователь " + friendId);
        }
        jdbcTemplate.update(SQL_REMOVE_LIKE, id, friendId);
        if (checkFriend(id, friendId)) {
            jdbcTemplate.update(SQL_CHANGE_STATUS, false, friendId, id);
        }
        log.debug("Пользователь {} удалил из друзей пользователя {}.", id, friendId);
        return friendId;
    }

    private boolean checkFriend(Integer id, Integer friendId) {
        List<Integer> friends = jdbcTemplate.query(SQL_CHECK_FRIEND, (rs, rowNum) ->
                        rs.getInt("friend_id"), friendId, id);
        if (friends.contains(id)) {
            return true;
        }
        return false;
    }

    public Collection<User> getUserFriends(Integer id) {
        log.debug("Запрошен список друзей пользователя {}.", id);
        return jdbcTemplate.query(SQL_GET_USER_FRIENDS, (rs, rowNum) -> makeUser(rs), id);
    }

    public Collection<User> getMutualFriends(Integer id, Integer id1) {
        log.debug("Запрошен список общих друзей пользователя {} и {}.", id, id1);
        return jdbcTemplate.query(SQL_GET_MUTUAL_FRIENDS, (rs, rowNum) -> makeUser(rs), id, id1);
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
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(SQL_GET_FRIENDS_STATUS, id);
        if (friendsRows.next()) {
            friendStatus.put(friendsRows.getInt("friend_id"),
                    friendsRows.getBoolean("status"));
        }
        HashSet<Integer> likedFilms = new HashSet<>(jdbcTemplate.query(SQL_GET_LIKED_FILMS,
                (rs3, rowNum) -> (rs3.getInt("film_id")), id));
        return new User(id, email, login, name, birthday, friends, friendStatus, likedFilms);
    }
}
