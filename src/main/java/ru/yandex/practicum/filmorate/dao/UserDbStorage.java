package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        LocalDate birthday = rs.getDate("release_date").toLocalDate();
        String sqlFriends = "SELECT friend_id FROM friends WHERE user_id = ?";
        HashSet<Integer> friends = new HashSet<>(jdbcTemplate.query(sqlFriends, (rs1, rowNum) -> (rs1.getInt(id))));
        HashMap<Integer, Boolean> friendStatus = new HashMap<>();
        String sqlFriendStatus = "SELECT * FROM friends WHERE user_id = ?";
        SqlRowSet friendsRows = jdbcTemplate.queryForRowSet(sqlFriendStatus, id);
        if (friendsRows.next()) {
            friendStatus.put(friendsRows.getInt("friend_id"), friendsRows.getBoolean("status"));
        }
        String sqlLikedFilms = "SELECT film_id FROM likes WHERE user_id = ?";
        HashSet<Integer> likedFilms = new HashSet<>(jdbcTemplate.query(sqlLikedFilms, (rs3, rowNum) -> (rs3.getInt(id))));
        return new User(id, email, login, name, birthday, friends, friendStatus, likedFilms);
    }

    @Override
    public User add(User user) {
        String sqlAddUser = "INSERT INTO users(email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sqlAddUser, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday());
        log.debug("Добавлен новый пользователь {}.", user);
        return user;
    }

    @Override
    public User update(User user) {
        return user;
    }

    @Override
    public void remove(Integer id) {

    }
}
